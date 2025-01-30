package com.example.passpar2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.passpar2.Accueil_adaptateur_fragments;
import com.example.passpar2.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Cette classe activité gère 3 fragments qui seront affichés via un ViewPager
 * Le ViewPager gère le défilement entre les 3 fragments, défilement effectué
 * lorsque l'utilisteur fait un "glisser".
 * Le 1er fragment est codé de manière à générer un nombre aléatoire, les 2 suivants
 * affichent seulement un texte
 * @author C. Servières
 */
public class Accueil_main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accueil_main);

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

        /*
         * on récupère un accès sur le ViewPager défini dans la vue
         * ainsi que sur le TabLayout qui gèrera les onglets
         */
        ViewPager2 gestionnairePagination = findViewById(R.id.activity_main_viewpager);
        TabLayout gestionnaireOnglet = findViewById(R.id.tab_layout);
        /*
         * on associe au ViewPager un adaptateur (c'est lui qui organise le
         * défilement entre les fragments à afficher)
         */
        gestionnairePagination.setAdapter(new Accueil_adaptateur_fragments(this)) ;
        /*
         * On regroupe dans un tableau les intitulés des boutons d'onglet
         */
        String[] titreOnglet = {getString(R.string.nouveau_compte_onglet_parcours),
                getString(R.string.nouveau_compte_onglet_details)};
        /*
         * On crée une instance de type TabLayoutMediator qui fera le lien entre
         * le gestionnaire de pagination et le gestionnaire des onglets
         * La méthode onConfigureTab permet de préciser quel intitulé de bouton
         * d'onglets correspond à tel ou tel onglet, selon la position de celui-ci
         * L'instance TabLayoutMediator est attachée à l'activité courante
         *
         */
        new TabLayoutMediator(gestionnaireOnglet, gestionnairePagination,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(TabLayout.Tab tab,
                                                         int position) {
                        tab.setText(titreOnglet[position]);
                    }
                }).attach();
        /*
         * Le code ci-dessus peut être écrit de manière plus concise en utilisant
         * des lambda :
         *
         * new TabLayoutMediator(gestionnaireOnglet, gestionnairePagination,
         * (tab, position) -> tab.setText(titreOnglet[position])
         * ).attach();
         *
         */
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
            // création d'une intention
            Intent intention =
                    new Intent(Accueil_main.this,
                            AccountActivity.class);
            // lancement de l'activité fille
            startActivity(intention);
            return true;
        } else if (id == R.id.action_path) {
            Toast.makeText(this, "Parcours sélectionné", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_clients) {
            // création d'une intention
            Intent intention =
                    new Intent(Accueil_main.this,
                            Clients_afficher.class);
            // lancement de l'activité fille
            startActivity(intention);
            return true;
        } else if (id == R.id.action_iti) {
            // création d'une intention
            Intent intention =
                    new Intent(Accueil_main.this,
                            NewRouteActivity.class);
            // lancement de l'activité fille
            startActivity(intention);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
