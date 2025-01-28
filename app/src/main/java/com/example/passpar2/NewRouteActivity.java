package com.example.passpar2;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

public class NewRouteActivity extends AppCompatActivity implements CallbackListener {

    /** Contient l'URL appelant l'API  */
    private final String URL_ENTERPRISES = "blablabla";

    /** Clé pour le nombre transmis par l'activité fille */
    public final static String CLE_NOMBRE = "NOMBRE";

    public static final String EXTRA_ENTERPRISE = "enterprise";

    /**
     * File d'attente pour les requêtes Web (en lien avec l'utilisation de Volley)
     */
    private RequestQueue fileRequete;

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

        getEnterpriseList();

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

        //if (enterpriseValues.size() == 0) {

        //}

        CheckboxAdapter adapter = new CheckboxAdapter(this, enterpriseValues, this);
        enterpriseList.setAdapter(adapter);

        /*
         * Lorsque l'utilisateur cliquera sur la touche back du téléphone pour revenir
         * vers l'activité parente, on souhaite qu'un traitement bien précis soit réalisé.
         * Il faut donc ajouter un callBack (une méthode de rappel) qui sera appelée lorsque
         * l'utilisateur cliquera sur back. Le callBack est ajouté à un "distributeur" qui
         * gère les appuis sur la touche back.
         */
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            /**
             * La méthode handleOnBackPressed sera appelée automatiquement lorsque
             * l'utilisateur cliquera sur la touche back du téléphone.
             * On souhaite, à titre d'illustration :
             * - renvoyer à l'activité principale le nombre 9999999 (et pas celui saisi
             * par l'utilisateur)
             * - de plus on considère que le retour est "normal", le code retour renvoyé
             * à l'activité principale sera donc RESULT_OK
             */
            public void handleOnBackPressed() {
                // création d'une intention pour informer l'activté parente
                Intent intentionRetour = new Intent();
                // retour à l'activité parente et destruction de l'activité fille
                intentionRetour.putExtra(NewRouteActivity.CLE_NOMBRE, 999999);
                setResult(Activity.RESULT_OK, intentionRetour);
                finish(); // destruction de l'activité courante
            }
        });
    }

    public void getEnterpriseList() {
        /*
         * on crée une requête GET, paramètrée par l'url préparée ci-dessus,
         * Le résultat de cette requête sera une chaîne de caractères, donc la requête
         * est de type StringRequest
         */
        StringRequest requeteVolley = new StringRequest(Request.Method.GET, URL_ENTERPRISES,
                // écouteur de la réponse renvoyée par la requête
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONTokener jsonToken = new JSONTokener(response);
                        try {
                            JSONObject jsonObject = (JSONObject) jsonToken.nextValue();
                            for (int i=0; i < jsonObject.getInt("Total"); i++) {
                                enterpriseValues.add(jsonObject.getString("\"" + i + "\""));
                            }
                        } catch(JSONException erreur) {

                        }
                    }
                },
                // écouteur du retour de la requête si aucun résultat n'est renvoyé
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError erreur) {

                    }
                });
        // la requête est placée dans la file d'attente des requêtes
        getFileRequete().add(requeteVolley);
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

    // Implémentation du callback
    @Override
    public void onCheckboxClicked(String checkboxContent, boolean isChecked) {
        // Afficher un message ou effectuer une action
        String message = checkboxContent + " est " + (isChecked ? "cochée" : "décochée");
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        // TODO ajouter ou retirer l'entreprise à la liste des entreprises sélectionnées en temps réel
        //if (isChecked) {
        //    selectedEnterprises.add(checkboxContent);
        //} else {
        //    selectedEnterprises.remove(checkboxContent);
        //}
        //for(int i = 0; i < selectedEnterprises.size(); i++) {
        //    displayedEnterprises.setText(selectedEnterprises.get(i) + ", ");
        //}
    }
}