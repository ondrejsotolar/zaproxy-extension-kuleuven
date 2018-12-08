package org.parosproxy.paros.extension.phishingprevention.requestscan;

import org.parosproxy.paros.network.HttpMessage;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class RequestCache {

    private ConcurrentHashMap<HttpMessage, Integer> requestCache = new ConcurrentHashMap<>();
    private int requestCounter = 0;

    public HttpMessage getRequestById(int id) {
        HttpMessage originalRequest = this.requestCache.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), id))
                .map(Map.Entry::getKey)
                .findFirst()
                .get();
        return originalRequest;
    }

    public int putRequestInCache(HttpMessage message) {
        if (requestCounter >= Integer.MAX_VALUE - 1) {
            requestCounter = 0;
            this.requestCache.clear(); // primitive cache size management
        }
        this.requestCache.put(message, requestCounter);
        return this.requestCounter++;
    }

    public ConcurrentHashMap<HttpMessage, Integer> getRequestCache() {
        return requestCache;
    }

    public int getRequestCounter() {
        return requestCounter;
    }
}
