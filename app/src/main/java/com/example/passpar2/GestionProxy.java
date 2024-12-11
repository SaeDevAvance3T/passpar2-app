package com.example.passpar2;

import com.android.volley.toolbox.HurlStack;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * Gesionnaire du proxy
 * Une instance de cette classe doit être fournie en argument du constructeur
 * de la file d'attente des requêtes (de type RequestQueue)
 */
public class GestionProxy extends HurlStack {

    /**  Adresse du proxy de l'IUT */
    private static final String PROXY_ADDRESSE = "193.54.203.134";

    /** Numéro du port du proxy de l'IUT */
    private static final int PROXY_PORT = 8080;

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {

        // création d'un proxy avec les info. de l'IUT
        Proxy proxy = new Proxy(Proxy.Type.HTTP,
                InetSocketAddress.createUnresolved(PROXY_ADDRESSE, PROXY_PORT));
        return (HttpURLConnection) url.openConnection(proxy);
    }
}
