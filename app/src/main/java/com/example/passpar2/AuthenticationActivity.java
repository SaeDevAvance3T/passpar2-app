package com.example.passpar2;

import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.HashMap;
import java.util.Map;

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
        Intent intent = new Intent(AuthenticationActivity.this, ActiviteCreationComptePartie1.class);
        startActivity(intent);
        finish(); // Facultatif : ferme l'activité actuelle
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

        // Appeler la méthode pour désactiver la validation SSL
        SSLCertificate.disableSSLCertificateValidation();

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
                                // Vérifier si le status est "OK"
                                String status = response.getString("status");
                                if (status.equals("OK")) {
                                    // Récupérer les données de l'utilisateur
                                    JSONObject responseData = response.getJSONObject("response");
                                    int id = responseData.getInt("id");
                                    String firstName = responseData.getString("firstName");
                                    String lastName = responseData.getString("lastName");
                                    String email = responseData.getString("email");

                                    // Démarrer une nouvelle activité et passer les données
                                    Intent intent = new Intent(AuthenticationActivity.this, Accueil_main.class);
                                    //intent.putExtra("id", id);
                                    //intent.putExtra("firstName", firstName);
                                    //intent.putExtra("lastName", lastName);
                                    //intent.putExtra("email", email);

                                    // Enregistrer l'ID dans SharedPreferences
                                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putInt("userId", id);  // Enregistrer l'ID de l'utilisateur
                                    editor.apply();  // Appliquer les changements

                                    startActivity(intent);
                                    finish(); // Facultatif : ferme l'activité actuelle
                                    Toast.makeText(getApplicationContext(), "Connexion réussie !", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Échec de l'authentification !", Toast.LENGTH_SHORT).show();
                                    //responseArea.setText("Échec de l'authentification.");
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "Erreur lors de la lecture de la réponse !", Toast.LENGTH_SHORT).show();
                                //responseArea.setText("Erreur lors de la lecture de la réponse.");
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
