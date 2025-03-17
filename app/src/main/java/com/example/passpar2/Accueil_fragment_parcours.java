package com.example.passpar2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class Accueil_fragment_parcours extends Fragment {

    private MapView mapView;

    public static Accueil_fragment_parcours newInstance() {
        return new Accueil_fragment_parcours();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Charger la configuration OSM
        Configuration.getInstance().load(requireContext(),
                requireContext().getSharedPreferences("osmdroid", Context.MODE_PRIVATE));

        // Inflater la vue du fragment
        View view = inflater.inflate(R.layout.accueil_fragment_parcours, container, false);

        // Récupérer la MapView
        mapView = view.findViewById(R.id.map);

        // Configurer la carte
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getController().setZoom(12.0);

        // Centrer la carte sur Paris (par exemple)
        GeoPoint startPoint = new GeoPoint(48.8566, 2.3522);
        mapView.getController().setCenter(startPoint);

        // Ajouter un marqueur
        Marker marker = new Marker(mapView);
        marker.setPosition(startPoint);
        marker.setTitle("Je suis ici !");
        mapView.getOverlays().add(marker);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume(); // Obligatoire pour OSM
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause(); // Obligatoire pour OSM
    }
}
