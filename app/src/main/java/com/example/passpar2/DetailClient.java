package com.example.passpar2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailClient extends MenuActivity {

    /**
     * File d'attente pour les requêtes API (en lien avec l'utilisation de Volley)
     */
    private RequestQueue fileRequete;

    /**
     * URL de l'API à interroger
     */
    private static String URL_CONTACTS = "https://2bet.fr/api/contacts?customer=";

    private static final String URL_DELETE = "https://2bet.fr/api/contacts/";

    /**
     * URL de l'API pour les informations du customer
     */
    private static final String URL_CUSTOMER = "https://2bet.fr/api/customers/";

    private ActivityResultLauncher<Intent> lanceurFille;

    private ActivityResultLauncher<Intent> lanceurAdapter;

    private RecyclerView recyclerView;
    private Contacts_RecyclerView adapter;
    private List<String> contacts;

    private List<Integer> idContacts;

    private AppCompatButton boutonModifier;

    private TextView nameView;

    private TextView descriptionView;

    private String idCustomer;

    private TextView addressView;

    private String name;
    private String description;
    private String country;
    private String street;
    private String supplement;
    private String postalCode;
    private String city;
    private String idAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_client);

        // Configuration de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // Définit cette Toolbar comme ActionBar

        // Si nécessaire, vous pouvez ajouter un bouton "retour" ou d'autres options
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Supprimer la flèche de retour
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);  // Désactive la flèche de retour
            getSupportActionBar().setTitle("");
        }

        lanceurFille = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::refreshCustomer);

        // Initialisation correcte du lanceur
        lanceurAdapter = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::resultatEditContact
        );

        // Appeler la méthode pour désactiver la validation SSL
        SSLCertificate.disableSSLCertificateValidation();

        nameView = findViewById(R.id.nameView);
        descriptionView = findViewById(R.id.descriptionView);
        addressView = findViewById(R.id.customer_address);

        // Initialisation de la liste des clients avant de l'utiliser dans l'adapter
        contacts = new ArrayList<>();

        // Initialisation de la liste des id des clients avant de l'utiliser dans l'adapter
        idContacts = new ArrayList<>();

        // Configuration du RecyclerView
        recyclerView = findViewById(R.id.contacts_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Récupération de l'id du client
        Intent intentionMere = getIntent();
        idCustomer = intentionMere.getStringExtra("idCustomer");

        adapter = new Contacts_RecyclerView(contacts, idContacts, new Contacts_RecyclerView.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                contacts.remove(position);
                idContacts.remove(position);
                adapter.notifyItemRemoved(position);
            }
        }, lanceurAdapter,this);

        recyclerView.setAdapter(adapter);

        getCustomerDatas();

        // Appel de la méthode pour récupérer les contacts
        getContacts();

        boutonModifier = findViewById(R.id.detail_client_modifier);

        //Ecouteur de clic sur le bouton modifier
        boutonModifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // création d'une intention
                Intent intention = new Intent(DetailClient.this, EditCustomer.class);

                // Envoie des informations dans l'intention
                intention.putExtra("name", name);
                intention.putExtra("description", description);
                intention.putExtra("country", country);
                intention.putExtra("street", street);
                intention.putExtra("postalCode", postalCode);
                intention.putExtra("city", city);
                intention.putExtra("supplement", supplement);

                intention.putExtra("idAddress", idAddress);

                intention.putExtra("idCustomer", "12");

                lanceurFille.launch(intention); // Lancement activité fille
            }
        });
    }

    /**
     * Renvoie la file d'attente pour les requêtes Web :
     * - si la file n'existe pas encore : elle est créée puis renvoyée
     * - si une file d'attente existe déjà : elle est renvoyée
     * On assure ainsi l'unicité de la file d'attente
     * @return RequestQueue une file d'attente pour les requêtes Volley
     */
    private RequestQueue getFileRequete() {
        if (fileRequete == null) {

            // notez en argument la présence d'un objet pour gérer le proxy
            fileRequete = Volley.newRequestQueue(this, new GestionProxy());
        }
        // sinon
        return fileRequete;
    }

    /**
     * Vérifie si l'appareil a une connexion Internet disponible
     * @return true si la connexion est disponible, false sinon
     */
    public boolean estConnecteInternet() {
        ConnectivityManager gestionnaireConnexion = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoReseau = gestionnaireConnexion.getActiveNetworkInfo();

        if (infoReseau != null && infoReseau.isConnected()) {
            return true;
        } else {
            Toast.makeText(this, "Pas de connexion Internet", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void refreshCustomer(ActivityResult resultat) {
        // on récupère l'intention envoyée par la fille
        Intent intent = resultat.getData();
        // si le code retour indique que tout est ok
        if (resultat.getResultCode() == Activity.RESULT_OK) {
            getCustomerDatas();
        }
    }

    private void getContacts() {
        // Vérifier la connexion Internet avant de lancer la requête
        if (!estConnecteInternet()) {
            return;  // Si pas de connexion, on ne fait rien
        }

        String urlGetContacts = URL_CONTACTS + idCustomer;

        // Créer la requête GET
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlGetContacts, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Effacer la liste des clients existante
                        contacts.clear();
                        idContacts.clear();

                        try {
                            // Accéder à la clé 'response' qui contient le tableau des entreprises
                            JSONArray clientArray = response.getJSONArray("response");

                            // Parcourir la réponse JSON pour extraire les données des entreprises
                            for (int i = 0; i < clientArray.length(); i++) {
                                JSONObject clientJson = clientArray.getJSONObject(i); // Récupérer une entreprise

                                // Récupérer le nom et la description du contact
                                String firstName = clientJson.optString("firstName", "Prenom non disponible");
                                String lastName = clientJson.optString("lastName", "Nom non disponible");
                                Integer idClient = clientJson.optInt("id", -1);
                                // Ajouter les clients dans la liste
                                String clientInfo = firstName + " " + lastName;  // Ajout d'un espace entre le prénom et le nom
                                contacts.add(clientInfo);
                                idContacts.add(idClient);
                            }

                            // Mettre à jour l'adapter pour refléter les changements
                            adapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(DetailClient.this, "Erreur lors de la récupération des clients", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error.networkResponse != null) {
                            // Si la réponse réseau est disponible, récupérer le code d'état et afficher les détails
                            Log.e("VolleyError", "Status Code: " + error.networkResponse.statusCode);
                            Log.e("VolleyError", "Response: " + new String(error.networkResponse.data));
                        } else {
                            // Si la réponse réseau est nulle, afficher un message d'erreur générique
                            Log.e("VolleyError", "Erreur réseau inconnue");
                        }
                        Toast.makeText(DetailClient.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                    }
                });

        // Ajouter la requête à la file d'attente Volley
        getFileRequete().add(jsonObjectRequest);
    }

    private void getCustomerDatas() {
        // Vérifier la connexion Internet avant de lancer la requête
        if (!estConnecteInternet()) {
            return;  // Si pas de connexion, on ne fait rien
        }

        String urlCustomerDatas = URL_CUSTOMER + idCustomer;

        // Créer la requête GET
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlCustomerDatas, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject clientJson = response.getJSONObject("response"); // Récupérer une entreprise

                            // Récupérer le nom et la description du client
                            name = clientJson.optString("name", "Nom non disponible");
                            description = clientJson.optString("description", "Description non disponible");

                            JSONObject addressJson = clientJson.getJSONObject("address");
                            country = addressJson.optString("country", "Pays non disponible");
                            street = addressJson.optString("street", "Rue non disponible");
                            city = addressJson.optString("city", "Ville non disponible");
                            postalCode = addressJson.optString("postalCode", "Code postal non disponible");
                            supplement = addressJson.optString("supplement", "Code postal non disponible");

                            idAddress = addressJson.optString("id", "Id non disponible");

                            // Mettre à jour les informations du client
                            nameView.setText(name);
                            descriptionView.setText(description);
                            addressView.setText(street + ",\n" + postalCode + " " + city + ",\n" + country + ",\n" + supplement);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(DetailClient.this, "Erreur lors de la récupération des clients", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error.networkResponse != null) {
                            // Si la réponse réseau est disponible, récupérer le code d'état et afficher les détails
                            Log.e("VolleyError", "Status Code: " + error.networkResponse.statusCode);
                            Log.e("VolleyError", "Response: " + new String(error.networkResponse.data));
                        } else {
                            // Si la réponse réseau est nulle, afficher un message d'erreur générique
                            Log.e("VolleyError", "Erreur réseau inconnue");
                        }
                        Toast.makeText(DetailClient.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                    }
                });

        // Ajouter la requête à la file d'attente Volley
        getFileRequete().add(jsonObjectRequest);
    }


    public void resultatEditContact(ActivityResult resultat) {
        // on récupère l'intention envoyée par la fille
        Intent intent = resultat.getData();
        // si le code retour indique que tout est ok
        if (resultat.getResultCode() == Activity.RESULT_OK) {
            getContacts();
        }
    }

    public void deleteContact(String idContact) {
        // Vérifier la connexion Internet avant de lancer la requête
        if (!estConnecteInternet()) {
            return;  // Si pas de connexion, on ne fait rien
        }

        String urlDeleteContact = URL_DELETE + idContact;

        // Créer la requête GET
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE, urlDeleteContact, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Vérifier la réponse et afficher un message adapté
                            String status = response.getString("status");
                            if ("OK".equals(status)) {
                                Toast.makeText(getApplicationContext(), "Client supprimé avec succès", Toast.LENGTH_SHORT).show();
                                getContacts();
                            } else {
                                Toast.makeText(getApplicationContext(), "Erreur lors de la suppression du client", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(DetailClient.this, "Erreur lors de la récupération des clients", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error.networkResponse != null) {
                            // Si la réponse réseau est disponible, récupérer le code d'état et afficher les détails
                            Log.e("VolleyError", "Status Code: " + error.networkResponse.statusCode);
                            Log.e("VolleyError", "Response: " + new String(error.networkResponse.data));
                        } else {
                            // Si la réponse réseau est nulle, afficher un message d'erreur générique
                            Log.e("VolleyError", "Erreur réseau inconnue");
                        }
                        Toast.makeText(DetailClient.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                    }
                });

        // Ajouter la requête à la file d'attente Volley
        getFileRequete().add(jsonObjectRequest);
    }
}