package com.example.passpar2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Gestion_clients extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestion_clients);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Charge le menu de l'Activity
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gérer les clics des items
        int id = item.getItemId();

        if (id == R.id.action_account) {
            Toast.makeText(this, "Compte sélectionné", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_path) {
            // création d'une intention
            Intent intention =
                    new Intent(Gestion_clients.this,
                            Accueil_main.class);
            // lancement de l'activité fille
            startActivity(intention);
            return true;
        } else if (id == R.id.action_clients) {
            Toast.makeText(this, "Déjà sur client", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_iti) {
            Toast.makeText(this, "Itinéraires sélectionné", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}