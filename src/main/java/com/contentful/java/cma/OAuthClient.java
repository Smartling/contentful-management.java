package com.contentful.java.cma;

import com.contentful.java.cma.interceptor.ContentfulUserAgentHeaderInterceptor;
import com.contentful.java.cma.interceptor.ContentfulUserAgentHeaderInterceptor.Section;
import com.contentful.java.cma.interceptor.ContentfulUserAgentHeaderInterceptor.Section.OperatingSystem;
import com.contentful.java.cma.interceptor.ContentfulUserAgentHeaderInterceptor.Section.Version;
import com.contentful.java.cma.interceptor.LogInterceptor;
import com.contentful.java.cma.interceptor.OAuthErrorInterceptor;
import com.contentful.java.cma.interceptor.UserAgentHeaderInterceptor;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Properties;
import java.util.concurrent.Executor;

import static com.contentful.java.cma.Logger.Level.NONE;
import static com.contentful.java.cma.build.GeneratedBuildParameters.PROJECT_VERSION;
import static com.contentful.java.cma.interceptor.ContentfulUserAgentHeaderInterceptor.Section.Version.parse;
import static com.contentful.java.cma.interceptor.ContentfulUserAgentHeaderInterceptor.Section.os;
import static com.contentful.java.cma.interceptor.ContentfulUserAgentHeaderInterceptor.Section.platform;
import static com.contentful.java.cma.interceptor.ContentfulUserAgentHeaderInterceptor.Section.sdk;

/**
 * OAuth client for Contentful Management API.
 * <p>
 * This client is specifically designed for OAuth operations.
 * <p>
 * Example usage:
 * <pre>{@code
 * OAuthClient client = new OAuthClient.Builder()
 *     .setEndpoint("https://be.contentful.com/")
 *     .build();
 *
 * CMAOAuthTokenResponse response = client.oauth().exchangeAuthorizationCode(
 *     "client-id",
 *     "client-secret",
 *     "authorization-code",
 *     "redirect-uri"
 * );
 * }</pre>
 */
public class OAuthClient {
    private final ModuleOAuth moduleOAuth;
    private Executor callbackExecutor;

    private OAuthClient(Builder builder) {
        setCallbackExecutor(builder);

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(builder.endpoint)
            .callFactory(
                builder.callFactory == null
                    ? builder.defaultCallFactoryBuilder().build()
                    : builder.callFactory
            )
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

        this.moduleOAuth = new ModuleOAuth(retrofit, callbackExecutor);
    }

    private void setCallbackExecutor(Builder builder) {
        if (builder.callbackExecutor == null) {
            callbackExecutor = Platform.get().callbackExecutor();
        } else {
            callbackExecutor = builder.callbackExecutor;
        }
    }

    /**
     * @return the OAuth module for token operations
     */
    public ModuleOAuth oauth() {
        return moduleOAuth;
    }

    /**
     * Builder for creating an OAuthClient instance.
     */
    public static class Builder {
        private String endpoint = "https://be.contentful.com/";
        private Executor callbackExecutor;
        private Call.Factory callFactory;
        private Section application;
        private Section integration;
        private Logger logger;
        private Logger.Level logLevel = NONE;

        /**
         * Sets the OAuth endpoint URL.
         *
         * @param endpoint the OAuth endpoint URL
         * @return this Builder instance
         */
        public Builder setEndpoint(String endpoint) {
            if (endpoint == null) {
                throw new IllegalArgumentException("Cannot call setEndpoint() with null.");
            }
            this.endpoint = endpoint;
            return this;
        }

        /**
         * Sets the callback executor for async operations.
         * <p>
         * This is optional. If not set, a platform-specific default executor will be used
         * (e.g., main thread on Android, or a background thread on JVM).
         * <p>
         * Used for async methods like:
         * <pre>{@code
         * client.oauth().async().exchangeAuthorizationCode(..., callback);
         * }</pre>
         *
         * @param executor the executor to run callbacks on
         * @return this Builder instance
         */
        public Builder setCallbackExecutor(Executor executor) {
            this.callbackExecutor = executor;
            return this;
        }

