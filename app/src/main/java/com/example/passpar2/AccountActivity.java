package com.example.passpar2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AccountActivity extends AppCompatActivity {

    /**
     * File d'attente pour les requêtes API (en lien avec l'utilisation de Volley)
     */
    private RequestQueue fileRequete;

    private String urlAPI = "https://2bet.fr/api/customers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

            // notez en argument la présence d'un objet pour gérer le proxy
            fileRequete = Volley.newRequestQueue(this, new GestionProxy());
        }
        // sinon
        return fileRequete;
    }

    /**
     * Vérifie si l'appareil a une connexion Internet disponible
     * @return true si la connexion est disponible, false sinon
     */
    public boolean estConnecteInternet() {
        ConnectivityManager gestionnaireConnexion = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoReseau = gestionnaireConnexion.getActiveNetworkInfo();

        if (infoReseau != null && infoReseau.isConnected()) {
            return true;
        } else {
            Toast.makeText(this, "Pas de connexion Internet", Toast.LENGTH_SHORT).show();
            return false;
        }
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
            // création d'une intention
            Intent intention =
                    new Intent(AccountActivity.this,
                            NewRouteActivity.class);
            // lancement de l'activité fille
            startActivity(intention);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
