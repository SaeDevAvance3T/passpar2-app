package com.example.passpar2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * Classe qui gère un fragment.
 * Le fragment comporte 2 boutons : l'un pour générer un nombre aléatoire, l'autre
 * pour effacer le nombre généré
 * @author C. Servières
 */
public class Accueil_fragment_details extends Fragment implements View.OnClickListener {
    /** Zone pour afficher le nombre aléatoire généré */
    private TextView zoneResultat;
    /**
     * Cette méthode est une "factory" : son rôle est de créer une nouvelle instance
     * du fragment de type FragmentUn
     * @return A new instance of fragment FragmentUn.
     */
    public static Accueil_fragment_details newInstance() {
        Accueil_fragment_details fragment = new Accueil_fragment_details();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // On récupère la vue (le layout) associée au fragment un
        View vueDuFragment = inflater.inflate(R.layout.accueil_fragment_details, container, false);

        return vueDuFragment;
    }

    @Override
    public void onClick(View view) {

    }
}