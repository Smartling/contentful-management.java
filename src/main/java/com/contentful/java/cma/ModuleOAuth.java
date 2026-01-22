package com.contentful.java.cma;

import com.contentful.java.cma.model.CMAOAuthTokenResponse;

import java.util.concurrent.Executor;

import retrofit2.Retrofit;

/**
 * OAuth Module for token exchange and refresh operations.
 */
public class ModuleOAuth extends AbsModule<ServiceOAuth> {
    final Async async;

    /**
     * Create the OAuth module.
     *
     * @param retrofit         the retrofit instance to be used to create the service.
     * @param callbackExecutor to tell on which thread it should run.
     */
    public ModuleOAuth(Retrofit retrofit, Executor callbackExecutor) {
        super(retrofit, callbackExecutor, null, null, false);
        this.async = new Async();
    }

    @Override
    protected ServiceOAuth createService(Retrofit retrofit) {
        return retrofit.create(ServiceOAuth.class);
    }

    /**
     * Exchange authorization code for access and refresh tokens.
     *
     * @param clientId          the OAuth client ID.
     * @param clientSecret      the OAuth client secret.
     * @param authorizationCode the authorization code received from the OAuth flow.
     * @param redirectUri       the redirect URI used in the OAuth flow.
     * @return {@link CMAOAuthTokenResponse} result instance containing access and
     *         refresh tokens.
     * @throws IllegalArgumentException if any parameter is null.
     */
    public CMAOAuthTokenResponse exchangeAuthorizationCode(String clientId,
                                                            String clientSecret,
                                                            String authorizationCode,
                                                            String redirectUri) {
        assertNotNull(clientId, "clientId");
        assertNotNull(clientSecret, "clientSecret");
        assertNotNull(authorizationCode, "authorizationCode");
        assertNotNull(redirectUri, "redirectUri");

        return service.exchangeAuthorizationCode("authorization_code", clientId,
                clientSecret, authorizationCode, redirectUri).blockingFirst();
    }

    /**
     * Refresh access token using refresh token.
     *
     * @param clientId     the OAuth client ID.
     * @param clientSecret the OAuth client secret.
     * @param refreshToken the refresh token.
     * @param redirectUri  the redirect URI used in the OAuth flow.
     * @return {@link CMAOAuthTokenResponse} result instance containing new access and
     *         refresh tokens.
     * @throws IllegalArgumentException if any parameter is null.
     */
    public CMAOAuthTokenResponse refreshAccessToken(String clientId, String clientSecret,
                                                     String refreshToken, String redirectUri) {
        assertNotNull(clientId, "clientId");
        assertNotNull(clientSecret, "clientSecret");
        assertNotNull(refreshToken, "refreshToken");
        assertNotNull(redirectUri, "redirectUri");

        return service.refreshAccessToken("refresh_token", clientId, clientSecret,
                refreshToken, redirectUri).blockingFirst();
    }

    /**
     * Async methods for OAuth operations.
     */
    public class Async {
        /**
         * Exchange authorization code for access and refresh tokens asynchronously.
         *
         * @param clientId          the OAuth client ID.
         * @param clientSecret      the OAuth client secret.
         * @param authorizationCode the authorization code received from the OAuth flow.
         * @param redirectUri       the redirect URI used in the OAuth flow.
         * @param callback          callback to be called on success or error.
         * @return the callback passed in.
         */
        public CMACallback<CMAOAuthTokenResponse> exchangeAuthorizationCode(
                String clientId, String clientSecret, String authorizationCode,
                String redirectUri, CMACallback<CMAOAuthTokenResponse> callback) {
            return defer(new RxExtensions.DefFunc<CMAOAuthTokenResponse>() {
                @Override
                CMAOAuthTokenResponse method() {
                    return ModuleOAuth.this.exchangeAuthorizationCode(clientId,
                            clientSecret, authorizationCode, redirectUri);
                }
            }, callback);
        }

        /**
         * Refresh access token using refresh token asynchronously.
         *
         * @param clientId     the OAuth client ID.
         * @param clientSecret the OAuth client secret.
         * @param refreshToken the refresh token.
         * @param redirectUri  the redirect URI used in the OAuth flow.
         * @param callback     callback to be called on success or error.
         * @return the callback passed in.
         */
        public CMACallback<CMAOAuthTokenResponse> refreshAccessToken(String clientId,
                String clientSecret, String refreshToken, String redirectUri,
                CMACallback<CMAOAuthTokenResponse> callback) {
            return defer(new RxExtensions.DefFunc<CMAOAuthTokenResponse>() {
                @Override
                CMAOAuthTokenResponse method() {
                    return ModuleOAuth.this.refreshAccessToken(clientId, clientSecret,
                            refreshToken, redirectUri);
                }
            }, callback);
        }
    }
}
