package org.spanlab.log4j.pubsub.appender;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpBackOffIOExceptionHandler;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.Sleeper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import java.io.IOException;

/**
 * RetryHttpInitializerWrapper will automatically retry upon RPC
 * failures, preserving the auto-refresh behavior of the Google
 * Credentials.
 */
public class RetryHttpInitializerWrapper implements HttpRequestInitializer {
    // Intercepts the request for filling in the "Authorization"
    // header field, as well as recovering from certain unsuccessful
    // error codes wherein the Credential must refresh its token for a
    // retry.
    private final Credential wrappedCredential;

    // A sleeper; you can replace it with a mock in your test.
    private final Sleeper sleeper;

    private final int maxElapsedTimeMillis;

    public RetryHttpInitializerWrapper(final Credential wrappedCredential,
                                       final int maxElapsedTimeMillis) {
        this(wrappedCredential, Sleeper.DEFAULT, maxElapsedTimeMillis);
    }

    @VisibleForTesting
    RetryHttpInitializerWrapper(final Credential wrappedCredential,
                                final Sleeper sleeper,
                                final int maxElapsedTimeMillis) {
        this.wrappedCredential = Preconditions.checkNotNull(wrappedCredential);
        this.sleeper = sleeper;
        this.maxElapsedTimeMillis = maxElapsedTimeMillis;
    }

    @Override
    public void initialize(final HttpRequest request) {
        final HttpUnsuccessfulResponseHandler backoffHandler =
                new HttpBackOffUnsuccessfulResponseHandler(
                        new ExponentialBackOff.Builder()
                                .setMaxElapsedTimeMillis(maxElapsedTimeMillis)
                                .build())
                        .setSleeper(sleeper);

        request.setInterceptor(wrappedCredential);
        request.setUnsuccessfulResponseHandler(
                new HttpUnsuccessfulResponseHandler() {
                    @Override
                    public boolean handleResponse(
                            HttpRequest request,
                            HttpResponse response,
                            boolean supportsRetry) throws IOException {
                        if (wrappedCredential.handleResponse(request,
                                response,
                                supportsRetry)) {
                            // If credential decides it can handle it,
                            // the return code or message indicated
                            // something specific to authentication,
                            // and no backoff is desired.
                            return true;
                        } else if (backoffHandler.handleResponse(request,
                                response,
                                supportsRetry)) {
                            // Otherwise, we defer to the judgement of
                            // our internal backoff handler.
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
        request.setIOExceptionHandler(
                new HttpBackOffIOExceptionHandler(
                        new ExponentialBackOff.Builder()
                                .setMaxElapsedTimeMillis(maxElapsedTimeMillis)
                                .build())
                        .setSleeper(sleeper));
    }
}
