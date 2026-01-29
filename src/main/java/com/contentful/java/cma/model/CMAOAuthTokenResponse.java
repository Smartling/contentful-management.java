package com.contentful.java.cma.model;

import com.google.gson.annotations.SerializedName;

public class CMAOAuthTokenResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("expires_in")
    private Integer expiresIn;

    @SerializedName("scope")
    private String scope;

    @SerializedName("created_at")
    private Long createdAt;

    public String getAccessToken() {
        return accessToken;
    }

    public CMAOAuthTokenResponse setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public CMAOAuthTokenResponse setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public String getTokenType() {
        return tokenType;
    }

    public CMAOAuthTokenResponse setTokenType(String tokenType) {
        this.tokenType = tokenType;
        return this;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public CMAOAuthTokenResponse setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public String getScope() {
        return scope;
    }

    public CMAOAuthTokenResponse setScope(String scope) {
        this.scope = scope;
        return this;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public CMAOAuthTokenResponse setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
        return this;
    }
}
