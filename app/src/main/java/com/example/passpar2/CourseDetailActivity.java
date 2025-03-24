package com.example.passpar2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseDetailActivity extends MenuActivity {

    /** Contient l'URL appelant l'API  */
    private final String URL_ENTERPRISES = "https://2bet.fr/api/customers/";

    private final String URL_ITINERARY = "https://2bet.fr/api/itineraries/";

    private final String URL_START_COURSE = "https://2bet.fr/api/courses/";

    private final String ITINERARY_LABEL_BLANK = "Nom de l'itinéraire : ";

    /**
     * File d'attente pour les requêtes Web (en lien avec l'utilisation de Volley)
     */
    private RequestQueue fileRequete;

    public TextView itineraryLabel;

    public ListView enterpriseList;

    private List<Integer> customerIdList;

    /** Contient les entreprises sélectionnées pour l'itinéraire */
    public TextView displayedEnterprises;

    private EnterprisesItineraryDetails adapter;
    public TextView textChoice;

    private ImageButton arrowBack;

    private AppCompatButton resumeCourseButton;

    private Map<Integer, String> enterpriseValues = new HashMap<>(); // Stocke les entreprises récupérées

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_details);

        enterpriseList = findViewById(R.id.enterpriselist);
        displayedEnterprises = findViewById(R.id.displayed_enterprises);
        itineraryLabel = findViewById(R.id.itinerary_label);

        enterpriseValues = new HashMap<>();
        // Effacer la liste des entreprises existantes
        enterpriseValues.clear();

        customerIdList = new ArrayList<>();

        // Désactiver la validation SSL (si nécessaire)
        SSLCertificate.disableSSLCertificateValidation();

        //Charger les infos de l'itineraire demandé
        requestDatas();

        // Initialiser l'adapter avec des données vides pour éviter le crash
        adapter = new EnterprisesItineraryDetails(this, enterpriseValues);
        enterpriseList.setAdapter(adapter);

        findViewById(R.id.arrowBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentionRetour = new Intent();
                setResult(Activity.RESULT_CANCELED, intentionRetour);
                finish();
            }
        });

        resumeCourseButton = findViewById(R.id.retake_course);

        resumeCourseButton.setOnClickListener(v -> resumeCourse());
    }

    public void getEnterpriseData(int customerId) {

        // Vérifier la connexion Internet avant de lancer la requête
        if (!estConnecteInternet()) {
            return;  // Si pas de connexion, on ne fait rien
        }

        String urlGetEntreprise = URL_ENTERPRISES + customerId;

        Log.d("urlGetEntreprise", "urlGetEntreprise : " + urlGetEntreprise);

        /*
         * on crée une requête GET, paramètrée par l'url préparée ci-dessus,
         * Le résultat de cette requête sera une chaîne de caractères, donc la requête
         * est de type StringRequest
         */
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlGetEntreprise, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject enterpriseJson = response.getJSONObject("response");

                            String name = enterpriseJson.optString("name", "Nom non disponible");
                            JSONObject address = enterpriseJson.getJSONObject("address");
                            String fullAddress = address.optString("fullAddress", "Addresse non disponible");

                            if (customerId != -1 && customerId != 0) {
                                enterpriseValues.put(customerId, name + " - " + fullAddress);
                            }

                            Log.d("getEnterpriseList", "Entreprises récupérées : " + enterpriseValues);

                            // Initialiser l'adapter et mettre à jour les cases cochées
                            adapter = new EnterprisesItineraryDetails(CourseDetailActivity.this, enterpriseValues);
                            enterpriseList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(CourseDetailActivity.this, "Erreur lors de la récupération des entreprises", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error.networkResponse != null) {
                            // Si la réponse réseau est disponible, récupérer le code d'état et afficher les détails
                            Log.e("VolleyError", "Status Code: " + error.networkResponse.statusCode);
                            Log.e("VolleyError", "Response: " + new String(error.networkResponse.data));
                        } else {
                            // Si la réponse réseau est nulle, afficher un message d'erreur générique
                            Log.e("VolleyError", "Erreur réseau inconnue");
                        }
                        Toast.makeText(CourseDetailActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                        displayedEnterprises.setText("Aucun client disponible");
                        displayedEnterprises.setTextColor(Color.parseColor("#FF0000"));
                    }
                });
        // la requête est placée dans la file d'attente des requêtes
        getFileRequete().add(jsonObjectRequest);
    }

    private void requestDatas() {
        // Vérifier la connexion Internet avant de lancer la requête
        if (!estConnecteInternet()) {
            return;  // Si pas de connexion, on ne fait rien
        }

        Intent intention = getIntent();
        String itineraryId = intention.getStringExtra("itineraryId");

        Log.d("itineraryId", "itineraryId " + itineraryId);

        String urlGetItineraryDatas = URL_ITINERARY + itineraryId;

        Log.d("urlGetItineraryDatas", "urlGetItineraryDatas " + urlGetItineraryDatas);

        // Créer la requête GET
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlGetItineraryDatas, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            // Accéder à la clé "response" qui contient l'itinéraire
                            JSONObject responseData = response.getJSONObject("response");
                            String name = responseData.optString("name", "Nom non disponible");
                            itineraryLabel.setText(ITINERARY_LABEL_BLANK + name);

                            // Effacer la liste des entreprises existantes
                            enterpriseValues.clear();

                            customerIdList.clear();

                            JSONArray itineraryArray = responseData.getJSONArray("customersToVisit");
                            for (int i = 0; i < itineraryArray.length(); i++) {
                                JSONObject enterpriseJson = itineraryArray.getJSONObject(i);
                                Log.d("enterpriseJson", "enterpriseJson " + enterpriseJson);

                                int customerId = enterpriseJson.optInt("id", -1); // Récupérer l'ID
                                Log.d("customerId", "customerId " + customerId);

                                if (customerId != -1 && customerId != 0) {
                                    customerIdList.add(customerId);
                                    Log.d("customerIdList", "customerIdList " + customerId);
                                }
                            }

                            // Une fois les customerId récupérés, appeler getEnterpriseList()
                            for (int i = 0; i < customerIdList.size(); i++) {
                                Log.d("customerIdList.get(i)", "customerIdList.get(i) " + customerIdList.get(i));
                                getEnterpriseData(customerIdList.get(i));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(CourseDetailActivity.this, "Erreur lors de la récupération des clients", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error.networkResponse != null) {
                            // Si la réponse réseau est disponible, récupérer le code d'état et afficher les détails
                            Log.e("VolleyError", "Status Code: " + error.networkResponse.statusCode);
                            Log.e("VolleyError", "Response: " + new String(error.networkResponse.data));
                        } else {
                            // Si la réponse réseau est nulle, afficher un message d'erreur générique
                            Log.e("VolleyError", "Erreur réseau inconnue");
                        }
                        Toast.makeText(CourseDetailActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                    }
                });

        // Ajouter la requête à la file d'attente Volley
        getFileRequete().add(jsonObjectRequest);
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
            fileRequete = Volley.newRequestQueue(this);
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

    public void resumeCourse() {
        Intent intention = getIntent();
        String courseId = intention.getStringExtra("courseId");

        // Vérifier la connexion Internet avant de lancer la requête
        if (!estConnecteInternet()) {
            return;  // Si pas de connexion, on ne fait rien
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("courseId", courseId); // Stocke l'ID de l'itinéraire
        editor.apply();

        // Création et lancement de l'intention avec l'ID récupéré
        Intent intentionCourse = new Intent(CourseDetailActivity.this, Accueil_main.class);
        intentionCourse.putExtra("courseId", courseId);
        startActivity(intentionCourse);
    }
}