package org.messagequeue.core;

import org.messagequeue.exception.CyclicDependencyException;
import org.messagequeue.exception.InvalidDependencyException;

import java.util.*;

public class SubscriberManager {
    private Map<String, Subscriber> subscribers = new HashMap<>();
    private Map<String, Set<String>> dependencies = new HashMap<>();

    public void addSubscriber(Subscriber subscriber) {
        subscribers.put(subscriber.getId(), subscriber);
    }

    public void removeSubscriber(String subscriberId) {
        subscribers.remove(subscriberId);
        dependencies.remove(subscriberId);
        for (Set<String> deps : dependencies.values()) {
            deps.remove(subscriberId);
        }
    }

    public void addDependency(String subscriberId, String dependencyId) throws InvalidDependencyException {
        System.out.println("Checking for cycle: " + subscriberId + " -> " + dependencyId);
        // Check cyclic dependency before adding to the dependencies
        Map<String, Set<String>> tempDependencies = new HashMap<>();
        for(Map.Entry<String, Set<String>> entry : dependencies.entrySet()) {
            tempDependencies.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        tempDependencies.computeIfAbsent(subscriberId, k -> new HashSet<>()).add(dependencyId);
        // Check for cycles using DFS
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        if (hasCycle(subscriberId, visited, recursionStack, tempDependencies)) {
            throw new InvalidDependencyException("Adding " + subscriberId + " -> " + dependencyId + " dependency can led to cyclic dependency");
        }
        dependencies.computeIfAbsent(subscriberId, k -> new HashSet<>()).add(dependencyId);
        System.out.println("Dependency added: " + subscriberId + " -> " + dependencyId);
    }

    private boolean hasCycle(String current, Set<String> visited, Set<String> recursionStack, Map<String, Set<String>> tempDependencies) {
        // Mark the current node as visited and add it to the recursion stack
        if (recursionStack.contains(current)) {
            return true; // Cycle detected
        }
        if (visited.contains(current)) {
            return false; // No cycle detected in this path
        }

        visited.add(current);
        recursionStack.add(current);

        // Recur for all dependencies of the current subscriber
        Set<String> deps = tempDependencies.getOrDefault(current, Collections.emptySet());
        for (String dep : deps) {
            if (hasCycle(dep, visited, recursionStack, tempDependencies)) {
                return true;
            }
        }

        // Remove the current node from the recursion stack
        recursionStack.remove(current);
        return false;
    }


    public List<Subscriber> getOrderedSubscribers() throws CyclicDependencyException {
        Map<String, Integer> inDegree = new HashMap<>();
        for (String subscriberId : subscribers.keySet()) {
            inDegree.put(subscriberId, 0);
        }

        for (Set<String> deps : dependencies.values()) {
            for (String dep : deps) {
                inDegree.put(dep, inDegree.getOrDefault(dep, 0) + 1);
            }
        }

        Queue<String> queue = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }

        List<Subscriber> orderedSubscribers = new ArrayList<>();
        while (!queue.isEmpty()) {
            String subscriberId = queue.poll();
            orderedSubscribers.add(subscribers.get(subscriberId));

            Set<String> deps = dependencies.getOrDefault(subscriberId, Collections.emptySet());
            for (String dep : deps) {
                inDegree.put(dep, inDegree.get(dep) - 1);
                if (inDegree.get(dep) == 0) {
                    queue.offer(dep);
                }
            }
        }

        if (orderedSubscribers.size() != subscribers.size()) {
            throw new CyclicDependencyException("Cyclic dependency detected in subscriber graph");
        }

        return orderedSubscribers;
    }
}