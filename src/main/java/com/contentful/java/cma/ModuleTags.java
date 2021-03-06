package com.contentful.java.cma;

import com.contentful.java.cma.RxExtensions.DefFunc;
import com.contentful.java.cma.model.CMAArray;
import com.contentful.java.cma.model.CMATag;
import retrofit2.Retrofit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * This module contains the tag options available to this SDK.
 */
public class ModuleTags extends AbsModule<ServiceTags> {
  private final Async async;

  /**
   * Create a new tag module.
   *
   * @param retrofit         the retrofit instance to be used to create the service.
   * @param callbackExecutor to tell on which thread it should run.
   * @param spaceId          the space to be used when not given.
   * @param environmentId    the environment to be used when not given.
   * @param environmentIdConfigured internal helper to see if environment was set.
   */
  public ModuleTags(
      Retrofit retrofit,
      Executor callbackExecutor,
      String spaceId,
      String environmentId,
      boolean environmentIdConfigured) {
    super(retrofit, callbackExecutor, spaceId, environmentId, environmentIdConfigured);
    async = new Async();
  }

  /**
   * Initialize retrofit with the current service.
   *
   * @param retrofit the instance to use the service with.
   * @return a populated instance of the service to be used.
   */
  @Override protected ServiceTags createService(Retrofit retrofit) {
    return retrofit.create(ServiceTags.class);
  }

  /**
   * Use all functionalities of this module asynchronously.
   *
   * @return an asynchronous handling instance.
   */
  public Async async() {
    return async;
  }

  /**
   * Fetch all tags of the configured space and environment.
   *
   * @return the array of tags.
   * @throws IllegalArgumentException if configured space id is null.
   * @throws IllegalArgumentException if configured environment id is null.
   * @see CMAClient.Builder#setSpaceId(String)
   * @see CMAClient.Builder#setEnvironmentId(String)
   */
  public CMAArray<CMATag> fetchAll() {
    return fetchAll(spaceId, environmentId);
  }

  /**
   * Fetch all tags matching a query using configured space and environment.
   * <p>
   * This fetch uses the default parameter defined in {@link DefaultQueryParameter#FETCH}
   *
   * @param map the query to narrow down the results.
   * @return matching tags.
   * @throws IllegalArgumentException if configured space id is null.
   * @throws IllegalArgumentException if configured environment id is null.
   * @see CMAClient.Builder#setSpaceId(String)
   * @see CMAClient.Builder#setEnvironmentId(String)
   */
  public CMAArray<CMATag> fetchAll(Map<String, String> map) {
    return fetchAll(spaceId, environmentId, map);
  }


  /**
   * Fetch all tags of the given space and environment.
   * <p>
   * This method will override the configuration specified through
   * {@link CMAClient.Builder#setSpaceId(String)} and
   * {@link CMAClient.Builder#setEnvironmentId(String)}.
   *
   * @param spaceId       the space identifier, identifying the space.
   * @param environmentId the environment identifier, identifying the environment.
   * @return the array of tags.
   * @throws IllegalArgumentException if spaceId is null.
   * @throws IllegalArgumentException if environmentId is null.
   */
  public CMAArray<CMATag> fetchAll(String spaceId, String environmentId) {
    assertNotNull(spaceId, "spaceId");
    assertNotNull(environmentId, "environmentId");

    return service.fetchAll(spaceId, environmentId, new HashMap<>()).blockingFirst();
  }

  /**
   * Fetch all tags matching the given query from the given space and environment.
   *
   * @param spaceId       the space identifier, identifying the space.
   * @param environmentId the environment identifier, identifying the environment.
   * @param query         specifying details about which tags to fetch.
   * @return {@link CMAArray} result instance
   * @throws IllegalArgumentException if spaceId is null.
   * @throws IllegalArgumentException if environmentId is null.
   */
  public CMAArray<CMATag> fetchAll(
          String spaceId,
          String environmentId,
          Map<String, String> query) {
    assertNotNull(spaceId, "spaceId");
    assertNotNull(environmentId, "environmentId");
    DefaultQueryParameter.putIfNotSet(query, DefaultQueryParameter.FETCH);

    return service.fetchAll(spaceId, environmentId, query).blockingFirst();
  }

