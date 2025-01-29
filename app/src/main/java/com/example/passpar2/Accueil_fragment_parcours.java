package com.example.passpar2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

/**
 * Classe qui gère un fragment.
 * Le fragment comporte 2 boutons : l'un pour générer un nombre aléatoire, l'autre
 * pour effacer le nombre généré
 * @author C. Servières
 */
public class Accueil_fragment_parcours extends Fragment implements View.OnClickListener {
    /** Zone pour afficher le nombre aléatoire généré */
    private TextView zoneResultat;

    /**
     * Cette méthode est une "factory" : son rôle est de créer une nouvelle instance
     * du fragment de type FragmentUn.
     * @return A new instance of fragment FragmentUn.
     */
    public static Accueil_fragment_parcours newInstance() {
        Accueil_fragment_parcours fragment = new Accueil_fragment_parcours();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);  // Désactive la gestion du menu dans le fragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // On récupère la vue (le layout) associée au fragment un
        View vueDuFragment = inflater.inflate(R.layout.accueil_fragment_parcours, container, false);

        return vueDuFragment;
    }

    @Override
    public void onClick(View view) {
        // Gestion des clics si nécessaire
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
