package com.example.passpar2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;

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

        boutonRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}