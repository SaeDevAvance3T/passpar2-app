package com.example.passpar2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

public class NewRouteActivity extends AppCompatActivity {

    /** Contient l'URL appelant l'API  */
    private final String URL_ENTERPRISES = "blablabla";

    public static final String EXTRA_ENTERPRISE = "enterprise";

    /**
     * File d'attente pour les requêtes Web (en lien avec l'utilisation de Volley)
     */
    //private RequestQueue fileRequete;

    /**  Contient les entreprise sélectionnées par l'utilisateur avec les checkboxs */
    private ArrayList<String> selectedEnterprises;

    private ArrayList<String> enterpriseValues;

    public ListView enterpriseList;

    /** Contient les entreprises sélectionnées pour l'itinéraire */
    public TextView displayedEnterprises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_route_layout);

        enterpriseList = findViewById(R.id.enterpriselist);

        //getEnterpriseList();

        enterpriseValues = new ArrayList<String>(10);
        enterpriseValues.add("SOPRA");
        enterpriseValues.add("CA");
        enterpriseValues.add("AIRBUS");
        enterpriseValues.add("KHREA");
        enterpriseValues.add("SARL Hervé Lapeyre");
        enterpriseValues.add("SOPRA");
        enterpriseValues.add("SOPRA");
        enterpriseValues.add("SARL Hervé Lapeyre");
        enterpriseValues.add("SARL Hervé Lapeyre");
        enterpriseValues.add("SARL Hervé Lapeyre");
        enterpriseValues.add("SARL Hervé Lapeyre");
        enterpriseValues.add("SARL Hervé Lapeyre");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_enterprise, enterpriseValues);
        enterpriseList.setAdapter(adapter);
    }

    //public void getEnterpriseList() {
    //    /*
    //     * on crée une requête GET, paramètrée par l'url préparée ci-dessus,
    //     * Le résultat de cette requête sera une chaîne de caractères, donc la requête
    //     * est de type StringRequest
    //     */
    //    StringRequest requeteVolley = new StringRequest(Request.Method.GET, URL_ENTERPRISES,
    //            // écouteur de la réponse renvoyée par la requête
    //            new Response.Listener<String>() {
    //                @Override
    //                public void onResponse(String response) {
    //                    JSONTokener jsonToken = new JSONTokener(response);
    //                    try {
    //                        JSONObject jsonObject = (JSONObject) jsonToken.nextValue();
    //                        for (int i=0; i < jsonObject.getInt("Total"); i++) {
    //                            enterpriseValues.add(jsonObject.getString("\"" + i + "\""));
    //                        }
    //                    } catch(JSONException erreur) {
    //                        Log.i(TAG_LOG, "Problème lors de l'analyse JSON");
    //                    }
    //                }
    //            },
    //            // écouteur du retour de la requête si aucun résultat n'est renvoyé
    //            new Response.ErrorListener() {
    //                @Override
    //                public void onErrorResponse(VolleyError erreur) {
    //                    Log.i(TAG_LOG, "Problème lors de l'appel API: " + erreur);
    //                }
    //            });
    //    // la requête est placée dans la file d'attente des requêtes
    //    getFileRequete().add(requeteVolley);
    //}

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
    //private RequestQueue getFileRequete() {
    //    if (fileRequete == null) {
    //        fileRequete = Volley.newRequestQueue(this);
    //    }
    //    // sinon
    //    return fileRequete;
    //}

    public void displayEnterprise(View view) {
        Toast.makeText(this, "oh",Toast.LENGTH_SHORT);
//        CheckBox checkBox = findViewById(R.id.checkbox);
//        if (checkBox.isChecked()) {
//            selectedEnterprises.add(checkBox.getText().toString());
//        } else {
//            selectedEnterprises.remove(checkBox.getText().toString());
//        }
//        for(int i = 0; i < selectedEnterprises.size(); i++) {
//            displayedEnterprises.setText(selectedEnterprises.get(i) + ", ");
//        }

    }
}