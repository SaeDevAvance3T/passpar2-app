package com.example.passpar2;

import androidx.appcompat.app.AlertDialog;
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
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class EditAccount extends AppCompatActivity {

    /**
     * File d'attente pour les requêtes API (en lien avec l'utilisation de Volley)
     */
    private RequestQueue fileRequete;

    private String urlAPI = "https://2bet.fr/api/users/";

    private AppCompatButton boutonValider;

    private EditText saisieNom;
    private EditText saisiePrenom;
    private EditText saisieEmail;
    private EditText saisieMotdepasse;
    private EditText saisiePays;
    private EditText saisieVille;
    private EditText saisieCodepostal;
    private EditText saisieRue;
    private EditText saisieComplement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_edit);

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

        saisieNom = findViewById(R.id.account_edit_nom);
        saisiePrenom = findViewById(R.id.account_edit_prenom);
        saisieEmail = findViewById(R.id.account_edit_email);
        saisieMotdepasse = findViewById(R.id.account_edit_motdepasse);
        saisiePays = findViewById(R.id.account_edit_pays);
        saisieVille = findViewById(R.id.account_edit_ville);
        saisieCodepostal = findViewById(R.id.account_edit_codepostal);
        saisieRue = findViewById(R.id.account_edit_rue);
        saisieComplement = findViewById(R.id.account_edit_complement);

        boutonValider = findViewById(R.id.nouveau_client_valider);

        requestDatas();

        // Gestion du clic sur le bouton "Valider"
        boutonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //createCustomer(v);
            }
        });
    }

    private void requestDatas() {
        // Vérifier la connexion Internet avant de lancer la requête
        if (!estConnecteInternet()) {
            return;  // Si pas de connexion, on ne fait rien
        }

        urlAPI += "12";
        Toast.makeText(EditAccount.this, urlAPI, Toast.LENGTH_SHORT).show();

        // Créer la requête GET
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlAPI, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Log pour afficher la réponse complète
                            Log.d("API Response", response.toString());

                            // Accéder à l'objet 'response' qui contient les données de l'utilisateur
                            JSONObject userJson = response.getJSONObject("response");

                            // Récupérer les informations de l'utilisateur
                            String nom = userJson.optString("lastName", "Nom non disponible");
                            String prenom = userJson.optString("firstName", "Prénom non disponible");
                            String email = userJson.optString("email", "Email non disponible");
                            String motdepasse = userJson.optString("passwordHash", "Mot de passe non disponible");

                            // Remplir les champs du formulaire avec les données récupérées
                            saisieNom.setText(nom);
                            saisiePrenom.setText(prenom);
                            saisieEmail.setText(email);
                            saisieMotdepasse.setText(motdepasse);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(EditAccount.this, "Erreur lors de la récupération des clients", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(EditAccount.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                    }
                });

        // Ajouter la requête à la file d'attente Volley
        getFileRequete().add(jsonObjectRequest);
    }

    private void updateDatas() {
        
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Charge le menu de l'Activity
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void createCustomer(View bouton) {
        // Vérifier la connexion Internet avant d'envoyer la requête
        if (!estConnecteInternet()) {
            return;
        }

        // Récupérer les informations du formulaire
        //String nomEntreprise = ((EditText) findViewById(R.id.nouveau_client_saisieNomEntreprise)).getText().toString().trim();
        //String description = ((EditText) findViewById(R.id.nouveau_client_saisieDescriptionEntreprise)).getText().toString().trim();
        //String pays = ((EditText) findViewById(R.id.nouveau_client_saisiePays)).getText().toString().trim();
        //String ville = ((EditText) findViewById(R.id.nouveau_client_saisieVille)).getText().toString().trim();
        //String codepostal = ((EditText) findViewById(R.id.nouveau_client_saisieCodePostal)).getText().toString().trim();
        //String rue = ((EditText) findViewById(R.id.nouveau_client_saisieRue)).getText().toString().trim();
        //String complement = ((EditText) findViewById(R.id.nouveau_client_saisieComplement)).getText().toString().trim();
        //String nomContact = ((EditText) findViewById(R.id.nouveau_client_saisieNomContact)).getText().toString().trim();
        //String prenomContact = ((EditText) findViewById(R.id.nouveau_client_saisiePrenomContact)).getText().toString().trim();
        //String telephoneContact = ((EditText) findViewById(R.id.nouveau_client_saisietelephoneContact)).getText().toString().trim();

        // Vérifier si toutes les informations sont remplies
        //if (!nomEntreprise.isEmpty() && !description.isEmpty() && !pays.isEmpty() && !ville.isEmpty()
        //        && !codepostal.isEmpty() && !rue.isEmpty() && !complement.isEmpty()
        //        && !nomContact.isEmpty() && !prenomContact.isEmpty() && !telephoneContact.isEmpty()) {
//
        //    // Créer l'objet JSON pour la requête
        //    JSONObject requeteJson = new JSONObject();
        //    try {
        //        // Ajouter les informations de l'entreprise
        //        requeteJson.put("name", nomEntreprise);
        //        requeteJson.put("description", description);
        //        requeteJson.put("userId", 12);
//
        //        // Créer l'objet 'contacts' et y ajouter les informations de contact
        //        JSONArray contactsArray = new JSONArray();
        //        JSONObject contact = new JSONObject();
        //        contact.put("firstName", prenomContact);
        //        contact.put("lastName", nomContact);
        //        contact.put("phone", telephoneContact);
//
        //        contactsArray.put(contact);
        //        requeteJson.put("contacts", contactsArray);
//
        //        JSONObject address = new JSONObject();
        //        address.put("country", pays);
        //        address.put("city", ville);
        //        address.put("street", rue);
        //        address.put("postalCode", codepostal);
        //        address.put("supplement", complement);
//
        //        requeteJson.put("address", address);
        //    } catch (JSONException e) {
        //        e.printStackTrace();
        //        Toast.makeText(getApplicationContext(), "Erreur lors de la création du JSON", Toast.LENGTH_SHORT).show();
        //        return;
        //    }
//
        //    // Créer la requête POST avec l'objet JSON
        //    JsonObjectRequest requeteVolley = new JsonObjectRequest(
        //            Request.Method.POST, urlAPI, requeteJson,
        //            new Response.Listener<JSONObject>() {
        //                @Override
        //                public void onResponse(JSONObject reponseJson) {
        //                    try {
        //                        // Vérifier la réponse et afficher un message adapté
        //                        String status = reponseJson.getString("status");
        //                        if ("CREATED".equals(status)) {
        //                            // Créer un Intent pour renvoyer les données à MainActivity
        //                            Intent intentionRetour = new Intent();
        //                            // Renvoyer le résultat avec les données et terminer l'activité
        //                            setResult(Activity.RESULT_OK, intentionRetour);
        //                            finish();
        //                            Toast.makeText(getApplicationContext(), "Client créé avec succès", Toast.LENGTH_SHORT).show();
        //                        } else {
        //                            Toast.makeText(getApplicationContext(), "Erreur lors de la création du client", Toast.LENGTH_SHORT).show();
        //                        }
        //                    } catch (JSONException e) {
        //                        Toast.makeText(getApplicationContext(), "Erreur lors du traitement de la réponse.", Toast.LENGTH_SHORT).show();
        //                    }
        //                }
        //            },
        //            new Response.ErrorListener() {
        //                @Override
        //                public void onErrorResponse(VolleyError erreur) {
        //                    erreur.printStackTrace();
        //                    Toast.makeText(getApplicationContext(), "Erreur lors de l'appel API : " + erreur.toString(), Toast.LENGTH_SHORT).show();
        //                }
        //            }
        //    );
//
        //    // Ajouter la requête à la file d'attente
        //    getFileRequete().add(requeteVolley);
        //} else {
        //    Toast.makeText(getApplicationContext(), "Les informations sont incomplètes", Toast.LENGTH_SHORT).show();
        //}
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