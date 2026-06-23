package com.example.template.network;

import android.content.Context;
import java.security.cert.CertificateException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Configura aquí la URL de tu API
    private static final String BASE_URL = "http://10.0.2.2:3000/api/"; // Desarrollo local (Emulador Android)
    // private static final String BASE_URL = "https://bolclick-backend.onrender.com/api/"; // Producción Render
    // private static final String BASE_URL = "https://runner-affair-gratitude.ngrok-free.dev/api/"; // Desarrollo local (ngrok)


    // Activa esto (true) en desarrollo para omitir la validación de certificados SSL de ngrok
    private static final boolean OMITIR_CERTIFICADOS_SSL = true;

    public static String getBaseUrl() {
        return BASE_URL;
    }

    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder clientBuilder;
            
            if (OMITIR_CERTIFICADOS_SSL && BASE_URL.startsWith("https")) {
                clientBuilder = getUnsafeOkHttpClientBuilder();
            } else {
                clientBuilder = new OkHttpClient.Builder();
            }

            OkHttpClient client = clientBuilder
                    .addInterceptor(logging)
                    .addInterceptor(new TenantInterceptor(context))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient.Builder getUnsafeOkHttpClientBuilder() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
            };

            // Usamos "TLS" para asegurar compatibilidad con protocolos modernos requeridos por ngrok
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