        /**
         * Sets a custom HTTP call factory.
         * <p>
         * This is optional. If not set, a default call factory with ErrorInterceptor will be used.
         * <p>
         * Use this to customize the HTTP client behavior (e.g., add custom interceptors,
         * configure timeouts, etc.).
         *
         * @param callFactory the custom call factory
         * @return this Builder instance
         */
        public Builder setCallFactory(Call.Factory callFactory) {
            if (callFactory == null) {
                throw new IllegalArgumentException("Cannot call setCallFactory() with null.");
            }
            this.callFactory = callFactory;
            return this;
        }

        /**
         * Which application is using this client.
         *
         * @param name    name of the application.
         * @param version the version of the application.
         * @return this builder for chaining.
         */
        public Builder setApplication(String name, String version) {
            this.application = Section.app(name, parse(version));
            return this;
        }

        /**
         * Which integration is used.
         *
         * @param name    name of the integration.
         * @param version the version of the integration.
         * @return this builder for chaining.
         */
        public Builder setIntegration(String name, String version) {
            this.integration = Section.integration(name, parse(version));
            return this;
        }

        /**
         * Sets the logger to be used by this client.
         *
         * @param logger the logger instance
         * @return this {@code Builder} instance
         */
        public Builder setLogger(Logger logger) {
            if (logger == null) {
                throw new IllegalArgumentException("Do not set a null logger");
            }

            this.logger = logger;
            return this;
        }

        /**
         * Sets the log level for this client.
         *
         * @param logLevel {@link Logger.Level} value
         * @return this {@code Builder} instance
         */
        public Builder setLogLevel(Logger.Level logLevel) {
            if (logLevel == null) {
                throw new IllegalArgumentException("Cannot call setLogLevel() with null.");
            }
            this.logLevel = logLevel;
            return this;
        }

        /**
         * @return default call factory builder, used by the SDK.
         */
        public OkHttpClient.Builder defaultCallFactoryBuilder() {
            final OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
                .addInterceptor(new UserAgentHeaderInterceptor(getUserAgent()))
                .addInterceptor(new ContentfulUserAgentHeaderInterceptor(
                    createCustomHeaderSections(application, integration))
                )
                .addInterceptor(new OAuthErrorInterceptor());

            return setLogger(okBuilder);
        }

        private OkHttpClient.Builder setLogger(OkHttpClient.Builder okBuilder) {
            if (logger != null) {
                switch (logLevel) {
                    case NONE:
                    default:
                        break;
                    case BASIC:
                        return okBuilder.addInterceptor(new LogInterceptor(logger));
                    case FULL:
                        return okBuilder.addNetworkInterceptor(new LogInterceptor(logger));
                }
            } else {
                if (logLevel != NONE) {
                    throw new IllegalArgumentException(
                        "Cannot log to a null logger. Please set either no logLevel or "
                            + "set a custom Logger");
                }
            }
            return okBuilder;
        }

        private String getUserAgent() {
            return String.format(
                "contentful-management.java/%s",
                PROJECT_VERSION);
        }

        Section[] createCustomHeaderSections(Section application, Section integration) {
            final Properties properties = System.getProperties();

            return new Section[]{
                sdk(
                    "contentful-management.java",
                    parse(PROJECT_VERSION)
                ),
                platform(
                    "java",
                    parse(properties.getProperty("java.runtime.version"))
                ),
                os(
                    OperatingSystem.parse(properties.getProperty("os.name")),
                    Version.parse(properties.getProperty("os.version"))
                ),
                application,
                integration
            };
        }

        /**
         * Builds the OAuthClient instance.
         *
         * @return a new OAuthClient instance
         */
        public OAuthClient build() {
            return new OAuthClient(this);
        }
    }
}
