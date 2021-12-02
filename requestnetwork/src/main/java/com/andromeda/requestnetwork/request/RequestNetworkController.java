package com.andromeda.requestnetwork.request;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestNetworkController {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    private static final int SOCKET_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 25000;

    protected OkHttpClient client;

    private static RequestNetworkController mInstance;

    public static synchronized RequestNetworkController getInstance() {
        if (mInstance == null) {
            mInstance = new RequestNetworkController();
        }
        return mInstance;
    }

    @SuppressLint("CustomX509TrustManager")
    private OkHttpClient getClient() {
        if (client == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            try {
                final TrustManager[] trustAllCerts = new TrustManager[]{

                        new X509TrustManager() {
                            @SuppressLint("TrustAllX509TrustManager")
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            }

                            @SuppressLint("TrustAllX509TrustManager")
                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                final SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
                builder.connectTimeout(SOCKET_TIMEOUT, TimeUnit.MILLISECONDS);
                builder.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
                builder.writeTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
                builder.hostnameVerifier((hostname, session) -> true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            client = builder.build();
        }

        return client;
    }
    
    public Response executeAsync(RequestNetwork requestNetwork, String method, String url) {
        try {
            return init(requestNetwork, method, url).execute();
        } catch (Exception e) {
            return null;
        }
    }


    public void execute(RequestNetwork requestNetwork, String method, String url, RequestNetwork.RequestListener requestListener) {
        try {

            init(requestNetwork, method, url).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                    if (requestNetwork.getActivity() == null) {
                        requestListener.onErrorResponse(e.getMessage());
                    } else {
                        requestNetwork.getActivity().runOnUiThread(() -> requestListener.onErrorResponse(e.getMessage()));
                    }

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                    assert response.body() != null;
                    final String responseBody = response.body().string().trim();
                    if (requestNetwork.getActivity() == null) {
                        requestListener.onResponse(responseBody, response);
                    } else {
                        requestNetwork.getActivity().runOnUiThread(() -> {
                            requestListener.onResponse(responseBody, response);
                        });
                    }
                }
            });
        } catch (Exception e) {
            requestListener.onErrorResponse(e.getMessage());
        }
    }

    private Call init(RequestNetwork requestNetwork, String method, String url) {
        Request.Builder reqBuilder = new Request.Builder();
        Headers.Builder headerBuilder = new Headers.Builder();

        if (requestNetwork.getHeaders().size() > 0) {
            HashMap<String, Object> headers = requestNetwork.getHeaders();

            for (HashMap.Entry<String, Object> header : headers.entrySet()) {
                headerBuilder.add(header.getKey(), String.valueOf(header.getValue()));
            }
        }

        if (requestNetwork.getParamsJson() != null) {
            RequestBody reqBody = RequestBody.create(okhttp3.MediaType.parse("application/json"), requestNetwork.getParamsJson());

            if (method.equals(GET)) {
                reqBuilder.url(url).headers(headerBuilder.build()).get();
            } else {
                reqBuilder.url(url).headers(headerBuilder.build()).method(method, reqBody);
            }
        } else {
            if (method.equals(GET)) {
                HttpUrl.Builder httpBuilder;

                try {
                    httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
                } catch (NullPointerException ne) {
                    throw new NullPointerException("unexpected url: " + url);
                }

                if (requestNetwork.getParams().size() > 0) {
                    HashMap<String, Object> params = requestNetwork.getParams();

                    for (HashMap.Entry<String, Object> param : params.entrySet()) {
                        httpBuilder.addQueryParameter(param.getKey(), String.valueOf(param.getValue()));
                    }
                }

                reqBuilder.url(httpBuilder.build()).headers(headerBuilder.build()).get();
            } else {
                FormBody.Builder formBuilder = new FormBody.Builder();
                if (requestNetwork.getParams().size() > 0) {
                    HashMap<String, Object> params = requestNetwork.getParams();

                    for (HashMap.Entry<String, Object> param : params.entrySet()) {
                        formBuilder.add(param.getKey(), String.valueOf(param.getValue()));
                    }
                }

                RequestBody reqBody = formBuilder.build();

                reqBuilder.url(url).headers(headerBuilder.build()).method(method, reqBody);
            }
        }

        Request req = reqBuilder.build();
        return getClient().newCall(req);
    }
}
