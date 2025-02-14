package com.example.passpar2;

import static android.widget.Toast.LENGTH_LONG;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class EditRoute extends MenuActivity implements CheckboxSelectionListener {

    /** Contient l'URL appelant l'API  */
    private final String URL_ENTERPRISES = "https://2bet.fr/api/customers?user=";

    private final String URL_ITINERARY = "https://2bet.fr/api/itineraries/";

    /**
     * File d'attente pour les requêtes Web (en lien avec l'utilisation de Volley)
     */
    private RequestQueue fileRequete;

    public EditText itineraryName;

    public TextView itineraryLabel;

    /**  Contient les entreprise sélectionnées par l'utilisateur avec les checkboxs */
    private ArrayList<Integer> selectedEnterprises;

    public ListView enterpriseList;

    /** Contient les entreprises sélectionnées pour l'itinéraire */
    public TextView displayedEnterprises;

    private CheckboxAdapterEdit adapter;
    public TextView textChoice;

    private ImageButton arrowBack;

    private Set<Integer> itineraryCustomerIds = new HashSet<>(); // Stocke les customerId récupérés de l'itinéraire
    private Map<Integer, String> enterpriseValues = new HashMap<>(); // Stocke les entreprises récupérées

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_route);

        itineraryName = findViewById(R.id.itinerary_name);

        itineraryLabel = findViewById(R.id.itinerary_label);

        enterpriseList = findViewById(R.id.enterpriselist);

        displayedEnterprises = findViewById(R.id.displayed_enterprises);

        textChoice = findViewById(R.id.text_choice);

        enterpriseValues = new HashMap<>();

        selectedEnterprises = new ArrayList<>();

        // Appeler la méthode pour désactiver la validation SSL
        SSLCertificate.disableSSLCertificateValidation();

        requestDatas();

        getEnterpriseList();

        adapter = new CheckboxAdapterEdit(this, enterpriseValues, selectedEnterprises, this);
        enterpriseList.setAdapter(adapter);

        arrowBack = findViewById(R.id.arrowBack);

        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // création d'une intention pour informer l'activité parente
                Intent intentionRetour = new Intent();
                setResult(Activity.RESULT_CANCELED, intentionRetour);
                finish(); // destruction de l'activité courante
            }
        });
    }

    public void getEnterpriseList() {

        // Vérifier la connexion Internet avant de lancer la requête
        if (!estConnecteInternet()) {
            return;  // Si pas de connexion, on ne fait rien
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);  // -1 est la valeur par défaut si l'ID n'est pas trouvé

        String urlGetEntreprise = URL_ENTERPRISES + userId;

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
                            // Effacer la liste des entreprises existantes
                            enterpriseValues.clear();

                            JSONArray enterpriseArray = response.getJSONArray("response");

                            for (int i = 0; i < enterpriseArray.length(); i++) {
                                JSONObject enterpriseJson = enterpriseArray.getJSONObject(i);

                                int id = enterpriseJson.optInt("id", -1); // Récupérer l'ID
                                String name = enterpriseJson.optString("name", "Nom non disponible");

                                if (id != -1) {
                                    enterpriseValues.put(id, name);
                                }
                            }

                            Log.d("getEnterpriseList", "Entreprises récupérées : " + enterpriseValues);

                            // Initialiser l'adapter et mettre à jour les cases cochées
                            adapter = new CheckboxAdapterEdit(EditRoute.this, enterpriseValues, itineraryCustomerIds);
                            enterpriseList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(EditRoute.this, "Erreur lors de la récupération des entreprises", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(EditRoute.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                        displayedEnterprises.setText("Aucun client disponible");
                        displayedEnterprises.setTextColor(Color.parseColor("#FF0000"));
                    }
                });
        // la requête est placée dans la file d'attente des requêtes
        getFileRequete().add(jsonObjectRequest);
    }

    /**
     *
     * @param view
     */
    public void editItinerary(View view) {

        Log.i("selectedEnterprises", "JSON envoyé : " + selectedEnterprises);
        if (selectedEnterprises.size() != 0) {
            if (!itineraryName.getText().toString().isEmpty()) {

                boolean toutOk;

                toutOk = true;
                JSONObject sentObject = new JSONObject();
                try {
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    int userId = sharedPreferences.getInt("userId", -1);  // -1 est la valeur par défaut si l'ID n'est pas trouvé
                    sentObject.put("userId", userId);
                    sentObject.put("name", itineraryName.getText().toString());
                    sentObject.put("itinerary", new JSONArray(selectedEnterprises));
                    Log.i("EditRouteActivity", "JSON envoyé : " + sentObject.toString());

                } catch (JSONException e) {
                    // l'exception ne doit pas se produire
                    toutOk = false;
                }
                if (toutOk) {
                    /*
                     * Préparation de la requête Volley. La réponse attendue est de type
                     * JsonObject
                     * REMARQUE : bien noter la présence du 3ème argument du constructeur qui est
                     * l'objet Json à transmettre avec la méthode POST, en fait le body de la
                     * requête
                     */

                    Intent intention = getIntent();
                    String itineraryId = intention.getStringExtra("itineraryId");

                    String urlPutItineraryDatas = URL_ITINERARY + itineraryId;

                    JsonObjectRequest requeteVolley = new JsonObjectRequest(Request.Method.PUT,
                            urlPutItineraryDatas, sentObject,
                            // Ecouteur pour la réception de la réponse de la requête
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject reponse) {
                                    // Créer un Intent pour renvoyer les données à MainActivity
                                    Intent intentionRetour = new Intent();
                                    // Renvoyer le résultat avec les données et terminer l'activité
                                    setResult(Activity.RESULT_OK, intentionRetour);
                                    finish();
                                    Toast.makeText(getApplicationContext(), "Itineraire modifié avec succès", Toast.LENGTH_SHORT).show();
                                }
                            },
                            // Ecouteur en cas d'erreur
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
                                    Toast.makeText(EditRoute.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                                }
                            })
                            // on ajoute un header, contenant la clé d'authentification
                    {
                        @Override
                        public Map getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Content-Type", "application/json");  // Ajout du Content-Type
                            return headers;
                        }
                    };
                    // ajout de la requête dans la file d'attente Volley
                    getFileRequete().add(requeteVolley);
                }
            } else {
                Toast.makeText(EditRoute.this, "Erreur: pas de nom d'itinéraire", LENGTH_LONG).show();
                Log.i("NewRouteActivity", "Erreur: pas de nom d'itinéraire");
                itineraryLabel.setTextColor(Color.parseColor("#FF0000"));
            }
        } else {
            if (itineraryName.getText().toString().isEmpty()) {
                itineraryLabel.setTextColor(Color.parseColor("#FF0000"));
                Log.e("NewRouteActivity", "Erreur: pas de nom d'itinéraire");
            } else {
                itineraryLabel.setTextColor(Color.parseColor("#FFFFFF"));
            }
            Toast.makeText(EditRoute.this, "Erreur: aucune entreprise sélectionnée", LENGTH_LONG).show();
            Log.e("NewRouteActivity", "Erreur: aucune entreprise sélectionnée");
            textChoice.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    private void requestDatas() {
        // Vérifier la connexion Internet avant de lancer la requête
        if (!estConnecteInternet()) {
            return;  // Si pas de connexion, on ne fait rien
        }

        Intent intention = getIntent();
        String itineraryId = intention.getStringExtra("itineraryId");

        String urlGetItineraryDatas = URL_ITINERARY + itineraryId;

        // Créer la requête GET
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, urlGetItineraryDatas, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Vider la liste avant d'ajouter de nouvelles données
                            itineraryCustomerIds.clear();

                            // Accéder à la clé "response" qui contient l'itinéraire
                            JSONObject responseData = response.getJSONObject("response");
                            String name = responseData.optString("name", "Nom non disponible");
                            JSONArray itineraryArray = responseData.getJSONArray("itinerary");

                            for (int i = 0; i < itineraryArray.length(); i++) {
                                JSONObject itineraryItem = itineraryArray.getJSONObject(i);
                                int customerId = itineraryItem.getInt("customerId"); // Extraire customerId
                                itineraryCustomerIds.add(customerId); // L'ajouter au Set
                            }

                            Log.d("requestDatas", "Customer IDs récupérés : " + itineraryCustomerIds);

                            itineraryName.setText(name);
                            // Une fois les customerId récupérés, appeler getEnterpriseList()
                            getEnterpriseList();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(EditRoute.this, "Erreur lors de la récupération des clients", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(EditRoute.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
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
     * Met à jour l'affichage des entreprises sélectionnées
     *
     */
    public void onCheckboxSelectionChanged(Set<Integer> selectedItems) {
        Log.d("MainActivity", "onCheckboxSelectionChanged exécuté avec : " + selectedItems);

        selectedEnterprises = new ArrayList<>();
        StringJoiner joiner = new StringJoiner(", ");

        if (!selectedItems.isEmpty()) {
            textChoice.setTextColor(Color.parseColor("#FFFFFF"));
            for (Integer key : selectedItems) {
                joiner.add(enterpriseValues.get(key));
                selectedEnterprises.add(key);
            }
            displayedEnterprises.setText(joiner.toString());
        } else {
            displayedEnterprises.setText("");
        }

        Log.i("MainActivity", "contenu de selectedEnterprises : " + selectedEnterprises);
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
}