package org.messagequeue.core;

import java.util.function.Consumer;
import java.util.regex.Pattern;

public class Subscriber {
    private String id;
    private Pattern pattern;
    private Consumer<Message> callback;

    public Subscriber(String id, String regex, Consumer<Message> callback) {
        this.id = id;
        this.pattern = Pattern.compile(regex);
        this.callback = callback;
    }

    public String getId() {
        return id;
    }

    public boolean matches(Message message) {
        return pattern.matcher(message.getContent().toString()).matches();
    }

    public void process(Message message) {
        callback.accept(message);
    }
}