package org.messagequeue.core;

import org.json.JSONObject;

public class Publisher {
    private MessageQueue messageQueue;

    public Publisher(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    public void publish(JSONObject content) {
        Message message = new Message(content);
        messageQueue.enqueue(message);
    }
}