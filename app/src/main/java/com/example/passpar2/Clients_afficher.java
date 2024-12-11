package com.example.passpar2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Clients_afficher extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Clients_RecyclerView adapter;
    private List<String> clients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clients_afficher);

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

        // Initialisation de la liste des clients
        clients = new ArrayList<>();
        clients.add("Tony Lapeyre");
        clients.add("Thomas Izard");
        clients.add("Thomas Lemaire");

        // Configuration du RecyclerView
        recyclerView = findViewById(R.id.clients_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Clients_RecyclerView(clients, position -> {
            // Supprimer un client
            clients.remove(position);
            adapter.notifyItemRemoved(position);
        });

        recyclerView.setAdapter(adapter);

        // Ajouter un client
        findViewById(R.id.clients_ajouter_bouton).setOnClickListener(v -> {
            clients.add("Nouveau Client");
            adapter.notifyItemInserted(clients.size() - 1);
        });
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
                    new Intent(Clients_afficher.this,
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