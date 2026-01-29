package com.contentful.java.cma;

import com.contentful.java.cma.model.CMAOAuthTokenResponse;

import io.reactivex.Flowable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * OAuth Service for token exchange and refresh operations.
 */
public interface ServiceOAuth {
    @FormUrlEncoded
    @POST("oauth/token")
    Flowable<CMAOAuthTokenResponse> exchangeAuthorizationCode(
            @Field("grant_type") String grantType,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("code") String authorizationCode,
            @Field("redirect_uri") String redirectUri);

    @FormUrlEncoded
    @POST("oauth/token")
    Flowable<CMAOAuthTokenResponse> refreshAccessToken(
            @Field("grant_type") String grantType,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("refresh_token") String refreshToken,
            @Field("redirect_uri") String redirectUri);
}
