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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;



public class DetailClient extends AppCompatActivity {

    /**
     * File d'attente pour les requêtes API (en lien avec l'utilisation de Volley)
     */
    private RequestQueue fileRequete;

    /**
     * URL de l'API à interroger
     */
    private static String URL_API = "https://2bet.fr/api/customers";

    /**
     * URL de l'API pour les informations du customer
     */
    private static final String URL_CUSTOMER = "https://2bet.fr/api/customers";

    private ActivityResultLauncher<Intent> lanceurFille;

    private ActivityResultLauncher<Intent> lanceurAdapter;

    private RecyclerView recyclerView;
    private Contacts_RecyclerView adapter;
    private List<String> contacts;

    private List<Integer> idContacts;

    private AppCompatButton boutonRequete;

    private TextView nameView;

    private TextView descriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_client);

        lanceurFille = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::editContacts);

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

        // Initialisation correcte du lanceur
        lanceurAdapter = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::resultatEditContact
        );

        // Désactiver la vérification du certificat SSL pour les tests
        TrustManager[] trustAllCertificates = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Installer un gestionnaire de confiance pour tous les certificats
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCertificates, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        nameView = findViewById(R.id.nameView);
        descriptionView = findViewById(R.id.descriptionView);

        // Initialisation de la liste des clients avant de l'utiliser dans l'adapter
        contacts = new ArrayList<>();

        // Initialisation de la liste des id des clients avant de l'utiliser dans l'adapter
        idContacts = new ArrayList<>();

        // Configuration du RecyclerView
        recyclerView = findViewById(R.id.contacts_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DetailClient detailClient = new DetailClient();  // Créer l'instance de DetailClient
        adapter = new Contacts_RecyclerView(contacts, idContacts, new Contacts_RecyclerView.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                contacts.remove(position);
                idContacts.remove(position);
                adapter.notifyItemRemoved(position);
            }
        }, lanceurAdapter,detailClient);

        recyclerView.setAdapter(adapter);

        getCustomerDatas();

        // Appel de la méthode pour récupérer les contacts
        getContacts();

        findViewById(R.id.detail_client_modifier).setOnClickListener(v -> {
            Intent intention = new Intent(DetailClient.this, Clients_edit.class);
            lanceurFille.launch(intention);
        });

        boutonRequete = findViewById(R.id.detail_client_modifier);

        boutonRequete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // redirigé vers authentification
                getContacts();
                Toast.makeText(getApplicationContext(), "Redirection vers authentification", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private <O> void editContacts(O o) {
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

    private void getContacts() {
        // Vérifier la connexion Internet avant de lancer la requête
        if (!estConnecteInternet()) {
            return;  // Si pas de connexion, on ne fait rien
        }

        // Créer la requête GET
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, URL_API, null,
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
                                JSONArray contactsArray = clientJson.getJSONArray("contacts"); // Récupérer les contacts de l'entreprise

                                // Parcourir le tableau des contacts de chaque entreprise
                                for (int j = 0; j < contactsArray.length(); j++) {
                                    JSONObject contactJson = contactsArray.getJSONObject(j);

                                    // Récupérer le nom et la description du contact
                                    String firstName = contactJson.optString("firstName", "Prenom non disponible");
                                    String lastName = contactJson.optString("lastName", "Nom non disponible");
                                    Integer idClient = contactJson.optInt("id", -1);

                                    // Ajouter les clients dans la liste
                                    String clientInfo = firstName + " " + lastName;  // Ajout d'un espace entre le prénom et le nom
                                    contacts.add(clientInfo);
                                    idContacts.add(idClient);
                                }
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

        // Créer la requête GET
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, URL_CUSTOMER, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Accéder à la clé 'response' qui contient le tableau des entreprises
                            JSONArray clientArray = response.getJSONArray("response");

                            JSONObject clientJson = clientArray.getJSONObject(0); // Récupérer une entreprise

                            // Récupérer le nom et la description du client
                            String name = clientJson.optString("name", "Nom non disponible");
                            String description = clientJson.optString("description", "Description non disponible");
                            // Mettre à jour les informations du client
                            nameView.setText(name);
                            descriptionView.setText(description);

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

    public void deleteContact(int position) {
        // Vérifier la connexion Internet avant de lancer la requête
        if (!estConnecteInternet()) {
            return;  // Si pas de connexion, on ne fait rien
        }

        URL_API += position;

        // Créer la requête GET
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE, URL_API, null,
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

    /**
     * Met à jour la liste des contacts selon celle renvoyée par l'API
     * @param reponse Objet JSON contenant la réponse de l'API
     */
    public void updateListeContact(JSONObject reponse) {
        try {
            StringBuilder resultatFormate = new StringBuilder();
            if (reponse.has("nom")) {
                resultatFormate.append("Nom : ").append(reponse.getString("nom"));
            } else {
                resultatFormate.append("Nom non disponible");
            }
            //zoneResultat.setText(resultatFormate.toString());
            //zoneResultat.setText(reponse.toString());
        } catch (JSONException erreur) {
         Log.e("JSONError", "Erreur lors de l'analyse du JSON", erreur);
            Toast.makeText(this, "Erreur lors de l'analyse du JSON", Toast.LENGTH_LONG).show();
        }
    }
}