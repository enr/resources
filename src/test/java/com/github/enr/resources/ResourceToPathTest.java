package com.github.enr.resources;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class ResourceToPathTest {

  @Test
  void testFileSystemResourceToPath() {
    // Create a temporary file for testing
    try {
      Path tempFile = Files.createTempFile("test", ".txt");
      Files.writeString(tempFile, "test content");

      FileSystemResource resource = new FileSystemResource(tempFile.toString());

      // Test with default strategy (STRICT)
      Path result = resource.getAsPath();
      assertEquals(tempFile, result);

      // Test with explicit STRICT strategy
      Path resultStrict = resource.getAsPath(PathConversionStrategy.STRICT);
      assertEquals(tempFile, resultStrict);

      // Test with LENIENT strategy
      Path resultLenient = resource.getAsPath(PathConversionStrategy.LENIENT);
      assertEquals(tempFile, resultLenient);

      // Test with FORCE_TEMPORARY strategy
      Path resultForce = resource.getAsPath(PathConversionStrategy.FORCE_TEMPORARY);
      assertEquals(tempFile, resultForce);

      // Cleanup
      Files.deleteIfExists(tempFile);
    } catch (IOException e) {
      fail("Test failed due to IO exception: " + e.getMessage());
    }
  }

  @Test
  void testEnvironmentResourceToPath() {
    // Set a test environment variable
    String testKey = "TEST_RESOURCE_PATH";
    String testValue = "test environment value";

    // Create environment resource with a mock environment source
    EnvironmentResource resource = new EnvironmentResource(testKey, new com.github.enr.system.EnvironmentSource() {
      @Override
      public String getEnvironmentVar(String key) {
        return testKey.equals(key) ? testValue : null;
      }
    });

    // Test that STRICT strategy throws exception
    assertThrows(ResourceLoadingException.class, () -> {
      resource.getAsPath(PathConversionStrategy.STRICT);
    });

    // Test that LENIENT strategy creates temporary file
    try {
      Path result = resource.getAsPath(PathConversionStrategy.LENIENT);
      assertTrue(Files.exists(result));
      assertEquals(testValue, Files.readString(result));

      // Cleanup
      Files.deleteIfExists(result);
    } catch (IOException e) {
      fail("Test failed due to IO exception: " + e.getMessage());
    }

    // Test that FORCE_TEMPORARY strategy creates temporary file
    try {
      Path result = resource.getAsPath(PathConversionStrategy.FORCE_TEMPORARY);
      assertTrue(Files.exists(result));
      assertEquals(testValue, Files.readString(result));

      // Cleanup
      Files.deleteIfExists(result);
    } catch (IOException e) {
      fail("Test failed due to IO exception: " + e.getMessage());
    }
  }

  @Test
  void testUnreadableResourceToPath() {
    UnreadableResource resource = new UnreadableResource("unreadable://test");

    // Test that all strategies throw exception
    assertThrows(ResourceLoadingException.class, () -> {
      resource.getAsPath(PathConversionStrategy.STRICT);
    });

    assertThrows(ResourceLoadingException.class, () -> {
      resource.getAsPath(PathConversionStrategy.LENIENT);
    });

    assertThrows(ResourceLoadingException.class, () -> {
      resource.getAsPath(PathConversionStrategy.FORCE_TEMPORARY);
    });

    assertThrows(ResourceLoadingException.class, () -> {
      resource.getAsPath();
    });
  }
}
