/*
 * Copyright (c) 2018 Sanjiv Pansari
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.spanlab.log4j.pubsub.appender;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.annotations.VisibleForTesting;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;

@Plugin(name = "GoogleCloudCredentials", category = Node.CATEGORY, printObject = true)
public class GoogleCloudCredentials {
    private final File serviceAccountJsonFile;

    protected GoogleCloudCredentials(final File serviceAccountJsonFile) {
        this.serviceAccountJsonFile = serviceAccountJsonFile;
    }

    public Credential getCredential(final HttpTransport transport,
                                    final JacksonFactory jacksonFactory,
                                    final Collection<String> serviceAccountScopes)
            throws GeneralSecurityException, IOException {
        return buildNewGoogleCredentials(transport, jacksonFactory, serviceAccountScopes);
    }


    @VisibleForTesting
    GoogleCredential buildNewGoogleCredentials(final HttpTransport transport,
                                               final JacksonFactory jacksonFactory,
                                               final Collection<String> serviceAccountScopes)
            throws GeneralSecurityException, IOException {

        return GoogleCredential.fromStream(new FileInputStream(serviceAccountJsonFile)).createScoped(serviceAccountScopes);

    }


    @PluginFactory
    public static GoogleCloudCredentials createGoogleCloudCredentials(@PluginAttribute(value = "serviceAccountJsonFileName") final String serviceAccountJsonFileName) {
        return newBuilder()
                .withServiceAccountJsonFileName(serviceAccountJsonFileName)
                .build();
    }


    @VisibleForTesting
    static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder
            implements org.apache.logging.log4j.core.util.Builder<GoogleCloudCredentials> {
        private String serviceAccountJsonFileName;


        public Builder withServiceAccountJsonFileName(final String serviceAccountJsonFileName) {
            this.serviceAccountJsonFileName = serviceAccountJsonFileName;
            return this;
        }

        @Override
        public GoogleCloudCredentials build() {
            File serviceAccountJsonFile = null;

            serviceAccountJsonFile = getServiceAccountJsonFile();
            return new GoogleCloudCredentials(serviceAccountJsonFile);
        }

        @VisibleForTesting
        File getServiceAccountJsonFile() {
            return new File(serviceAccountJsonFileName);
        }

    }
}
