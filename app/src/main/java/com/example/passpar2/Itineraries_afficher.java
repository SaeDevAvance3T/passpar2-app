package com.example.passpar2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Itineraries_afficher extends MenuActivity {

    /**
     * File d'attente pour les requêtes API (en lien avec l'utilisation de Volley)
     */
    private RequestQueue fileRequete;

    private RecyclerView recyclerView;
    private Itineraries_RecyclerView adapter;
    private List<String> itineraries;
    private List<String> idItineraries;

    private String url = "https://2bet.fr/api/itineraries";  // URL de l'API

    // Code pour le résultat
    private static final int REQUEST_CODE_AJOUTER_CLIENT = 1;

    private ActivityResultLauncher<Intent> lanceurFille;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itineraries_afficher);

        lanceurFille = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::resultatAjoutClient);

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

        // Appeler la méthode pour désactiver la validation SSL
        SSLCertificate.disableSSLCertificateValidation();

        // Initialisation de la liste des clients avant de l'utiliser dans l'adapter
        itineraries = new ArrayList<>();
        idItineraries = new ArrayList<>();

        // Configuration du RecyclerView
        recyclerView = findViewById(R.id.itineraries_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Itineraries_RecyclerView(itineraries, idItineraries, position -> {
            // Supprimer un client
            itineraries.remove(position);
            idItineraries.remove(position);
            adapter.notifyItemRemoved(position);
        });

        recyclerView.setAdapter(adapter);

        // Appel de la méthode pour récupérer les clients
        fetchClients();

        findViewById(R.id.itineraries_create_button).setOnClickListener(v -> {
            Intent intention = new Intent(Itineraries_afficher.this, NewRouteActivity.class);
            lanceurFille.launch(intention);
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

    private void fetchClients() {
        // Vérifier la connexion Internet avant de lancer la requête
        if (!estConnecteInternet()) {
            return;  // Si pas de connexion, on ne fait rien
        }

        // Créer la requête GET
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Effacer la liste des clients existante
                        itineraries.clear();
                        idItineraries.clear();

                        try {
                            // Accéder à la clé 'response' qui contient le tableau des clients
                            JSONArray itinerarieArray = response.getJSONArray("response");

                            // Parcourir la réponse JSON pour extraire les données
                            for (int i = 0; i < itinerarieArray.length(); i++) {
                                JSONObject itinerarieJson = itinerarieArray.getJSONObject(i);

                                // Récupérer le nom et la description du client
                                String name = itinerarieJson.optString("name", "Nom non disponible");
                                String id = itinerarieJson.optString("id", "Id non disponible");

                                // Ajouter les clients dans la liste
                                itineraries.add(name);
                                idItineraries.add(id);
                            }

                            // Mettre à jour l'adapter pour refléter les changements
                            adapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(Itineraries_afficher.this, "Erreur lors de la récupération des clients", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Itineraries_afficher.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                    }
                });

        // Ajouter la requête à la file d'attente Volley
        getFileRequete().add(jsonObjectRequest);
    }


    private void resultatAjoutClient(ActivityResult resultat) {
        // on récupère l'intention envoyée par la fille
        Intent intent = resultat.getData();
        // si le code retour indique que tout est ok
        if (resultat.getResultCode() == Activity.RESULT_OK) {
            // on récupère la valeur d’un extra, par exemple
            //String nomEntreprise = intent.getStringExtra("nomEntreprise");
            //String description = intent.getStringExtra("description");
            //String pays = intent.getStringExtra("pays");
            //String ville = intent.getStringExtra("ville");
            //String codepostal = intent.getStringExtra("codepostal");
            //String rue = intent.getStringExtra("rue");
            //String complement = intent.getStringExtra("complement");
            //String nomContact = intent.getStringExtra("nomContact");
            //String prenomContact = intent.getStringExtra("prenomContact");
            //String telephoneContact = intent.getStringExtra("telephoneContact");
            //Toast.makeText(this, nomEntreprise + description + pays + ville + codepostal + rue + complement + nomContact + prenomContact + telephoneContact
            //        , Toast.LENGTH_SHORT).show();
//
            //clients.add(prenomContact + " " + nomContact);
            //adapter.notifyItemInserted(clients.size() - 1);

            // Recharger la liste des clients depuis l'API pour être sûr que les données sont à jour
            fetchClients();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Charge le menu de l'Activity
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gérer les clics des items
        int id = item.getItemId();

        if (id == R.id.action_account) {
            // création d'une intention
            Intent intention =
                    new Intent(Itineraries_afficher.this,
                            AccountActivity.class);
            // lancement de l'activité fille
            startActivity(intention);
            return true;
        } else if (id == R.id.action_path) {
            // création d'une intention
            Intent intention =
                    new Intent(Itineraries_afficher.this,
                            Accueil_main.class);
            // lancement de l'activité fille
            startActivity(intention);
            return true;
        } else if (id == R.id.action_clients) {
            // création d'une intention
            Intent intention =
                    new Intent(Itineraries_afficher.this,
                            Clients_afficher.class);
            // lancement de l'activité fille
            startActivity(intention);
            return true;
        } else if (id == R.id.action_iti) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}