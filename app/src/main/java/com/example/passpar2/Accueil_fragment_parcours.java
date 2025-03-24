package com.example.passpar2;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Accueil_fragment_parcours extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String API_KEY = "5b3ce3597851110001cf6248c08c820b5b4b4e5b9f090b12ec73f770";

    private static final String URL_CUSTOMER = "https://2bet.fr/api/customers/";

    private static final String URL_ITINERARY = "https://2bet.fr/api/itineraries/";

    private static final String URL_COURSE = "https://2bet.fr/api/courses/";

    private static final String URL_VISIT = "/points/";

    private static final String END_VISITED = "/visited";

    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private RequestQueue requestQueue;
    private List<GeoPoint> itineraryPoints = new ArrayList<>();
    private List<Integer> itineraryCustomers = new ArrayList<>();
    private GeoPoint userLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private IMapController mapController;
    private int currentClientIndex = 0;
    private TextView clientName;
    private TextView clientAddress;
    private AppCompatButton visitButton, cancelButton;

    private String itineraryId;

    private String courseId;

    public static Accueil_fragment_parcours newInstance(String itineraryId) {
        Accueil_fragment_parcours fragment = new Accueil_fragment_parcours();
        Bundle args = new Bundle();
        args.putString("itineraryId", itineraryId);
        //args.putString("courseId", courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", MODE_PRIVATE));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.accueil_fragment_parcours, container, false);
        Context ctx = requireContext();
        Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", MODE_PRIVATE));

        File osmdroidBasePath = new File(ctx.getCacheDir(), "osmdroid");
        Configuration.getInstance().setOsmdroidBasePath(osmdroidBasePath);
        Configuration.getInstance().setOsmdroidTileCache(osmdroidBasePath);

        SSLCertificate.disableSSLCertificateValidation();

        // Initialisation de la carte
        mapView = view.findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapController = mapView.getController();
        mapController.setZoom(15.0);

        // Overlay pour la position de l'utilisateur
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation(); // Active le suivi de position
        mapView.getOverlays().add(myLocationOverlay);

        // Initialisation de la localisation Google Play Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        requestLocationUpdates();

        clientName = view.findViewById(R.id.client_name);
        clientAddress = view.findViewById(R.id.client_address);
        visitButton = view.findViewById(R.id.btn_mark_visit);
        cancelButton = view.findViewById(R.id.btn_cancel_visit);

        visitButton.setOnClickListener(v -> markVisitAndMoveToNextClient());
        cancelButton.setOnClickListener(v -> moveToNextClient());

        // Initialisation de Volley pour les requêtes API
        requestQueue = Volley.newRequestQueue(ctx);

        if (getArguments() != null) {
            itineraryId = getArguments().getString("itineraryId");
            //courseId = getArguments().getString("courseId");
            Log.d("FragmentParcours", "Itinerary ID reçu: " + itineraryId);
        }

        // Vérifie `SharedPreferences` seulement si `itineraryId` est encore null
        if (itineraryId == null) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
            itineraryId = sharedPreferences.getString("currentItineraryId", null);
        }

        if (itineraryId == null) {
            Toast.makeText(getContext(), "Aucun itinéraire en cours", Toast.LENGTH_SHORT).show();
        } else {
            fetchItineraryData();
        }

        /*
        // Vérifie `SharedPreferences` seulement si `itineraryId` est encore null
        if (courseId == null) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
            courseId = sharedPreferences.getString("courseId", null);
        }

        /*
        if (courseId == null) {
            Toast.makeText(getContext(), "Aucun itinéraire en cours", Toast.LENGTH_SHORT).show();
        } else {
            fetchItineraryData();
        }

         */


        fetchItineraryData();

        return view;
    }

    private void markVisitAndMoveToNextClient() {
        if (currentClientIndex < itineraryCustomers.size()) {
            int customerId = itineraryCustomers.get(currentClientIndex);
            String visitUrl = URL_COURSE + itineraryId + URL_VISIT + customerId + END_VISITED;

            Log.d("markVisit", "Visite en cours pour client ID: " + customerId);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, visitUrl, null,
                    response -> {
                        Toast.makeText(getContext(), "Visite marquée", Toast.LENGTH_SHORT).show();
                        moveToNextClient();
                    },
                    error -> {
                        Log.e("markVisit", "Erreur lors de la marque de visite: " + error.toString());
                        Toast.makeText(getContext(), "Erreur de validation", Toast.LENGTH_SHORT).show();
                    });

            requestQueue.add(request);
        }
    }

    private void moveToNextClient() {
        Log.d("moveToNextClient", "Index actuel: " + currentClientIndex + " / " + (itineraryCustomers.size() - 1));

        if (currentClientIndex < itineraryCustomers.size() - 1) {
            currentClientIndex++;
            Log.d("moveToNextClient", "Client suivant index: " + currentClientIndex);
            updateNextClientInfo();
        } else {
            clientName.setText("Tous les clients ont été visités.");
            clientAddress.setText("");
        }
    }

    private void updateNextClientInfo() {
        Log.d("updateNextClientInfo", "Mise à jour du client index: " + currentClientIndex);

        if (currentClientIndex < itineraryCustomers.size()) {
            int nextCustomerId = itineraryCustomers.get(currentClientIndex);
            if (nextCustomerId == 0){
                moveToNextClient();
            }

            Log.d("updateNextClientInfo", "Client suivant ID: " + nextCustomerId);

            String usedUrl = URL_CUSTOMER + nextCustomerId;
            Log.d("updateNextClientInfo", "Requête client: " + usedUrl);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, usedUrl, null,
                    response -> {
                        try {
                            JSONObject responseObject = response.getJSONObject("response");
                            String customerName = responseObject.getString("name");
                            JSONObject address = responseObject.getJSONObject("address");
                            String fullAddress = address.getString("fullAddress");

                            clientName.setText("Prochain client : " + customerName);
                            clientAddress.setText(fullAddress);
                        } catch (JSONException e) {
                            Log.e("updateNextClientInfo", "Erreur parsing JSON: " + e.getMessage());
                        }
                    },
                    error -> Log.e("updateNextClientInfo", "Erreur API: " + error.toString()));

            requestQueue.add(request);
        } else {
            clientName.setText("Tous les clients ont été visités.");
            clientAddress.setText("");
        }
    }

    private void fetchRoutedItinerary() {
        if (itineraryPoints.size() < 2) return;

        for (int i = 0; i < itineraryPoints.size() - 1; i++) {
            GeoPoint start = itineraryPoints.get(i);
            GeoPoint end = itineraryPoints.get(i + 1);
            fetchSegmentRoute(start, end);
        }
    }

    private void fetchSegmentRoute(GeoPoint start, GeoPoint end) {
        String url = "https://api.openrouteservice.org/v2/directions/driving-car?api_key=" + API_KEY +
                "&start=" + start.getLongitude() + "," + start.getLatitude() +
                "&end=" + end.getLongitude() + "," + end.getLatitude();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray features = response.getJSONArray("features");
                        if (features.length() == 0) return;

                        JSONObject geometry = features.getJSONObject(0).getJSONObject("geometry");
                        JSONArray coordinates = geometry.getJSONArray("coordinates");

                        List<GeoPoint> routedPoints = new ArrayList<>();
                        for (int i = 0; i < coordinates.length(); i++) {
                            JSONArray point = coordinates.getJSONArray(i);
                            double lon = point.getDouble(0);
                            double lat = point.getDouble(1);
                            routedPoints.add(new GeoPoint(lat, lon));
                        }

                        drawRoutedItinerary(routedPoints);
                    } catch (JSONException e) {
                        Log.e("OSMDroid", "Erreur parsing ORS: " + e.getMessage());
                    }
                },
                error -> Log.e("OSMDroid", "Erreur API ORS: " + error.toString()));

        requestQueue.add(request);
    }

    private void drawRoutedItinerary(List<GeoPoint> routedPoints) {
        if (routedPoints.isEmpty()) return;

        Polyline polyline = new Polyline();
        polyline.setPoints(routedPoints);
        polyline.setWidth(10f);
        Context context = requireContext();
        polyline.setColor(context.getResources().getColor(R.color.black));
        mapView.getOverlays().add(polyline);
        mapView.invalidate();
    }

    private void centerMapOnUser() {
        if (userLocation != null) {
            IMapController mapController = mapView.getController();
            mapController.setCenter(userLocation);
            mapController.setZoom(15.0);
        }
    }


    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
            } else {
                Log.e("OSMDroid", "Impossible de récupérer la position de l'utilisateur");
            }
        });
    }



    private void addMarker(GeoPoint point, String title, String type) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setTitle(title);

        // Charger l'icône personnalisée en tant que Bitmap
        BitmapDrawable drawable;
        if (type == "client"){
            drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.customer);
        }else if (type == "prospect"){
            drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.prospect);
        }else if (type == "domicile"){
            drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.home);
        }else {
            drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.skip);
        }

        Bitmap originalBitmap = drawable.getBitmap();

        // Définir la taille maximale et minimale de l'icône (par exemple, 100x100px et 50x50px)
        int maxWidth = 100;
        int maxHeight = 100;
        int minWidth = 50;
        int minHeight = 50;

        // Calculer la nouvelle taille tout en gardant le ratio d'origine
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        float ratio = (float) width / height;

        if (width > height) {
            width = maxWidth;
            height = (int) (width / ratio);
        } else {
            height = maxHeight;
            width = (int) (height * ratio);
        }

        // Appliquer la taille minimale
        if (width < minWidth) {
            width = minWidth;
            height = (int) (width / ratio);
        }
        if (height < minHeight) {
            height = minHeight;
            width = (int) (height * ratio);
        }

        // Redimensionner l'image pour qu'elle corresponde à la taille maximale et minimale
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);

        // Appliquer le Bitmap redimensionné au marqueur
        marker.setIcon(new BitmapDrawable(getResources(), resizedBitmap));

        // Définir l'ancrage du marqueur
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        // Ajouter le marqueur à la carte
        mapView.getOverlays().add(marker);
    }



    private void fetchItineraryData() {
        if (itineraryId == null || itineraryId.isEmpty()) {
            Log.e("OSMDroid", "Aucun ID d'itinéraire reçu, requête annulée.");
            return; // Ne fait rien si l'ID est absent
        }

        String usedUrl = URL_COURSE + itineraryId;

        /*
        if ((itineraryId == null || itineraryId.isEmpty()) && (courseId != null || !courseId.isEmpty())){
            usedUrl += courseId;
        }else {
            usedUrl += itineraryId;
        }*/

        Log.d("urlCourse", "urlCourse " + usedUrl);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, usedUrl, null,
                response -> {
                    try {
                        JSONArray itinerary = response.getJSONObject("response").getJSONArray("points");

                        itineraryPoints.clear();
                        itineraryCustomers.clear();

                        for (int i = 0; i < itinerary.length(); i++) {
                            JSONObject point = itinerary.getJSONObject(i);
                            JSONObject coordinates = point.getJSONObject("coordinates");
                            double latitude = coordinates.getDouble("latitude");
                            double longitude = coordinates.getDouble("longitude");

                            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                            itineraryPoints.add(geoPoint);

                            Log.d("OSMDroid", "Point " + (i + 1) + ": Lat = " + latitude + ", Lon = " + longitude);

                            int customerId;
                            if (point.has("customerId") && !point.isNull("customerId")) {
                                customerId = point.getInt("customerId");
                            } else {
                                customerId = -99; //Domicile
                            }
                            itineraryCustomers.add(customerId);

                            // Capture `i` dans une variable finale ou une copie locale
                            final int pointIndex = i;  // Copie locale de `i`

                            // Faire l'appel API pour récupérer le type d'utilisateur avec le customerId
                            fetchUserType(customerId, new CustomerTypeCallback() {
                                @Override
                                public void onCustomerTypeReceived(String customerType) {
                                    // Maintenant vous avez le type d'utilisateur pour ce customerId
                                    Log.d("OSMDroid", "Customer " + customerId + " is of type: " + customerType);

                                    // Utilisez le pointIndex ici pour que l'index soit correct
                                    addMarker(geoPoint, "Point " + (pointIndex + 1), customerType);
                                }
                            });
                        }

                        if (!itineraryPoints.isEmpty()) {
                            updateNextClientInfo(); // Afficher directement le premier client
                            fetchRoutedItinerary();
                        }
                        Log.d("OSMDroid", "itineraryPoints " + itineraryPoints);

                    } catch (JSONException e) {
                        Log.e("OSMDroid", "Erreur parsing JSON: " + e.getMessage());
                    }
                },
                error -> Log.e("OSMDroid", "Erreur API: " + error.getMessage())
        );

        requestQueue.add(jsonObjectRequest);
    }

    // Fonction pour récupérer le type d'utilisateur avec le customerId
    private void fetchUserType(int customerId, CustomerTypeCallback callback) {
        Log.d("OSMDroid", "customerId " + customerId);
        if (customerId == 0){
            callback.onCustomerTypeReceived("domicile");
            return;
        }
        String usedUrl = URL_CUSTOMER + customerId;
        Log.d("OSMDroid", "usedUrl " + usedUrl);

        JsonObjectRequest userTypeRequest = new JsonObjectRequest(
                Request.Method.GET, usedUrl, null,
                userResponse -> {
                    try {
                        JSONObject response = userResponse.getJSONObject("response");
                        // Extraire le type d'utilisateur à partir de la réponse
                        Boolean userType = response.getBoolean("isProspect");
                        String userTypeStr;
                        if (userType == false){
                            userTypeStr = "client";
                        }else {
                            userTypeStr = "prospect";
                        }

                        // Appeler le callback avec le type d'utilisateur
                        callback.onCustomerTypeReceived(userTypeStr);

                    } catch (JSONException e) {
                        Log.e("OSMDroid", "Erreur parsing JSON pour le type d'utilisateur: " + e.getMessage());
                    }
                },
                error -> Log.e("OSMDroid", "Erreur API pour récupérer le type d'utilisateur: " + error.getMessage())
        );

        requestQueue.add(userTypeRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
            mapView.invalidate();
            centerMapOnUser();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null) {
            mapView.onDetach();
            mapView = null;
        }
    }
}
