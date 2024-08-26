package org.messagequeue;

import org.json.JSONObject;
import org.messagequeue.core.MessageQueue;
import org.messagequeue.core.Publisher;
import org.messagequeue.core.Subscriber;

public class Main {
    public static void main(String[] args) {
        MessageQueue messageQueue = new MessageQueue();
        Publisher publisher = new Publisher(messageQueue);

        // Create subscribers
        Subscriber subscriber1 = new Subscriber("sub1", ".*type\":\"A\".*", message -> System.out.println("Subscriber 1: " + message.getContent()));
        Subscriber subscriber2 = new Subscriber("sub2", ".*type\":\"B\".*", message -> System.out.println("Subscriber 2: " + message.getContent()));
        Subscriber subscriber3 = new Subscriber("sub3", ".*priority\":\"high\".*", message -> System.out.println("Subscriber 3: " + message.getContent()));

        // Add subscribers to the message queue
        messageQueue.addSubscriber(subscriber1);
        messageQueue.addSubscriber(subscriber2);
        messageQueue.addSubscriber(subscriber3);

        // Set up dependencies
        messageQueue.addDependency("sub1", "sub3");
        messageQueue.addDependency("sub2", "sub3");
        messageQueue.addDependency("sub3", "sub1");

        // Publish messages
        publisher.publish(new JSONObject().put("type", "A").put("content", "Message A"));
        publisher.publish(new JSONObject().put("type", "B").put("content", "Message B"));
        publisher.publish(new JSONObject().put("type", "C").put("priority", "high").put("content", "High priority message"));

        // Remove a subscriber
        messageQueue.removeSubscriber("sub2");

        // Publish another message
        publisher.publish(new JSONObject().put("type", "B").put("content", "Message B2"));
    }
}