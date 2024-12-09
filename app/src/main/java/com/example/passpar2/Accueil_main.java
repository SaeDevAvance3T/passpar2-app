package com.example.passpar2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.passpar2.Accueil_adaptateur_fragments;
import com.example.passpar2.R;

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
        // on récupère un accès sur le ViewPager défini dans la vue (le fichier layout)
        ViewPager2 pager = findViewById(R.id.activity_main_viewpager);
        /*
         * on associe au ViewPager un adaptateur (c'est lui qui organise le
         * défilement entre les fragments à afficher)
         * La classe AdaptateurPage a été codée par le développeur (elle hérite de
         * FragmentStateAdapter)
         */
        pager.setAdapter(new Accueil_adaptateur_fragments(this)) ;
    }
}