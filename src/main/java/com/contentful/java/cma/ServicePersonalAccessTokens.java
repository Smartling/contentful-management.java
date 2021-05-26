/*
 * Copyright (C) 2019 Contentful GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.contentful.java.cma;

import com.contentful.java.cma.model.CMAArray;
import com.contentful.java.cma.model.CMAPersonalAccessToken;

import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Personal Access Token Service.
 */
interface ServicePersonalAccessTokens {
  @GET("/users/me/access_tokens")
  Flowable<CMAArray<CMAPersonalAccessToken>> fetchAll();

  @GET("/users/me/access_tokens")
  Flowable<CMAArray<CMAPersonalAccessToken>> fetchAll(@QueryMap Map<String, String> query);

  @GET("/users/me/access_tokens/{tokenId}")
  Flowable<CMAPersonalAccessToken> fetchOne(@Path("tokenId") String tokenId);

  @POST("/users/me/access_tokens")
  Flowable<CMAPersonalAccessToken> create(@Body CMAPersonalAccessToken token);

  @PUT("/users/me/access_tokens/{tokenId}/revoked")
  Flowable<CMAPersonalAccessToken> revoke(@Path("tokenId") String tokenId);
}
