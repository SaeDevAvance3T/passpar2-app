package com.example.passpar2;

import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class AccountActivity extends MenuActivity {

    /**
     * File d'attente pour les requêtes API (en lien avec l'utilisation de Volley)
     */
    private RequestQueue fileRequete;

    private String urlAPI = "https://2bet.fr/api/customers";

    private boolean motdepasseVisible = false;  // Contrôler l'état du mot de passe

    private TextView viewPassword;

    private String motdepasse;

    private ActivityResultLauncher<Intent> lanceurFille;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lanceurFille = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::resultatModificationUser);

        // Appeler la méthode pour désactiver la validation SSL
        SSLCertificate.disableSSLCertificateValidation();

        requestData();
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

    public void clicModifier(View v) {
        Intent intention = new Intent(AccountActivity.this, EditAccount.class);
        lanceurFille.launch(intention);
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

    private void resultatModificationUser(ActivityResult resultat) {
        // on récupère l'intention envoyée par la fille
        Intent intent = resultat.getData();
        // si le code retour indique que tout est ok
        if (resultat.getResultCode() == Activity.RESULT_OK) {
            // Recharger les informations pour la mise à jour
            requestData();
        }
    }

    private void requestData() {
        // Vérifier la connexion Internet avant de lancer la requête
        if (!estConnecteInternet()) {
            return;  // Si pas de connexion, on ne fait rien
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);  // -1 est la valeur par défaut si l'ID n'est pas trouvé

        String urlAPI = "https://2bet.fr/api/users/" + userId;

        // Créer la requête GET
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlAPI, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject userJson = response.getJSONObject("response");

                            // Récupérer les informations de l'utilisateur
                            String nom = userJson.optString("lastName", "Nom non disponible");
                            String prenom = userJson.optString("firstName", "Prénom non disponible");
                            String email = userJson.optString("email", "Email non disponible");
                            motdepasse = userJson.optString("passwordHash", "Mot de passe non disponible");

                            JSONObject addressJson = userJson.getJSONObject("address");
                            String country = addressJson.optString("country", "Pays non disponible");
                            String street = addressJson.optString("street", "Rue non disponible");
                            String city = addressJson.optString("city", "Ville non disponible");
                            String postalCode = addressJson.optString("postalCode", "Code postal non disponible");
                            String supplement = addressJson.optString("supplement", "Complement non disponible");

                            // Remplir les champs du formulaire avec les données récupérées
                            TextView viewName = findViewById(R.id.account_name);
                            TextView viewEmail = findViewById(R.id.account_email);
                            TextView viewAddress = findViewById(R.id.account_address);

                            viewName.setText(nom + " " + prenom);
                            viewEmail.setText(email);
                            viewAddress.setText(street + ",\n" + postalCode + " " + city + ",\n" + country + "\n" + supplement);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(AccountActivity.this, "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(AccountActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                    }
                });

        // Ajouter la requête à la file d'attente
        getFileRequete().add(jsonObjectRequest);
    }
}
