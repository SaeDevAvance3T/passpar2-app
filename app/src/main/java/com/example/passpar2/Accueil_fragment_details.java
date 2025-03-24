package com.example.passpar2;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Accueil_fragment_details extends Fragment {
    private RequestQueue fileRequete;
    private RecyclerView recyclerView;
    private ItinerarieSelection_RecyclerView adapter;
    private List<String> itineraries;
    private List<String> idItineraries;

    private String url = "https://2bet.fr/api/itineraries/user/"; // URL des itinéraires

    private ActivityResultLauncher<Intent> lanceurAdapter;

    private AppCompatButton savedCoursesButton;

    public static Accueil_fragment_details newInstance() {
        return new Accueil_fragment_details();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vueDuFragment = inflater.inflate(R.layout.accueil_fragment_details, container, false);

        SSLCertificate.disableSSLCertificateValidation();

        itineraries = new ArrayList<>();
        idItineraries = new ArrayList<>();

        recyclerView = vueDuFragment.findViewById(R.id.itineraries_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Gestion du clic sur un itinéraire pour afficher les détails
        lanceurAdapter = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Gestion du résultat si nécessaire
                }
        );

        savedCoursesButton = vueDuFragment.findViewById(R.id.saved_courses);

        vueDuFragment.findViewById(R.id.saved_courses).setOnClickListener(v -> {
            Intent intention = new Intent(getActivity(), CoursesList.class); // Change Clients_creer avec l'activité appropriée
            lanceurAdapter.launch(intention); // Utiliser lanceurAdapter pour démarrer l'activité
        });


        adapter = new ItinerarieSelection_RecyclerView(itineraries, idItineraries, lanceurAdapter);
        recyclerView.setAdapter(adapter);

        fetchItineraries();

        /*
        vueDuFragment.findViewById(R.id.courses_create_button).setOnClickListener(v -> {
            Intent intention = new Intent(getActivity(), NewCourseActivity.class);
            startActivity(intention);
        });*/

        return vueDuFragment;
    }

    private RequestQueue getFileRequete() {
        if (fileRequete == null) {
            fileRequete = Volley.newRequestQueue(getContext(), new GestionProxy());
        }
        return fileRequete;
    }

    public boolean estConnecteInternet() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoReseau = cm.getActiveNetworkInfo();
        return infoReseau != null && infoReseau.isConnected();
    }

    private void fetchItineraries() {
        if (!estConnecteInternet()) {
            Toast.makeText(getContext(), "Pas de connexion Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);
        String usedUrl = url + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, usedUrl, null,
                response -> {
                    itineraries.clear();
                    idItineraries.clear();
                    try {
                        JSONArray itinerariesArray = response.getJSONArray("response");
                        for (int i = 0; i < itinerariesArray.length(); i++) {
                            JSONObject itineraryJson = itinerariesArray.getJSONObject(i);
                            String name = itineraryJson.optString("name", "Nom non disponible");
                            String id = itineraryJson.optString("id", "Id non disponible");
                            itineraries.add(name);
                            idItineraries.add(id);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("VolleyError", "Erreur réseau", error);
                }
        );
        getFileRequete().add(jsonObjectRequest);
    }
}
