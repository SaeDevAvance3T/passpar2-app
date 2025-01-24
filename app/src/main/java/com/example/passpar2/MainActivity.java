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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    /**
     * File d'attente pour les requêtes API (en lien avec l'utilisation de Volley)
     */
    private RequestQueue fileRequete;

    /**
     * URL de l'API à interroger
     */
    private static final String URL_FILM = "http://10.2.14.36:8080/api/customers";

    /** Zone de saisie du titre recherché */
    private EditText zoneTitre;

    /** Zone pour afficher le résultat de la recherche */
    private TextView zoneResultat;

    /** Boutons de connection et vers la page suivante*/
    private AppCompatButton boutonConnecter;
    private AppCompatButton boutonSuivant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Récupération du layout de la page
        setContentView(R.layout.nouveau_compte_partie1);

        //Récupération du titre, résultat, bouton de connexion et suivant
        zoneTitre = findViewById(R.id.saisieNom);
        zoneResultat = findViewById(R.id.titre);
        boutonConnecter = findViewById(R.id.nouveau_compte_connecter);

        boutonSuivant = findViewById(R.id.nouveau_compte_suivant);

        //Ecouteur de clic sur le bouton connecter
        boutonConnecter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // redirigé vers authentification
                //clicRechercherEnChaine(v);
                Toast.makeText(getApplicationContext(), "Redirection vers authentification", Toast.LENGTH_SHORT).show();
            }
        });

        //Ecouteur de clic sur le bouton suivant
        boutonSuivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Récupération des informations saisies par l'utilisateur
                String nom = ((EditText) findViewById(R.id.saisieNom)).getText().toString().trim();
                String prenom = ((EditText) findViewById(R.id.saisiePrenom)).getText().toString().trim();
                String mail = ((EditText) findViewById(R.id.saisieMail)).getText().toString().trim();
                String motdepasse = ((EditText) findViewById(R.id.saisieMdp)).getText().toString().trim();

                //Si données complètes
                if (!nom.isEmpty() && !prenom.isEmpty() && !mail.isEmpty() && !motdepasse.isEmpty()
                        && Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$").matcher(mail).matches()){
                    // création d'une intention
                    Intent intention = new Intent(MainActivity.this, ActiviteCreationComptePartie2.class);

                    // Envoie des informations dans l'intention
                    intention.putExtra("nom", nom);
                    intention.putExtra("prenom", prenom);
                    intention.putExtra("mail", mail);
                    intention.putExtra("motdepasse", motdepasse);

                    startActivity(intention); // Lancement activité fille
                } else {
                    Toast.makeText(getApplicationContext(), "Les informations sont incomplètes", Toast.LENGTH_SHORT).show();
                }
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
     * Gestion du clic sur le bouton rechercher
     * Une requête est envoyée au Web service pour rechercher ce qui est saisi par l'utilisateur.
     * @param bouton bouton à l'origine du clic
     */
    public void clicRechercherEnChaine(View bouton) {
        // Vérifier la connexion Internet avant d'envoyer la requête
        if (!estConnecteInternet()) {
            return;
        }

        String urlPrete = URL_FILM;

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
                                setZoneResultatAvecObjetJson(reponseJson);
                            } catch (JSONException e) {
                                Log.e("JSONError", "Erreur lors de l'analyse du JSON", e);
                                Toast.makeText(MainActivity.this, "Erreur lors de l'analyse du JSON", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // La réponse n'est pas un JSON, afficher un message d'erreur
                            Log.e("ResponseError", "La réponse n'est pas un JSON : " + reponseBrute);
                            Toast.makeText(MainActivity.this, "Réponse non JSON reçue", Toast.LENGTH_LONG).show();
                            zoneResultat.setText("Erreur : Réponse non JSON");
                            zoneResultat.setText(reponseBrute);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError erreur) {
                        erreur.printStackTrace();
                        zoneResultat.setText(erreur.toString());
                        Toast.makeText(MainActivity.this, erreur.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        getFileRequete().add(requeteVolley);
    }

    /**
     * Affiche dans la zone de résultat, les informations essentielles associées à la réponse JSON
     * @param reponse Objet JSON contenant la réponse de l'API
     */
    public void setZoneResultatAvecObjetJson(JSONObject reponse) {
        try {
            StringBuilder resultatFormate = new StringBuilder();
            if (reponse.has("nom")) {
                resultatFormate.append("Nom : ").append(reponse.getString("nom"));
            } else {
                resultatFormate.append("Nom non disponible");
            }
            zoneResultat.setText(resultatFormate.toString());
            zoneResultat.setText(reponse.toString());
        } catch (JSONException erreur) {
            Log.e("JSONError", "Erreur lors de l'analyse du JSON", erreur);
            Toast.makeText(this, "Erreur lors de l'analyse du JSON", Toast.LENGTH_LONG).show();
        }
    }
}
