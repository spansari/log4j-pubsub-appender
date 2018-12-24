package io.spanlab.log4j.pubsub.appender;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GoogleCloudCredentials.class})
@PowerMockIgnore({"javax.management.*", "javax.crypto.*"})
public class GoogleCloudCredentialsTest {
  @Spy
  GoogleCloudCredentials.Builder googleCloudCredentialsBuilder =
      new GoogleCloudCredentials.Builder();

  @Before
  public void setup() throws Exception {
    doReturn(mock(File.class))
        .when(googleCloudCredentialsBuilder)
        .getServiceAccountJsonFile();
  }

}