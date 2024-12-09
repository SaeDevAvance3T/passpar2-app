package com.example.passpar2;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Cette classe est un adaptateur pour gérer les fragments qui seront associés
 * au ViewPager. L'adaptateur gère les changements de fragment.
 * Dans sa version minimale, la classe contient :
 * - un constructeur auquel on passera en argument l'activité qui gère le ViewPager,
 * - une méthode createFragment et
 * - une méthode getItemCount (qui renvoie le nombre de fragments gérés)
 * @author C.Servières
 */
public class Accueil_adaptateur_fragments extends FragmentStateAdapter {
    /** Nombre de fragments gérés par cet adaptateur */
    private static final int NB_FRAGMENT = 2;
    /**
     * Constructeur de base
     * @param activite activité qui contient le ViewPager qui gèrera les fragments
     */
    public Accueil_adaptateur_fragments(FragmentActivity activite) {
        super(activite);
    }

    @Override
    public Fragment createFragment(int position) {
        /*
         * Le ViewPager auquel on associera cet adaptateur devra afficher
         * successivement un fragment de type : FragmentUn, puis FragmentDeux, et
         * enfin FragmentTrois. C'est dans cette méthode que l'on décide dans quel
         * ordre sont affichés les fragments, et quel fragment (nom de la classe)
         * doit précisément être affiché
         */
        switch(position) {
            case 0 :
                return Accueil_fragment_parcours.newInstance();
            case 1 :
                return Accueil_fragment_details.newInstance();
            default :
                return null;
        }
    }
    @Override
    public int getItemCount() {

        // renvoyer le nombre de fragments gérés par l'adaptateur
        return NB_FRAGMENT;
    }
} 