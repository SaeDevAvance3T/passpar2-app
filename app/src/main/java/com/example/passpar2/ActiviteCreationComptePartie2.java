package com.example.passpar2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ActiviteCreationComptePartie2 extends AppCompatActivity {

    private String nom;
    private String prenom;
    private String mail;
    private String motdepasse;

    private TextView zoneResultat;

    /**
     * File d'attente pour les requêtes API (en lien avec l'utilisation de Volley)
     */
    private RequestQueue fileRequete;

    /**
     * URL de l'API à interroger
     */
    private static final String URL_API = "https://2bet.fr/api/register";

    private AppCompatButton boutonInscrire;
    private AppCompatButton boutonRetour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nouveau_compte_partie2);

        // Désactiver la vérification du certificat SSL pour les tests
        TrustManager[] trustAllCertificates = new TrustManager[]{
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

        Intent intention = getIntent();
        nom = intention.getStringExtra("nom");
        prenom = intention.getStringExtra("prenom");
        mail = intention.getStringExtra("mail");
        motdepasse = intention.getStringExtra("motdepasse");

        boutonInscrire = findViewById(R.id.nouveau_compte_inscrire);
        boutonRetour = findViewById(R.id.nouveau_compte_retour);

        zoneResultat = findViewById(R.id.texteapi);

        boutonInscrire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pays = ((EditText) findViewById(R.id.saisiePays)).getText().toString().trim();
                String ville = ((EditText) findViewById(R.id.saisieVille)).getText().toString().trim();
                String codepostal = ((EditText) findViewById(R.id.saisieCodePostal)).getText().toString().trim();
                String rue = ((EditText) findViewById(R.id.saisieRue)).getText().toString().trim();
                String complement = ((EditText) findViewById(R.id.saisieComplement)).getText().toString().trim();
                if (!pays.isEmpty() && !ville.isEmpty() && !codepostal.isEmpty() && !rue.isEmpty() && !complement.isEmpty()){
                    register(v);
                    //Toast.makeText(getApplicationContext(), "Appel API pour connection (bloqué actuellement)", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Les informations sont incomplètes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        boutonRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    public void register(View bouton) {
        // Vérifier la connexion Internet avant d'envoyer la requête
        if (!estConnecteInternet()) {
            return;
        }

        // Récupérer les informations du formulaire
        String pays = ((EditText) findViewById(R.id.saisiePays)).getText().toString().trim();
        String ville = ((EditText) findViewById(R.id.saisieVille)).getText().toString().trim();
        String codepostal = ((EditText) findViewById(R.id.saisieCodePostal)).getText().toString().trim();
        String rue = ((EditText) findViewById(R.id.saisieRue)).getText().toString().trim();
        String complement = ((EditText) findViewById(R.id.saisieComplement)).getText().toString().trim();

        // Vérifier si toutes les informations sont remplies
        if (!pays.isEmpty() && !ville.isEmpty() && !codepostal.isEmpty() && !rue.isEmpty() && !complement.isEmpty()) {
            // Créer l'objet JSON pour la requête
            JSONObject requeteJson = new JSONObject();
            try {
                // Ajouter les informations de l'utilisateur
                requeteJson.put("firstName", prenom);
                requeteJson.put("lastName", nom);
                requeteJson.put("email", mail);
                requeteJson.put("password", motdepasse);

                // Créer l'objet 'address' et ajouter les informations dedans
                JSONObject addressJson = new JSONObject();
                addressJson.put("country", pays);  // Le pays
                addressJson.put("street", rue);    // La rue
                addressJson.put("city", ville);    // La ville
                addressJson.put("postalCode", Integer.parseInt(codepostal)); // Le code postal (assurez-vous qu'il est un nombre)
                addressJson.put("supplement", complement);  // Complément d'adresse

                // Ajouter l'objet 'address' à l'objet principal
                requeteJson.put("address", addressJson);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Erreur lors de la création du JSON", Toast.LENGTH_SHORT).show();
                return;
            }

            // URL de l'API pour l'inscription
            String urlPrete = URL_API;

            // Créer la requête POST avec l'objet JSON
            JsonObjectRequest requeteVolley = new JsonObjectRequest(
                    Request.Method.POST, urlPrete, requeteJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject reponseJson) {
                            try {
                                // Vérifier la réponse et afficher un message adapté
                                String status = reponseJson.getString("status");
                                if ("CREATED".equals(status)) {
                                    zoneResultat.setText("Inscription réussie!");
                                } else {
                                    zoneResultat.setText("Erreur :");
                                }
                            } catch (JSONException e) {
                                zoneResultat.setText("Erreur lors du traitement de la réponse.");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError erreur) {
                            erreur.printStackTrace();
                            zoneResultat.setText("Erreur lors de l'appel API : " + erreur.toString());
                        }
                    }
            );

            // Ajouter la requête à la file d'attente
            getFileRequete().add(requeteVolley);
        } else {
            Toast.makeText(getApplicationContext(), "Les informations sont incomplètes", Toast.LENGTH_SHORT).show();
        }
    }

}