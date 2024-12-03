package com.example.passpar2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    /**
     * File d'attente pour les requêtes API (en lien avec l'utilisation de Volley)
     */
    private RequestQueue fileRequete;

    /**
     * URL de l'API à interroger
     */
    private static final String URL_FILM = "http://www.omdbapi.com/?t=%s&apikey=XXXXXXXX";

    /** Zone de saisie du titre du film recherché */
    private EditText zoneTitre;

    /** Zone pour afficher le résultat de la recherche */
    private TextView zoneResultat;

    private AppCompatButton boutonConnecter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nouveau_compte_partie1);

        zoneTitre = findViewById(R.id.saisieNom);
        zoneResultat = findViewById(R.id.titre);

        boutonConnecter = findViewById(R.id.nouveau_compte_connecter);
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
            fileRequete = Volley.newRequestQueue(this);
        }
        // sinon
        return fileRequete;
    }

    /**
     * Gestion du clic sur le bouton rechercher
     * Une requête est envoyée au Web service pour rechercher le film saisi par l'utilisateur.
     * Le résultat de la requête est affiché en sous la forme d'une chaîne de caractères.
     * A défaut, c'est un message d'erreur qui est affiché
     * @param bouton bouton à l'origine du clic
     */
    public void clicRechercherEnChaine(View bouton) {
        try {
            // le titre saisi par l'utilisateur est récupéré et encodé en UTF-8
            String titre = URLEncoder.encode(zoneTitre.getText().toString(), "UTF-8");
            // le titre du film est insésré dans l'URL de recherche du film
            String url = String.format(URL_FILM, titre);
            /*
             * on crée une requête GET, paramètrée par l'url préparée ci-dessus,
             * Le résultat de cette requête sera une chaîne de caractères, donc la requête
             * est de type StringRequest
             */
            StringRequest requeteVolley = new StringRequest(Request.Method.GET, url,
                    // écouteur de la réponse renvoyée par la requête
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String reponse) {
                            zoneResultat.setText("Début de la réponse obtenue"
                                    + reponse.substring(0, Math.min(400, reponse.length())));
                        }
                    },
                    // écouteur du retour de la requête si aucun résultat n'est renvoyé
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError erreur) {
                            zoneResultat.setText("R.string.message_erreur");
                        }
                    });
            // la requête est placée dans la file d'attente des requêtes
            getFileRequete().add(requeteVolley);
        } catch(UnsupportedEncodingException erreur) {
            // problème lors de l'encodage de la chaîne titre
            Toast.makeText(this, "erreur", Toast.LENGTH_LONG).show();
        }
    }

}