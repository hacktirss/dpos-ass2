package com.detisa.omicrom.codi;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.softcoatl.utils.logging.LogManager;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.io.IOUtils;

public class HttpsRestfulClient {

    static {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    @Override
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    @Override
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
            };
            
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            LogManager.fatal(ex);
        }
    }

    private final URL url;
    public HttpsRestfulClient(String endpoint) throws MalformedURLException {
        this.url = new URL(endpoint);
    }
    
    public JsonObject consume(JsonElement params) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        HttpsURLConnection client = (HttpsURLConnection) this.url.openConnection();
        client.setRequestMethod("POST");
        client.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        client.setRequestProperty("Accept", "application/json");
        client.setDoOutput(true);
        client.setDoInput(true);

        try (OutputStreamWriter writer = new OutputStreamWriter(client.getOutputStream())) {
            writer.write(new Gson().toJson(params));
        }
        try (InputStreamReader reader = new InputStreamReader(client.getInputStream())) {
            String response = IOUtils.toString(reader);
            return new JsonParser().parse(response).getAsJsonObject();
        } catch (IOException ex) {
            LogManager.error(ex);
            try (InputStreamReader error = new InputStreamReader(client.getErrorStream())) {
                return new JsonParser().parse(error).getAsJsonObject();
            }
        }
    }
    
    public static void main(String[] args) throws MalformedURLException  {
        HttpsRestfulClient client = new HttpsRestfulClient("https://detisatest.dyndns.org:8444/cuentaCodi");
        JsonObject parameters = new JsonObject();
        parameters.addProperty("idfae", 0);
        parameters.addProperty("telefono", "5951208522");
        parameters.addProperty("folio", 123878);
        parameters.addProperty("monto", 1.00);
        parameters.addProperty("descripcion", "pago de premium");
        
        try {
            JsonObject result = client.consume(parameters);
            System.out.println(result);
            if (!result.has("error")) {
                System.out.println(result.getAsJsonObject("detalleResultado").get("id"));
            } else {
                System.out.println(result.get("error"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
