package com.example.passpar2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Charger le menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gérer les clics des items
        int id = item.getItemId();

        if (id == R.id.action_account) {
            return true;
        } else if (id == R.id.action_path) {
            Toast.makeText(this, "Parcours sélectionné", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_clients) {
            // création d'une intention
            Intent intention =
                    new Intent(AccountActivity.this,
                            Clients_afficher.class);
            // lancement de l'activité fille
            startActivity(intention);
            return true;
        } else if (id == R.id.action_iti) {
            Toast.makeText(this, "Itinéraires sélectionné", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
