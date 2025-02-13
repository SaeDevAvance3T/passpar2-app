
package com.example.passpar2;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.List;

public class EditCustomer extends AppCompatActivity {

    /**
     * File d'attente pour les requêtes API (en lien avec l'utilisation de Volley)
     */
    private RequestQueue fileRequete;

    private String urlAPI = "https://2bet.fr/api/customers/";

    private AppCompatButton boutonValider;

    private EditText nameView;
    private EditText descriptionView;
    private EditText cityView;
    private EditText countryView;
    private EditText postalcodeView;
    private EditText streetView;
    private EditText supplementView;

    private String idAddress;
    private String idCustomer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_edit);

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

        nameView = findViewById(R.id.customers_edit_name);
        descriptionView = findViewById(R.id.customers_edit_description);
        
        // Partie Adresse
        cityView = findViewById(R.id.customers_edit_city);
        countryView = findViewById(R.id.customers_edit_country);
        postalcodeView = findViewById(R.id.customers_edit_postalcode);
        streetView = findViewById(R.id.customers_edit_street);
        supplementView = findViewById(R.id.customers_edit_supplement);

        boutonValider = findViewById(R.id.customers_edit_validate);

        updateCustomerDatas();

        // Gestion du clic sur le bouton "Valider"
        boutonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCustomer(v);
            }
        });
    }

    private void updateCustomerDatas() {
        Intent intention = getIntent();
        String name = intention.getStringExtra("name");
        String description = intention.getStringExtra("description");
        String city = intention.getStringExtra("city");
        String country = intention.getStringExtra("country");
        String postalCode = intention.getStringExtra("postalCode");
        String street = intention.getStringExtra("street");
        String supplement = intention.getStringExtra("supplement");

        idAddress = intention.getStringExtra("idAddress");

        idCustomer = intention.getStringExtra("idCustomer");

        nameView.setText(name);
        descriptionView.setText(description);

        cityView.setText(city);
        countryView.setText(country);
        postalcodeView.setText(postalCode);
        streetView.setText(street);
        supplementView.setText(supplement);
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

    public void editCustomer(View bouton) {
        // Vérifier la connexion Internet avant d'envoyer la requête
        if (!estConnecteInternet()) {
            return;
        }

        String urlEditCustomer = urlAPI + idCustomer;

        // Récupérer les informations du formulaire
        String name = ((EditText) findViewById(R.id.customers_edit_name)).getText().toString().trim();
        String description = ((EditText) findViewById(R.id.customers_edit_description)).getText().toString().trim();
        String country = ((EditText) findViewById(R.id.customers_edit_country)).getText().toString().trim();
        String city = ((EditText) findViewById(R.id.customers_edit_city)).getText().toString().trim();
        String street = ((EditText) findViewById(R.id.customers_edit_street)).getText().toString().trim();
        String postalCode = ((EditText) findViewById(R.id.customers_edit_postalcode)).getText().toString().trim();
        String supplement = ((EditText) findViewById(R.id.customers_edit_supplement)).getText().toString().trim();


        // Vérifier si toutes les informations sont remplies
        if (!name.isEmpty() && !description.isEmpty() && !country.isEmpty()
            && !city.isEmpty() && !street.isEmpty() && !postalCode.isEmpty()
            && !supplement.isEmpty()) {

            // Créer l'objet JSON pour la requête
            JSONObject requeteJson = new JSONObject();
            try {
                //Récupérer l'id du user connecté
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                int userId = sharedPreferences.getInt("userId", -1);  // -1 est la valeur par défaut si l'ID n'est pas trouvé

                // Ajouter les informations de l'entreprise
                requeteJson.put("name", name);
                requeteJson.put("description", description);
                requeteJson.put("userId", userId);

                JSONObject address = new JSONObject();
                address.put("id", idAddress);
                address.put("country", country);
                address.put("city", city);
                address.put("street", street);
                address.put("postalCode", postalCode);
                address.put("supplement", supplement);

                requeteJson.put("address", address);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Erreur lors de la création du JSON", Toast.LENGTH_SHORT).show();
                return;
            }

            // Créer la requête POST avec l'objet JSON
            JsonObjectRequest requeteVolley = new JsonObjectRequest(
                    Request.Method.PUT, urlEditCustomer, requeteJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject reponseJson) {
                            try {
                                // Vérifier la réponse et afficher un message adapté
                                String status = reponseJson.getString("status");
                                if ("OK".equals(status)) {
                                    // Créer un Intent pour renvoyer les données à MainActivity
                                    Intent intentionRetour = new Intent();
                                    // Renvoyer le résultat avec les données et terminer l'activité
                                    setResult(Activity.RESULT_OK, intentionRetour);
                                    finish();
                                    Toast.makeText(getApplicationContext(), "CLient modifié avec succés", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Erreur lors de la création du client", Toast.LENGTH_SHORT).show();
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
