package com.example.template.network;

import android.content.Context;
import com.example.template.utils.SessionManager;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TenantInterceptor implements Interceptor {
    private SessionManager sessionManager;

    public TenantInterceptor(Context context) {
        this.sessionManager = new SessionManager(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder();
        
        String tenantId = sessionManager.getTenantId();
        if (tenantId != null) {
            requestBuilder.addHeader("x-tenant-id", tenantId);
        }

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
