package com.bumptech.glide.manager;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public class RequestTracker {
    private boolean isPaused;
    private final List<Request> pendingRequests = new ArrayList();
    private final Set<Request> requests = Collections.newSetFromMap(new WeakHashMap());

    public void runRequest(Request request) {
        this.requests.add(request);
        if (this.isPaused) {
            this.pendingRequests.add(request);
        } else {
            request.begin();
        }
    }

    public boolean clearRemoveAndRecycle(Request request) {
        boolean isOwnedByUs = request != null && (this.requests.remove(request) || this.pendingRequests.remove(request));
        if (isOwnedByUs) {
            request.clear();
            request.recycle();
        }
        return isOwnedByUs;
    }

    public void pauseRequests() {
        this.isPaused = true;
        for (Request request : Util.getSnapshot(this.requests)) {
            if (request.isRunning()) {
                request.pause();
                this.pendingRequests.add(request);
            }
        }
    }

    public void resumeRequests() {
        this.isPaused = false;
        for (Request request : Util.getSnapshot(this.requests)) {
            if (!(request.isComplete() || request.isCancelled() || request.isRunning())) {
                request.begin();
            }
        }
        this.pendingRequests.clear();
    }

    public void clearRequests() {
        for (Request request : Util.getSnapshot(this.requests)) {
            clearRemoveAndRecycle(request);
        }
        this.pendingRequests.clear();
    }

    public void restartRequests() {
        for (Request request : Util.getSnapshot(this.requests)) {
            if (!(request.isComplete() || request.isCancelled())) {
                request.pause();
                if (this.isPaused) {
                    this.pendingRequests.add(request);
                } else {
                    request.begin();
                }
            }
        }
    }

    public String toString() {
        String valueOf = String.valueOf(super.toString());
        int size = this.requests.size();
        return new StringBuilder(String.valueOf(valueOf).length() + 41).append(valueOf).append("{numRequests=").append(size).append(", isPaused=").append(this.isPaused).append("}").toString();
    }
}
