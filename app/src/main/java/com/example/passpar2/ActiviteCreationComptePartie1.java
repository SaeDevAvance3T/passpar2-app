package com.example.passpar2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class ActiviteCreationComptePartie1 extends AppCompatActivity {

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
                Intent intent = new Intent(ActiviteCreationComptePartie1.this, AuthenticationActivity.class);
                startActivity(intent);
                finish(); // Facultatif : ferme l'activité actuelle
                // redirigé vers authentification
                //clicRechercherEnChaine(v);
                //Toast.makeText(getApplicationContext(), "Redirection vers authentification", Toast.LENGTH_SHORT).show();
            }
        });

        //Ecouteur de clic sur le bouton suivant
        boutonSuivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupération des informations saisies par l'utilisateur
                EditText etNom = findViewById(R.id.saisieNom);
                EditText etPrenom = findViewById(R.id.saisiePrenom);
                EditText etMail = findViewById(R.id.saisieMail);
                EditText etMdp = findViewById(R.id.saisieMdp);

                String nom = etNom.getText().toString().trim();
                String prenom = etPrenom.getText().toString().trim();
                String mail = etMail.getText().toString().trim();
                String motdepasse = etMdp.getText().toString().trim();

                // Regex pour valider l'email
                Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

                boolean isValid = true;

                // Récupération des couleurs depuis resources
                int defaultColor = getResources().getColor(R.color.white);
                int errorColor = getResources().getColor(R.color.error);

                etNom.setTextColor(defaultColor);
                etPrenom.setTextColor(defaultColor);
                etMail.setTextColor(defaultColor);
                etMdp.setTextColor(defaultColor);

                etNom.setHintTextColor(defaultColor);
                etPrenom.setHintTextColor(defaultColor);
                etMail.setHintTextColor(defaultColor);
                etMdp.setHintTextColor(defaultColor);

                if (nom.isEmpty()) {
                    etNom.setTextColor(errorColor);
                    etNom.setHintTextColor(errorColor);
                    isValid = false;
                }

                if (prenom.isEmpty()) {
                    etPrenom.setTextColor(errorColor);
                    etPrenom.setHintTextColor(errorColor);
                    isValid = false;
                }

                if (mail.isEmpty() || !emailPattern.matcher(mail).matches()) {
                    etMail.setTextColor(errorColor);
                    etMail.setHintTextColor(errorColor);
                    isValid = false;
                }

                if (motdepasse.isEmpty()) {
                    etMdp.setTextColor(errorColor);
                    etMdp.setHintTextColor(errorColor);
                    isValid = false;
                }

                // Si tout est bon, on passe à l'activité suivante
                if (isValid) {
                    Intent intention = new Intent(ActiviteCreationComptePartie1.this, ActiviteCreationComptePartie2.class);
                    intention.putExtra("nom", nom);
                    intention.putExtra("prenom", prenom);
                    intention.putExtra("mail", mail);
                    intention.putExtra("motdepasse", motdepasse);
                    startActivity(intention);
                } else {
                    Toast.makeText(getApplicationContext(), "Une ou plusieurs informations sont incomplètes", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
