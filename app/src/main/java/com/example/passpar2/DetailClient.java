package com.example.passpar2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailClient extends AppCompatActivity {

    /**
     * File d'attente pour les requêtes API (en lien avec l'utilisation de Volley)
     */
    private RequestQueue fileRequete;

    /**
     * URL de l'API à interroger
     */
    private static final String URL_API = "http://10.2.14.36:8080/api/customers";

    private RecyclerView recyclerView;
    private Contacts_RecyclerView adapter;
    private List<String> contacts;

    private Button boutonRequete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_client);

        // Configuration de la Toolbar
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);  // Définit cette Toolbar comme ActionBar

        // Si nécessaire, vous pouvez ajouter un bouton "retour" ou d'autres options
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Supprimer la flèche de retour
        //if (getSupportActionBar() != null) {
        //    getSupportActionBar().setDisplayHomeAsUpEnabled(false);  // Désactive la flèche de retour
        //    getSupportActionBar().setTitle("");
        //}

        // Initialisation de la liste des clients
        contacts = new ArrayList<>();
        contacts.add("Tony Lapeyre");
        contacts.add("Thomas Izard");
        contacts.add("Thomas Lemaire");

        // Configuration du RecyclerView
        recyclerView = findViewById(R.id.contacts_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Contacts_RecyclerView(contacts, position -> {
            // Supprimer un client
            contacts.remove(position);
            adapter.notifyItemRemoved(position);
        });

        recyclerView.setAdapter(adapter);

        boutonRequete = findViewById(R.id.button);

        boutonRequete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // redirigé vers authentification
                getContacts(v);
                Toast.makeText(getApplicationContext(), "Redirection vers authentification", Toast.LENGTH_SHORT).show();
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

    /**
     * Gestion de la récupération des contacts pour l'entreprise concernée
     * Une requête est envoyée au Web service pour récupérer cette liste
     * @param bouton bouton à l'origine du clic
     */
    public void getContacts(View bouton) {
        // Vérifier la connexion Internet avant d'envoyer la requête
        if (!estConnecteInternet()) {
            return;
        }

        String urlPrete = URL_API;

        StringRequest requeteVolley = new StringRequest(
                Request.Method.GET, urlPrete,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String reponseBrute) {
                        Log.d("Response", "Réponse brute : " + reponseBrute);
                        // Vérifie si la réponse est bien un JSON avant de la traiter
                        if (reponseBrute.startsWith("{") && reponseBrute.endsWith("}")) {
                            try {
                                JSONObject reponseJson = new JSONObject(reponseBrute);
                                updateListeContact(reponseJson);
                            } catch (JSONException e) {
                                Log.e("JSONError", "Erreur lors de l'analyse du JSON", e);
                                Toast.makeText(DetailClient.this, "Erreur lors de l'analyse du JSON", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // La réponse n'est pas un JSON, afficher un message d'erreur
                            Log.e("ResponseError", "La réponse n'est pas un JSON : " + reponseBrute);
                            Toast.makeText(DetailClient.this, "Réponse non JSON reçue", Toast.LENGTH_LONG).show();
                            //zoneResultat.setText("Erreur : Réponse non JSON");
                            //zoneResultat.setText(reponseBrute);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError erreur) {
                        erreur.printStackTrace();
                        //zoneResultat.setText(erreur.toString());
                        Toast.makeText(DetailClient.this, erreur.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        getFileRequete().add(requeteVolley);
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