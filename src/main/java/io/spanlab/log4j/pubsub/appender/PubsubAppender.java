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

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;

import static io.spanlab.log4j.pubsub.appender.PubsubManager.getManager;

@Plugin(name = "Pubsub", category = Node.CATEGORY, elementType = "appender", printObject = true)
public class PubsubAppender extends AbstractAppender {
  private static final long serialVersionUID = 1L;

  private final PubsubManager googleCloudPubsubManager;

  protected PubsubAppender(final String name,
                                      final Filter filter,
                                      final Layout<? extends Serializable> layout,
                                      final boolean ignoreExceptions,
                                      final PubsubManager googleCloudPubsubManager) {
    super(name, filter, layout, ignoreExceptions);
    this.googleCloudPubsubManager = googleCloudPubsubManager;
  }

  @Override
  public void append(LogEvent event) {
    googleCloudPubsubManager.write(event);
  }

  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder
      implements org.apache.logging.log4j.core.util.Builder<PubsubAppender> {
    @PluginElement("Layout")
    private Layout<? extends Serializable> layout = PatternLayout.createDefaultLayout();

    @PluginElement("Filter")
    private Filter filter;


    @PluginElement("GoogleCloudCredentials")
    private GoogleCloudCredentials googleCloudCredentials;

    @PluginBuilderAttribute
    @Required
    private String name;

    @PluginBuilderAttribute
    private boolean ignoreExceptions = true;

    @PluginBuilderAttribute
    private int maxRetryTimeMillis = 5000;

    @PluginBuilderAttribute
    private String projectId;

    @PluginBuilderAttribute
    @Required
    private String topic;

    @PluginBuilderAttribute
    private boolean autoCreateTopic = false;

    @Override
    public PubsubAppender build() {
      try {
        return new PubsubAppender(name,
                                              filter,
                                              layout,
                                              ignoreExceptions,
                                              getManager(name,
                                                         googleCloudCredentials,
                                                         projectId,
                                                         topic,
                                                         autoCreateTopic,
                                                         maxRetryTimeMillis));
      } catch (final Throwable e) {
        LOGGER.error("Error creating PubsubAppender [{}]", name, e);
        return null;
      }
    }
  }
}
