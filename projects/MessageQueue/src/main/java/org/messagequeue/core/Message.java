package org.messagequeue.core;

import org.json.JSONObject;

public class Message {
    private JSONObject content;

    public Message(JSONObject content) {
        this.content = content;
    }

    public JSONObject getContent() {
        return content;
    }
}