package com.example.passpar2;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class NewRouteActivity extends AppCompatActivity implements CheckboxSelectionListener {

    /** Contient l'URL appelant l'API  */
    private final String URL_ENTERPRISES = "https://2bet.fr/api/customers?user=0";

    /** Clé pour le nombre transmis par l'activité fille */
    public final static String CLE_NOMBRE = "NOMBRE";

    public static final String EXTRA_ENTERPRISE = "enterprise";

    /**
     * File d'attente pour les requêtes Web (en lien avec l'utilisation de Volley)
     */
    private RequestQueue fileRequete;

    /**  Contient les entreprise sélectionnées par l'utilisateur avec les checkboxs */
    private ArrayList<Integer> selectedEnterprises;

    private HashMap<Integer,String> enterpriseValues;

    public ListView enterpriseList;

    /** Contient les entreprises sélectionnées pour l'itinéraire */
    public TextView displayedEnterprises;

    private CheckboxAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_route_layout);

        enterpriseList = findViewById(R.id.enterpriselist);

        displayedEnterprises = findViewById(R.id.displayed_enterprises);

        enterpriseValues = new HashMap<>();

        // Désactiver la vérification du certificat SSL pour les tests
        TrustManager[] trustAllCertificates = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Installer un gestionnaire de confiance pour tous les certificats
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCertificates, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //enterpriseValues.put(1, "Sopra");
        //enterpriseValues.put(2, "CA");
        //enterpriseValues.put(3, "Ledger");
        //enterpriseValues.put(4, "SCH");
        //enterpriseValues.put(5, "Freeze Corleone");
        //enterpriseValues.put(6, "Thomas");
        //enterpriseValues.put(7, "Jérome");
        //enterpriseValues.put(8, "Jérome");

        getEnterpriseList();

        adapter = new CheckboxAdapter(this, enterpriseValues, this);
        enterpriseList.setAdapter(adapter);

        /*
         * Lorsque l'utilisateur cliquera sur la touche back du téléphone pour revenir
         * vers l'activité parente, on souhaite qu'un traitement bien précis soit réalisé.
         * Il faut donc ajouter un callBack (une méthode de rappel) qui sera appelée lorsque
         * l'utilisateur cliquera sur back. Le callBack est ajouté à un "distributeur" qui
         * gère les appuis sur la touche back.
         */
        //getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
        //    @Override
        //    /**
        //     * La méthode handleOnBackPressed sera appelée automatiquement lorsque
        //     * l'utilisateur cliquera sur la touche back du téléphone.
        //     * On souhaite, à titre d'illustration :
        //     * - renvoyer à l'activité principale le nombre 9999999 (et pas celui saisi
        //     * par l'utilisateur)
        //     * - de plus on considère que le retour est "normal", le code retour renvoyé
        //     * à l'activité principale sera donc RESULT_OK
        //     */
        //    public void handleOnBackPressed() {
        //        // création d'une intention pour informer l'activté parente
        //        Intent intentionRetour = new Intent();
        //        // retour à l'activité parente et destruction de l'activité fille
        //        intentionRetour.putExtra(NewRouteActivity.CLE_NOMBRE, 999999);
        //        setResult(Activity.RESULT_OK, intentionRetour);
        //        finish(); // destruction de l'activité courante
        //    }
        //});
    }

    public void getEnterpriseList() {

        // Vérifier la connexion Internet avant de lancer la requête
        if (!estConnecteInternet()) {
            return;  // Si pas de connexion, on ne fait rien
        }

        /*
         * on crée une requête GET, paramètrée par l'url préparée ci-dessus,
         * Le résultat de cette requête sera une chaîne de caractères, donc la requête
         * est de type StringRequest
         */
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, URL_ENTERPRISES, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Effacer la liste des clients existante
                        enterpriseValues.clear();
                        try {
                            // Accéder à la clé 'response' qui contient le tableau des clients
                            JSONArray enterpriseArray = response.getJSONArray("response");

                            // Parcourir la réponse JSON pour extraire les données
                            for (int i = 0; i < enterpriseArray.length(); i++) {
                                JSONObject clientJson = enterpriseArray.getJSONObject(i);

                                // Récupérer le nom et la description du client
                                String id = clientJson.optString("id", "Id non disponible");
                                String name = clientJson.optString("name", "Nom de l'entreprise non disponible");

                                // Ajouter les clients dans la liste
                                enterpriseValues.put(Integer.parseInt(id), name);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(NewRouteActivity.this, "Erreur lors de la récupération des entreprises", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(NewRouteActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                    }
                });
        // la requête est placée dans la file d'attente des requêtes
        getFileRequete().add(jsonObjectRequest);
    }

    /**
     *
     * @param view
     */
    public void checkClick(View view) {

        if (selectedEnterprises.size() != 0) {
            //Intent intention = new Intent(NewRouteActivity.class, )
            //intention.putExtra(EXTRA_ENTERPRISE, selectedEnterprises);
        }
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

    // Met à jour la TextView avec les Checkbox sélectionnées
    @Override
    public void onCheckboxSelectionChanged(Set<Integer> selectedItems) {
        Log.d("MainActivity", "onCheckboxSelectionChanged exécuté avec : " + selectedItems);
        StringBuilder textSelectedEnterprises = new StringBuilder();
        selectedEnterprises = new ArrayList<>();
        if (selectedItems.size() != 0) {
            for (Integer key : selectedItems) {
                textSelectedEnterprises.append(enterpriseValues.get(key)).append(", ");
                selectedEnterprises.add(key);
            }
            displayedEnterprises.setText(textSelectedEnterprises);
        } else {
            displayedEnterprises.setText("");
        }
        Log.d("MainActivity", "contenu de selectedEnterprises : " + selectedEnterprises);

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