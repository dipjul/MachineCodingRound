package org.messagequeue.core;

import org.messagequeue.exception.CyclicDependencyException;
import org.messagequeue.exception.InvalidDependencyException;
import org.messagequeue.util.RetryUtil;

import java.util.List;

public class MessageQueue {
    private CustomQueue<Message> queue = new CustomQueue<>();
    private SubscriberManager subscriberManager = new SubscriberManager();

    public void enqueue(Message message) {
        queue.enqueue(message);
        processMessages();
    }

    public void addSubscriber(Subscriber subscriber) {
        subscriberManager.addSubscriber(subscriber);
    }

    public void removeSubscriber(String subscriberId) {
        subscriberManager.removeSubscriber(subscriberId);
    }

    public void addDependency(String subscriberId, String dependencyId) {
        try {
            subscriberManager.addDependency(subscriberId, dependencyId);
        } catch (InvalidDependencyException e) {
            System.err.println("ERROR:" + e.getMessage());
        }
    }

    private void processMessages() {
        while (!queue.isEmpty()) {
            Message message = queue.dequeue();
            try {
                List<Subscriber> orderedSubscribers = subscriberManager.getOrderedSubscribers();

                for (Subscriber subscriber : orderedSubscribers) {
                    if (subscriber.matches(message)) {
                        try {
                            RetryUtil.retry(() -> {
                                subscriber.process(message);
                                return null; // Since we don't need to return anything
                            }, 3);
                        } catch (RuntimeException e) {
                            System.err.println("Failed to process message after 3 retries for subscriber " + subscriber.getId());
                        }
                    }
                }
            } catch (CyclicDependencyException e) {
                System.err.println("Error processing messages: " + e.getMessage());
            }
        }
    }

    // New method for testing
    public void processAllMessages() {
        processMessages();
    }
}