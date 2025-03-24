package com.example.passpar2;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Accueil_fragment_en_cours extends Fragment {
    private RequestQueue fileRequete;
    private ListView listView;
    public ListView enterpriseList;
    private TextView itineraryLabel;

    private final String URL_ITINERARY = "https://2bet.fr/api/itineraries/";

    private static final String URL_COURSE = "https://2bet.fr/api/courses/";
    private final String URL_ENTERPRISES = "https://2bet.fr/api/customers/";
    private final String ITINERARY_LABEL_BLANK = "Nom de l'itinéraire : ";

    private List<Integer> customerIdList;

    /** Contient les entreprises sélectionnées pour l'itinéraire */
    public TextView displayedEnterprises;

    private EnterprisesItineraryDetails adapterEntreprise;

    private Map<Integer, String> enterpriseValues = new HashMap<>(); // Stocke les entreprises récupérées

    public static Accueil_fragment_en_cours newInstance() {
        return new Accueil_fragment_en_cours();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vueDuFragment = inflater.inflate(R.layout.accueil_fragment_en_cours, container, false);

        enterpriseList = vueDuFragment.findViewById(R.id.enterpriselist);
        displayedEnterprises = vueDuFragment.findViewById(R.id.displayed_enterprises);
        itineraryLabel = vueDuFragment.findViewById(R.id.itinerary_label);

        enterpriseValues = new HashMap<>();
        // Effacer la liste des entreprises existantes
        enterpriseValues.clear();

        customerIdList = new ArrayList<>();

        // Désactiver la validation SSL (si nécessaire)
        SSLCertificate.disableSSLCertificateValidation();

        //Charger les infos de l'itineraire demandé
        requestDatas();

        // Initialiser l'adapter avec des données vides pour éviter le crash
        adapterEntreprise = new EnterprisesItineraryDetails(getContext(), enterpriseValues);
        enterpriseList.setAdapter(adapterEntreprise);

        return vueDuFragment;
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
                            adapterEntreprise = new EnterprisesItineraryDetails(getContext(), enterpriseValues);
                            enterpriseList.setAdapter(adapterEntreprise);
                            adapterEntreprise.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Erreur lors de la récupération des entreprises", Toast.LENGTH_SHORT).show();
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

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String itineraryId = sharedPreferences.getString("currentItineraryId", null);

        Log.d("itineraryId", "itineraryId " + itineraryId);

        String urlGetItineraryDatas = URL_COURSE + itineraryId;

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
                            String itineraryName = responseData.optString("itineraryName", "Nom non disponible");
                            itineraryLabel.setText(ITINERARY_LABEL_BLANK + itineraryName);

                            // Effacer la liste des entreprises existantes
                            enterpriseValues.clear();

                            customerIdList.clear();

                            JSONArray itineraryArray = responseData.getJSONArray("points");
                            for (int i = 0; i < itineraryArray.length(); i++) {
                                JSONObject enterpriseJson = itineraryArray.getJSONObject(i);
                                Log.d("enterpriseJson", "enterpriseJson " + enterpriseJson);

                                int customerId = enterpriseJson.optInt("customerId", -1); // Récupérer l'ID
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
                    }
                });

        // Ajouter la requête à la file d'attente Volley
        getFileRequete().add(jsonObjectRequest);
    }

    private RequestQueue getFileRequete() {
        if (fileRequete == null) {
            fileRequete = Volley.newRequestQueue(getContext());
        }
        return fileRequete;
    }

    public boolean estConnecteInternet() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoReseau = cm.getActiveNetworkInfo();
        return infoReseau != null && infoReseau.isConnected();
    }
}
