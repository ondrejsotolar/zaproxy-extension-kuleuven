package org.parosproxy.paros.extension.phishingprevention;

import org.parosproxy.paros.network.HttpMessage;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class RequestCache {

    private ConcurrentHashMap<HttpMessage, Integer> requestCache = new ConcurrentHashMap<>();
    private int requestCounter = 0;
    private int cacheSize = 65535;

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
        if (requestCounter >= cacheSize) {
            requestCounter = 0;
        }
        this.requestCache.put(message, requestCounter);
        return this.requestCounter++;
    }

    public void remove(HttpMessage message) {
        requestCache.remove(message);
    }

    public ConcurrentHashMap<HttpMessage, Integer> getRequestCache() {
        return requestCache;
    }

    public int getRequestCounter() {
        return requestCounter;
    }
}