  /**
   * Handler for asynchronous requests.
   */
  public class Async {
    /**
     * Fetch all tags from the configured space and environment, asynchronously.
     *
     * @param callback a callback to be called, once the results are present.
     * @return a callback for the array fetched.
     * @throws IllegalArgumentException if configured space id is null.
     * @throws IllegalArgumentException if configured environment id is null.
     * @see CMAClient.Builder#setSpaceId(String)
     * @see CMAClient.Builder#setEnvironmentId(String)
     * @see ModuleTags#fetchAll()
     */
    public CMACallback<CMAArray<CMATag>> fetchAll(
        final CMACallback<CMAArray<CMATag>> callback) {
      return defer(new DefFunc<CMAArray<CMATag>>() {
        @Override
        CMAArray<CMATag> method() {
          return ModuleTags.this.fetchAll();
        }
      }, callback);
    }

    /**
     * Fetch all tags from the configured space and environment, asynchronously with query.
     *
     * @param query    the query to narrow down the results.
     * @param callback a callback for the array fetched.
     * @return the given CMACallback instance
     * @throws IllegalArgumentException if configured space id is null.
     * @throws IllegalArgumentException if configured environment id is null.
     * @see CMAClient.Builder#setSpaceId(String)
     * @see CMAClient.Builder#setEnvironmentId(String)
     */
    public CMACallback<CMAArray<CMATag>> fetchAll(
            final Map<String, String> query,
            final CMACallback<CMAArray<CMATag>> callback) {
      return defer(new DefFunc<CMAArray<CMATag>>() {
        @Override CMAArray<CMATag> method() {
          return ModuleTags.this.fetchAll(query);
        }
      }, callback);
    }

    /**
     * Fetch all tags of the given space and environment, asynchronously.
     * <p>
     * This method will override the configuration specified through
     * {@link CMAClient.Builder#setSpaceId(String)} and
     * {@link CMAClient.Builder#setEnvironmentId(String)}.
     *
     * @param spaceId       the space identifier identifying the space.
     * @param environmentId the environment identifier identifying the space.
     * @param callback      a callback to be called, once the results are present.
     * @return a callback for the array fetched.
     * @throws IllegalArgumentException if space id is null.
     * @throws IllegalArgumentException if environment id is null.
     * @see ModuleTags#fetchAll(String, String)
     */
    public CMACallback<CMAArray<CMATag>> fetchAll(
        final String spaceId,
        final String environmentId,
        final CMACallback<CMAArray<CMATag>> callback) {
      return defer(new DefFunc<CMAArray<CMATag>>() {
        @Override CMAArray<CMATag> method() {
          return ModuleTags.this.fetchAll(spaceId, environmentId);
        }
      }, callback);
    }

    /**
     * Fetch all tags of the given space and environment, asynchronously with a query.
     * <p>
     * This method will override the configuration specified through
     * {@link CMAClient.Builder#setSpaceId(String)} and
     * {@link CMAClient.Builder#setEnvironmentId(String)}.
     *
     * @param spaceId       the space identifier identifying the space.
     * @param environmentId the environment identifier identifying the space.
     * @param query         the query to narrow down the results.
     * @param callback      a callback to be called, once the results are present.
     * @return the given CMACallback instance
     * @return a callback for the array fetched.
     * @throws IllegalArgumentException if space id is null.
     * @throws IllegalArgumentException if environment id is null.
     */
    public CMACallback<CMAArray<CMATag>> fetchAll(
            final String spaceId,
            final String environmentId,
            final Map<String, String> query,
            final CMACallback<CMAArray<CMATag>> callback) {
      return defer(new DefFunc<CMAArray<CMATag>>() {
        @Override CMAArray<CMATag> method() {
          return ModuleTags.this.fetchAll(spaceId, environmentId, query);
        }
      }, callback);
    }
  }
}
