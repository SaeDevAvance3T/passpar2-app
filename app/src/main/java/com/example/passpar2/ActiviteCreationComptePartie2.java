package com.example.passpar2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ActiviteCreationComptePartie2 extends AppCompatActivity {

    private String nom;
    private String prenom;
    private String mail;
    private String motdepasse;

    private AppCompatButton boutonInscrire;
    private AppCompatButton boutonRetour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nouveau_compte_partie2);

        Intent intention = getIntent();
        nom = intention.getStringExtra("nom");
        prenom = intention.getStringExtra("prenom");
        mail = intention.getStringExtra("mail");
        motdepasse = intention.getStringExtra("motdepasse");

        boutonInscrire = findViewById(R.id.nouveau_compte_inscrire);
        boutonRetour = findViewById(R.id.nouveau_compte_retour);

        boutonInscrire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pays = ((EditText) findViewById(R.id.saisiePays)).getText().toString().trim();
                String ville = ((EditText) findViewById(R.id.saisieVille)).getText().toString().trim();
                String codepostal = ((EditText) findViewById(R.id.saisieCodePostal)).getText().toString().trim();
                String rue = ((EditText) findViewById(R.id.saisieRue)).getText().toString().trim();
                String complement = ((EditText) findViewById(R.id.saisieComplement)).getText().toString().trim();
                if (!pays.isEmpty() && !ville.isEmpty() && !codepostal.isEmpty() && !rue.isEmpty() && !complement.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Appel API pour connection (bloqué actuellement)", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Les informations sont incomplètes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        boutonRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}