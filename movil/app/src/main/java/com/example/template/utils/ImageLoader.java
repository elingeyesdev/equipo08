package com.example.template.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.template.network.ApiClient;

public class ImageLoader {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static void loadImage(String url, ImageView imageView) {
        if (url == null || url.trim().isEmpty()) {
            imageView.setImageDrawable(null);
            return;
        }
        executor.execute(() -> {
            try {
                String processedUrl = preprocessUrl(url);
                URL urlObj = new URL(processedUrl);
                HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                
                // Cabecera necesaria para omitir la página de advertencia de ngrok
                conn.setRequestProperty("ngrok-skip-browser-warning", "true");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                
                InputStream in = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                handler.post(() -> {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        imageView.setImageDrawable(null);
                    }
                });
            } catch (Exception e) {
                handler.post(() -> imageView.setImageDrawable(null));
            }
        });
    }

    public static void loadCircularImage(String url, ImageView imageView) {
        if (url == null || url.trim().isEmpty()) {
            imageView.setImageDrawable(null);
            return;
        }
        executor.execute(() -> {
            try {
                String processedUrl = preprocessUrl(url);
                URL urlObj = new URL(processedUrl);
                HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                
                conn.setRequestProperty("ngrok-skip-browser-warning", "true");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                
                InputStream in = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                handler.post(() -> {
                    if (bitmap != null) {
                        Bitmap circularBitmap = getCircularBitmap(bitmap);
                        imageView.setImageBitmap(circularBitmap);
                    } else {
                        imageView.setImageDrawable(null);
                    }
                });
            } catch (Exception e) {
                handler.post(() -> imageView.setImageDrawable(null));
            }
        });
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(output);

        final int color = 0xff424242;
        final android.graphics.Paint paint = new android.graphics.Paint();
        final android.graphics.Rect rect = new android.graphics.Rect(0, 0, size, size);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        
        float r = size / 2.0f;
        canvas.drawCircle(r, r, r, paint);

        paint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        
        int left = (bitmap.getWidth() - size) / 2;
        int top = (bitmap.getHeight() - size) / 2;
        android.graphics.Rect srcRect = new android.graphics.Rect(left, top, left + size, top + size);
        
        canvas.drawBitmap(bitmap, srcRect, rect, paint);
        return output;
    }

    private static String preprocessUrl(String url) {
        if (url == null) return null;
        
        // Si la URL es relativa y empieza con "/", le añadimos el host base del backend
        if (url.startsWith("/")) {
            String apiBase = ApiClient.getBaseUrl();
            if (apiBase.endsWith("/api/")) {
                apiBase = apiBase.substring(0, apiBase.length() - 5);
            } else if (apiBase.endsWith("/api")) {
                apiBase = apiBase.substring(0, apiBase.length() - 4);
            }
            return apiBase + url;
        }
        
        // Si la URL apunta a localhost o entornos locales en la DB, la redirigimos al tunnel de ngrok actual
        if (url.contains("localhost:3000") || url.contains("127.0.0.1:3000") || url.contains("10.0.2.2:3000")) {
            String path = "";
            int portIndex = url.indexOf(":3000");
            if (portIndex != -1) {
                path = url.substring(portIndex + 5); // Obtiene la ruta después del puerto 3000 (ej: /uploads/...)
            }
            
            String apiBase = ApiClient.getBaseUrl();
            if (apiBase.endsWith("/api/")) {
                apiBase = apiBase.substring(0, apiBase.length() - 5);
            } else if (apiBase.endsWith("/api")) {
                apiBase = apiBase.substring(0, apiBase.length() - 4);
            }
            
            return apiBase + path;
        }
        return url;
    }
}
