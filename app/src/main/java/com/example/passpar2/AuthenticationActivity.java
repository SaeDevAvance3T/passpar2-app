package com.example.passpar2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class AuthenticationActivity extends AppCompatActivity {

    private EditText emailArea;
    private EditText passwordArea;
    private TextView responseArea;
    private String emailText;
    private String passwordText;
    private String url;
    private String[] bodyRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication);

        emailArea = findViewById(R.id.emailaddress);
        passwordArea = findViewById(R.id.password);
        responseArea = findViewById(R.id.responsetext);
        bodyRequest = new String[2];
    }

    public void createAccountClick(View view) {
        // L'intention pour créer un compte n'est pas encore définie
    }

    public void authenticationClick(View view) {
        emailText = emailArea.getText().toString();
        passwordText = passwordArea.getText().toString();
        if (emailText.isEmpty() || passwordText.isEmpty()) {
            responseArea.setText(R.string.missing_data);
        } else {
            url = "https://2bet.fr/api/login";
            bodyRequest[0] = emailText;
            bodyRequest[1] = passwordText;
            sendData(url, bodyRequest);
        }
    }

    public void sendData(String url, String[] body) {

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

        // Créer l'objet JSON pour la requête
        boolean toutOk = true;
        JSONObject objectSent = new JSONObject();
        try {
            objectSent.put("email", body[0]);
            objectSent.put("password", body[1]);
        } catch (JSONException e) {
            toutOk = false;
        }

        if (toutOk) {
            JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST, url, objectSent,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject responseData = response.getJSONObject("response");

                                // Extraire les données de l'objet "response"
                                int id = responseData.getInt("id");
                                String firstName = responseData.getString("firstName");
                                String lastName = responseData.getString("lastName");
                                String email = responseData.getString("email");

                                // Construire le message à afficher dans l'UI
                                String message = "ID: " + id + "\n" +
                                        "First Name: " + firstName + "\n" +
                                        "Last Name: " + lastName + "\n" +
                                        "Email: " + email;

                                // Afficher les données dans la TextView
                                responseArea.setText(message);
                            } catch (JSONException e) {
                                responseArea.setText("Erreur lors de la lecture de la réponse.");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            responseArea.setText("Erreur API : " + error.getMessage());
                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");  // Ajout du Content-Type
                    return headers;
                }
            };

            // Initialiser la RequestQueue et ajouter la requête
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(volleyRequest);
        } else {
            responseArea.setText("Erreur lors de la création de l'objet JSON.");
        }
    }

    public void forgottenPasswordClick(View view) {
        Toast.makeText(this, "Un mail vous a été envoyé à l'adresse mail renseignée", Toast.LENGTH_LONG)
                .show();
    }
}
