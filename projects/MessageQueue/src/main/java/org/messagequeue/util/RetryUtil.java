package org.messagequeue.util;

import java.util.function.Supplier;

public class RetryUtil {
    public static <T> T retry(Supplier<T> operation, int maxRetries) {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                return operation.get();
            } catch (Exception e) {
                retryCount++;
                if (retryCount == maxRetries) {
                    throw new RuntimeException("Operation failed after " + maxRetries + " retries", e);
                }
                try {
                    Thread.sleep(1000 * retryCount); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new RuntimeException("Unexpected error in retry logic");
    }
}