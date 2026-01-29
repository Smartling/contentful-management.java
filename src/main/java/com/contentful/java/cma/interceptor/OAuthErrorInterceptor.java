package com.contentful.java.cma.interceptor;

import com.contentful.java.cma.model.OAuthException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor for OAuth operations that throws OAuthException on error responses.
 */
public class OAuthErrorInterceptor implements Interceptor {

    /**
     * Intercepts chain to check for unsuccessful OAuth requests.
     *
     * @param chain provided by the framework to check
     * @return the response if no error occurred
     * @throws IOException will get thrown if response code is unsuccessful
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();
        final Response response = chain.proceed(request);

        if (!response.isSuccessful()) {
            throw new OAuthException(request, response);
        }

        return response;
    }
}
