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

import java.util.regex.Pattern;

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

        // Appeler la méthode pour désactiver la validation SSL
        SSLCertificate.disableSSLCertificateValidation();

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
                register(v);
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

        // Récupération des informations saisies par l'utilisateur
        EditText etPays = findViewById(R.id.saisiePays);
        EditText etVille = findViewById(R.id.saisieVille);
        EditText etCodePostal = findViewById(R.id.saisieCodePostal);
        EditText etRue = findViewById(R.id.saisieRue);
        EditText etComplement = findViewById(R.id.saisieComplement);

        String pays = etPays.getText().toString().trim();
        String ville = etVille.getText().toString().trim();
        String codepostal = etCodePostal.getText().toString().trim();
        String rue = etRue.getText().toString().trim();
        String complement = etComplement.getText().toString().trim();

        boolean isValid = true;

        // Récupération des couleurs depuis resources
        int defaultColor = getResources().getColor(R.color.white);
        int errorColor = getResources().getColor(R.color.error);

        // Regex pour valider le code postal (5 chiffres)
        Pattern codePostalPattern = Pattern.compile("^\\d{5}$");

        // Réinitialisation des couleurs des champs
        etPays.setTextColor(defaultColor);
        etVille.setTextColor(defaultColor);
        etCodePostal.setTextColor(defaultColor);
        etRue.setTextColor(defaultColor);
        etComplement.setTextColor(defaultColor);

        etPays.setHintTextColor(defaultColor);
        etVille.setHintTextColor(defaultColor);
        etCodePostal.setHintTextColor(defaultColor);
        etRue.setHintTextColor(defaultColor);
        etComplement.setHintTextColor(defaultColor);

        // Vérification des champs et mise en rouge si vide
        if (pays.isEmpty()) {
            etPays.setTextColor(errorColor);
            etPays.setHintTextColor(errorColor);
            isValid = false;
        }

        if (ville.isEmpty()) {
            etVille.setTextColor(errorColor);
            etVille.setHintTextColor(errorColor);
            isValid = false;
        }

        if (codepostal.isEmpty() || !codePostalPattern.matcher(codepostal).matches()) {
            etCodePostal.setTextColor(errorColor);
            etCodePostal.setHintTextColor(errorColor);
            isValid = false;
        }

        if (rue.isEmpty()) {
            etRue.setTextColor(errorColor);
            etRue.setHintTextColor(errorColor);
            isValid = false;
        }

        if (complement.isEmpty()) {
            etComplement.setTextColor(errorColor);
            etComplement.setHintTextColor(errorColor);
            isValid = false;
        }

        // Si tout est valide, passer à l'activité suivante
        if (isValid) {
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
                                    Intent intent = new Intent(ActiviteCreationComptePartie2.this, Accueil_main.class);
                                    startActivity(intent);
                                    finish(); // Facultatif : ferme l'activité actuelle
                                    Toast.makeText(getApplicationContext(), "Inscription réussie", Toast.LENGTH_SHORT).show();
                                    //zoneResultat.setText("Inscription réussie!");
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