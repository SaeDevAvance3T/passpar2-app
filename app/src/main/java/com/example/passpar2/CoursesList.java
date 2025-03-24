package com.example.passpar2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CoursesList extends MenuActivity {

    private RequestQueue fileRequete;
    private RecyclerView recyclerView;
    private CourseSelection_RecyclerView adapter;
    private List<String> itineraries;
    private List<String> idItineraries;
    private List<String> idCourses;

    private String url = "https://2bet.fr/api/courses/user/";  // URL de l'API pour les itinéraires

    // Ajout dans CoursesList
    private ActivityResultLauncher<Intent> lanceurAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_list);

        // Configuration de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Ajout du lanceur d'intention
        lanceurAdapter = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Traite le résultat de l'activité ici si nécessaire
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            // Récupérer les données retournées par l'activité (par exemple, l'ID de l'itinéraire)
                            String itineraryId = data.getStringExtra("itineraryId");
                            // Traiter l'ID de l'itinéraire si nécessaire
                        }
                    }
                });

        // Initialisation des listes d'itinéraires et de leurs identifiants
        itineraries = new ArrayList<>();
        idItineraries = new ArrayList<>();
        idCourses = new ArrayList<>();

        // Configuration du RecyclerView
        recyclerView = findViewById(R.id.itineraries_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CourseSelection_RecyclerView(itineraries, idItineraries,idCourses, lanceurAdapter);
        recyclerView.setAdapter(adapter);

        // Appel de la méthode pour récupérer les itinéraires
        fetchItineraries();
    }

    // Ajoute cette méthode dans la classe CoursesList
    private String formatDate(String dateString) {
        // Format de la date d'entrée : "2025-03-24T08:56:55.56"
        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SS";

        // Format de la date de sortie
        String outputPattern = "dd MMMM yyyy, HH:mm";

        try {
            // Créer un SimpleDateFormat pour analyser la date d'entrée
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.getDefault());
            Date date = inputFormat.parse(dateString);  // Convertir la chaîne en objet Date

            // Créer un SimpleDateFormat pour formater la date de sortie
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.getDefault());

            // Retourner la date formatée
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "Date invalide"; // En cas d'erreur, retourner un message d'erreur
        }
    }

    // Méthode pour récupérer les itinéraires via l'API
    private void fetchItineraries() {
        // Vérifier la connexion Internet avant de lancer la requête
        if (!estConnecteInternet()) {
            return;  // Si pas de connexion, on ne fait rien
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);  // -1 est la valeur par défaut si l'ID n'est pas trouvé

        String usedUrl = url + userId;  // Construire l'URL pour l'appel API

        // Créer la requête GET
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, usedUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Effacer les anciennes données avant de les remplir à nouveau
                        itineraries.clear();
                        idItineraries.clear();
                        idCourses.clear();
                        try {
                            // Accéder à la clé 'response' qui contient le tableau des itinéraires
                            JSONArray itinerariesArray = response.getJSONArray("response");

                            // Parcourir la réponse JSON pour extraire les itinéraires
                            for (int i = 0; i < itinerariesArray.length(); i++) {
                                JSONObject itineraryJson = itinerariesArray.getJSONObject(i);

                                // Récupérer les informations de l'itinéraire
                                String name = itineraryJson.optString("itineraryName", "Nom non disponible");
                                String idItinerary = itineraryJson.optString("itineraryId", "Id non disponible");
                                String idCourse = itineraryJson.optString("id", "Id non disponible");
                                String date = itineraryJson.optString("createdAt","Date nom disponible");

                                // Formater la date pour l'afficher correctement
                                String formattedDate = formatDate(date);
                                String texte = name + " - " + formattedDate;

                                // Ajouter les itinéraires dans la liste
                                itineraries.add(texte);
                                idCourses.add(idCourse);
                                idItineraries.add(idItinerary);
                            }

                            // Mettre à jour l'adapter pour refléter les changements
                            adapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(CoursesList.this, "Erreur lors de la récupération des itinéraires", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(CoursesList.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                    }
                });

        // Ajouter la requête à la file d'attente Volley
        getFileRequete().add(jsonObjectRequest);
    }

    // Renvoie la file d'attente pour les requêtes Web
    private RequestQueue getFileRequete() {
        if (fileRequete == null) {
            fileRequete = Volley.newRequestQueue(this);
        }
        return fileRequete;
    }

    // Vérifie la connexion Internet
    public boolean estConnecteInternet() {
        ConnectivityManager gestionnaireConnexion = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoReseau = gestionnaireConnexion.getActiveNetworkInfo();
        return infoReseau != null && infoReseau.isConnected();
    }
}
