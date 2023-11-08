package com.contentful.java.cma.interceptor;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

import java.io.IOException;
import java.util.function.Predicate;

/**
 * Interceptor to add content type header to requests
 */
public class ContentTypeInterceptor implements Interceptor {
  public static final String HEADER_NAME = "Content-Type";

  private final String contentType;
  private final MediaType mediaType;
  private final Predicate<Request> apply;

  /**
   * Create Header interceptor, saving parameters.
   *
   * @param contentType type header to be send with all of the requests
   */
  public ContentTypeInterceptor(String contentType, Predicate<Request> apply) {
    this.contentType = contentType;
    this.mediaType = MediaType.parse(contentType);
    this.apply = apply;
  }

  /**
   * Method called by framework, to enrich current request with the header information requested.
   *
   * @param chain the execution chain for the request.
   * @return the response received
   * @throws IOException if chain had an error
   */
  @Override public Response intercept(Chain chain) throws IOException {
    final Request request = chain.request();

    final Request.Builder builder = request.newBuilder();
    if (apply.test(request)) {
      builder.addHeader(HEADER_NAME, contentType);

      if (request.body() != null) {
        rewriteBodyWithCustomContentType(request, builder);
      }
    }

    final Request contentTypeRequest = builder.build();
    return chain.proceed(contentTypeRequest);
  }

  private void rewriteBodyWithCustomContentType(Request request, Request.Builder builder)
      throws IOException {
    final Buffer sink = new Buffer();
    request.body().writeTo(sink);

    final byte[] content = sink.readByteArray();
    final RequestBody body = RequestBody.create(mediaType, content);

    if ("POST".equals(request.method())) {
      builder.post(body);
    } else if ("PUT".equals(request.method())) {
      builder.put(body);
    } else if ("PATCH".equals(request.method())) {
      builder.patch(body);
    }
  }
}
