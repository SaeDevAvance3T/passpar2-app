package com.example.passpar2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Clients_creer extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clients_creer);

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

        Button boutonValider = findViewById(R.id.nouveau_client_valider);
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
                    new Intent(Clients_creer.this,
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

    /**
     * Méthode appelée automatiquement lors du clic sur le bouton "Valider"
     * Permet si toutes les informations sont renseignées de créer un client
     * @param bouton bouton cliqué
     */
    public void clicValider(View bouton) {
        // Récupérer les informations du formulaire
        String nomEntreprise = ((EditText) findViewById(R.id.nouveau_client_saisieNomEntreprise)).getText().toString().trim();
        String description = ((EditText) findViewById(R.id.nouveau_client_saisieDescriptionEntreprise)).getText().toString().trim();
        String pays = ((EditText) findViewById(R.id.nouveau_client_saisiePays)).getText().toString().trim();
        String ville = ((EditText) findViewById(R.id.nouveau_client_saisieVille)).getText().toString().trim();
        String codepostal = ((EditText) findViewById(R.id.nouveau_client_saisieCodePostal)).getText().toString().trim();
        String rue = ((EditText) findViewById(R.id.nouveau_client_saisieRue)).getText().toString().trim();
        String complement = ((EditText) findViewById(R.id.nouveau_client_saisieComplement)).getText().toString().trim();
        String nomContact = ((EditText) findViewById(R.id.nouveau_client_saisieNomContact)).getText().toString().trim();
        String prenomContact = ((EditText) findViewById(R.id.nouveau_client_saisiePrenomContact)).getText().toString().trim();
        String telephoneContact = ((EditText) findViewById(R.id.nouveau_client_saisietelephoneContact)).getText().toString().trim();

        // Validation des champs
        if (!nomEntreprise.isEmpty() && !description.isEmpty() && !pays.isEmpty() && !ville.isEmpty()
                && !codepostal.isEmpty() && !rue.isEmpty() && !complement.isEmpty()
                && !nomContact.isEmpty() && !prenomContact.isEmpty() && !telephoneContact.isEmpty()) {

            // Créer un AlertDialog pour confirmer la création du client
            new AlertDialog.Builder(Clients_creer.this)
                    .setTitle("Confirmer la création du client")
                    .setMessage("Êtes-vous sûr de vouloir enregistrer ces informations ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        // Créer un Intent pour renvoyer les données à MainActivity
                        Intent intentionRetour = new Intent();
                        intentionRetour.putExtra("nomEntreprise", nomEntreprise);
                        intentionRetour.putExtra("description", description);
                        intentionRetour.putExtra("pays", pays);
                        intentionRetour.putExtra("ville", ville);
                        intentionRetour.putExtra("codepostal", codepostal);
                        intentionRetour.putExtra("rue", rue);
                        intentionRetour.putExtra("complement", complement);
                        intentionRetour.putExtra("nomContact", nomContact);
                        intentionRetour.putExtra("prenomContact", prenomContact);
                        intentionRetour.putExtra("telephoneContact", telephoneContact);

                        // Renvoyer le résultat avec les données et terminer l'activité
                        setResult(Activity.RESULT_OK, intentionRetour);
                        finish();  // Retourne à MainActivity
                    })
                    .setNegativeButton("Non", null)  // Si "Non", on ne fait rien
                    .show();  // Afficher l'AlertDialog
        } else {
            Toast.makeText(Clients_creer.this, "Les infos sont pas complètes", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Méthode appelée automatiquement lors du clic sur le bouton "Annuler"
     * Permet d'annuler l'ajout et renvoie donc vers la page précédente
     * @param bouton bouton cliqué
     */
    public void clicAnnuler(View bouton) {
        finish();
    }

}