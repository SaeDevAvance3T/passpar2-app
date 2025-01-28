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
}
