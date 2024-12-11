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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationActivity extends AppCompatActivity {

    private static final String EXTRA_LOGIN = "EMAIL_KEY";

    private static final String EXTRA_PASSWORD = "PASSWORD_KEY";

    /** Contient l'url de l'appel à l'API */
    private static final String url = "https://postman-echo.com/post";

    private String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";


    /** Contient le champ email du formulaire */
    private EditText emailArea;

    /** Contient le champ mot de passe du formulaire */
    private EditText passwordArea;

    /** contient la reponse de l'API */
    private TextView responseArea;

    /** Contient la chaine de caractères du champ email */
    private String emailText;

    /** Contient la chaine de caractères du champ mot de passe */
    private String passwordText;

    /** Contient les paramètres du corps de la requête */
    private String[] bodyRequest;

    /**
     * File d'attente pour les requêtes Web (en lien avec l'utilisation de Volley)
     */
    private RequestQueue fileRequete;

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
        //Intent intention = new Intent(AuthenticationActivity.class, )
        //
    }

    public void authenticationClick(View view) {
        emailText = emailArea.getText().toString();
        passwordText = passwordArea.getText().toString();
        if (emailText.isEmpty() || passwordText.isEmpty()) {
            responseArea.setText(R.string.missing_data);
        } else if (!emailText.matches(emailRegex)){
            responseArea.setText(R.string.invalid_email);
        } else {
            Toast.makeText(this, emailText + passwordText, Toast.LENGTH_LONG)
                    .show();
            bodyRequest[0] = emailText;
            bodyRequest[1] = passwordText;
            sendData(url, bodyRequest);
        }
    }

    public void sendData(String url, String[] body) {

        boolean toutOk;
        toutOk = true;
        JSONObject objectSent = new JSONObject();
        try {
            objectSent.put("EMAIL", body[0]);
            objectSent.put("PASSWORD", body[1]);
        } catch(JSONException e) {
            toutOk = false;
        }
        if (toutOk) {
            JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST, url, objectSent,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject reponse) {
                            responseArea.setText(R.string.api_check);
                            //Intent intention = new Intent(AuthenticationActivity.class, );
                            //intention.putExtra(EXTRA_LOGIN, emailText);
                            //intention.putExtra(EXTRA_PASSWORD, passwordText);
                            //startActivity(intention);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            responseArea.setText(error.toString());
                        }

                    })

                    {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<>();

                            //headers.put();
                            return headers;
                    }
                };
                getFileRequete().add(volleyRequest);
            }
    }

    public void forgottenPasswordClick(View view) {
        Toast.makeText(this, "un mail vous a été envoyé à l'adresse mail renseignée", Toast.LENGTH_LONG)
                .show();
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
            fileRequete = Volley.newRequestQueue(this, new GestionProxy());
        }
        // sinon
        return fileRequete;
    }
}