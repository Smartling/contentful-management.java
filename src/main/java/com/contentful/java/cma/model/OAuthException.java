package com.contentful.java.cma.model;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

import static java.lang.String.format;

/**
 * Exception thrown when OAuth token operations fail.
 * <p>
 * OAuth error responses follow the OAuth 2.0 specification format:
 * <pre>{@code
 * {
 *   "error": "invalid_grant",
 *   "errorMessage": "The provided authorization grant is invalid...",
 *   "requestId": "1b991683-6531-4a22-a765-1a9c6781c1ee"
 * }
 * }</pre>
 */
public class OAuthException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * OAuth error body structure.
     */
    public static class ErrorBody {
        String error;
        String errorMessage;
        String requestId;

        /**
         * @return the OAuth error code (e.g., "invalid_grant", "invalid_client")
         */
        public String getError() {
            return error;
        }

        /**
         * @return the human-readable error message
         */
        public String getErrorMessage() {
            return errorMessage;
        }

        /**
         * @return the request ID for tracking purposes
         */
        public String getRequestId() {
            return requestId;
        }

        @Override
        public String toString() {
            return "ErrorBody { "
                + (getError() != null ? "error = " + getError() + ", " : "")
                + (getErrorMessage() != null ? "errorMessage = " + getErrorMessage() + ", " : "")
                + (getRequestId() != null ? "requestId = " + getRequestId() + " " : "")
                + "}";
        }
    }

    private final Request request;
    private final Response response;
    private ErrorBody errorBody;

    /**
     * Construct an OAuth error exception.
     *
     * @param request  the request that caused the error
     * @param response the error response from the OAuth server
     */
    public OAuthException(Request request, Response response) {
        this.request = request;
        this.response = response;

        try {
            final String body = response.body() != null ? response.body().string() : null;
            this.errorBody = new GsonBuilder().create().fromJson(body, ErrorBody.class);
        } catch (IOException e) {
            this.errorBody = null;
        }
    }

    /**
     * @return the HTTP response code
     */
    public int responseCode() {
        return response.code();
    }

    /**
     * @return the HTTP response message
     */
    public String responseMessage() {
        return response.message();
    }

    /**
     * @return the parsed error body, or null if parsing failed
     */
    public ErrorBody getErrorBody() {
        return errorBody;
    }

    @Override
    public String toString() {
        if (errorBody == null) {
            return format(
                Locale.getDefault(),
                "OAuth FAILED \n\t%s\n\t↳ Header{%s}%s\n\t%s\n\t↳ Header{%s}",
                request.toString(),
                headersToString(request.headers()),
                maybeBodyToString(request.body()),
                response.toString(),
                headersToString(response.headers()));
        } else {
            return format(
                Locale.getDefault(),
                "OAuth FAILED %s\n\t%s\n\t↳ Header{%s}%s\n\t%s\n\t↳ Header{%s}",
                errorBody.toString(),
                request.toString(),
                headersToString(request.headers()),
                maybeBodyToString(request.body()),
                response.toString(),
                headersToString(response.headers()));
        }
    }

    private String maybeBodyToString(RequestBody body) {
        if (body != null) {
            final Buffer sink = new Buffer();
            try {
                body.writeTo(sink);
                final String bodyContent = sink.readString(Charset.defaultCharset());
                return "\n\t↳ Body " + bodyContent;
            } catch (IOException e) {
                return "";
            }
        } else {
            return "";
        }
    }

    private String headersToString(Headers headers) {
        final StringBuilder builder = new StringBuilder();

        String divider = "";
        for (final String name : headers.names()) {
            final String value = headers.get(name);
            builder.append(divider);
            builder.append(name);
            builder.append(": ");
            builder.append(value);

            if (divider.isEmpty()) {
                divider = ", ";
            }
        }

        return builder.toString();
    }
}
