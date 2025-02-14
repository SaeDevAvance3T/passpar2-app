package com.example.passpar2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class CreateContact extends MenuActivity {

    /**
     * File d'attente pour les requêtes API (en lien avec l'utilisation de Volley)
     */
    private RequestQueue fileRequete;

    private String urlAPI = "https://2bet.fr/api/contacts/customer/";

    private AppCompatButton boutonValider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_create);

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

        boutonValider = findViewById(R.id.create_contact_validate);

        // Gestion du clic sur le bouton "Valider"
        boutonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createContact(v);
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

    public void createContact(View bouton) {
        // Vérifier la connexion Internet avant d'envoyer la requête
        if (!estConnecteInternet()) {
            return;
        }

        //Récupération de l'id du client
        Intent intentionMere = getIntent();
        String idCustomer = intentionMere.getStringExtra("idCustomer");

        String urlCreation = urlAPI + idCustomer;

        // Récupérer les informations du formulaire
        String nomContact = ((EditText) findViewById(R.id.create_contact_lastName)).getText().toString().trim();
        String prenomContact = ((EditText) findViewById(R.id.create_contact_firstName)).getText().toString().trim();
        String telephoneContact = ((EditText) findViewById(R.id.create_contact_phone)).getText().toString().trim();

        boolean isValid = true;

        // Récupération des couleurs depuis resources
        int defaultColor = getResources().getColor(R.color.white);
        int errorColor = getResources().getColor(R.color.error);

        // Réinitialisation des couleurs des champs
        EditText etNomContact = findViewById(R.id.create_contact_lastName);
        EditText etPrenomContact = findViewById(R.id.create_contact_firstName);
        EditText etTelephoneContact = findViewById(R.id.create_contact_phone);

        etNomContact.setTextColor(defaultColor);
        etPrenomContact.setTextColor(defaultColor);
        etTelephoneContact.setTextColor(defaultColor);

        etNomContact.setHintTextColor(defaultColor);
        etPrenomContact.setHintTextColor(defaultColor);
        etTelephoneContact.setHintTextColor(defaultColor);

        // Regex pour valider le numéro de téléphone français (10 chiffres)
        Pattern telephonePattern = Pattern.compile("^0[1-9]\\d{8}$");

        // Vérifier si toutes les informations sont remplies
        if (nomContact.isEmpty()) {
            etNomContact.setTextColor(errorColor);
            etNomContact.setHintTextColor(errorColor);
            isValid = false;
        }

        if (prenomContact.isEmpty()) {
            etPrenomContact.setTextColor(errorColor);
            etPrenomContact.setHintTextColor(errorColor);
            isValid = false;
        }

        if (telephoneContact.isEmpty() || !telephonePattern.matcher(telephoneContact).matches()) {
            etTelephoneContact.setTextColor(errorColor);
            etTelephoneContact.setHintTextColor(errorColor);
            isValid = false;
        }

        // Si tout est valide
        if (isValid) {

            // Créer l'objet JSON pour la requête
            JSONObject requeteJson = new JSONObject();
            try {
                // Ajouter les informations du contact
                requeteJson.put("firstName", prenomContact);
                requeteJson.put("lastName", nomContact);
                requeteJson.put("phone", telephoneContact);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Erreur lors de la création du JSON", Toast.LENGTH_SHORT).show();
                return;
            }

            // Créer la requête POST avec l'objet JSON
            JsonObjectRequest requeteVolley = new JsonObjectRequest(
                    Request.Method.POST, urlCreation, requeteJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject reponseJson) {
                            try {
                                // Vérifier la réponse et afficher un message adapté
                                String status = reponseJson.getString("status");
                                if ("CREATED".equals(status)) {
                                    // Créer un Intent pour renvoyer les données à MainActivity
                                    Intent intentionRetour = new Intent();
                                    // Renvoyer le résultat avec les données et terminer l'activité
                                    setResult(Activity.RESULT_OK, intentionRetour);
                                    finish();
                                    Toast.makeText(getApplicationContext(), "Client créé avec succès", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Erreur lors de la création du contact", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "Erreur lors du traitement de la réponse.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError erreur) {
                            erreur.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Erreur lors de l'appel API : " + erreur.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            // Ajouter la requête à la file d'attente
            getFileRequete().add(requeteVolley);
        } else {
            Toast.makeText(getApplicationContext(), "Les informations sont incomplètes", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Méthode appelée automatiquement lors du clic sur le bouton "Annuler"
     * Permet d'annuler l'ajout et renvoie donc vers la page précédente
     * @param bouton bouton cliqué
     */
    public void clicAnnuler(View bouton) {
        finish();
    }

}