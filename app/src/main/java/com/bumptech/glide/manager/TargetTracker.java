package com.bumptech.glide.manager;

import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public final class TargetTracker implements LifecycleListener {
    private final Set<Target<?>> targets = Collections.newSetFromMap(new WeakHashMap());

    public void track(Target<?> target) {
        this.targets.add(target);
    }

    public void untrack(Target<?> target) {
        this.targets.remove(target);
    }

    public void onStart() {
        for (Target<?> target : Util.getSnapshot(this.targets)) {
            target.onStart();
        }
    }

    public void onStop() {
        for (Target<?> target : Util.getSnapshot(this.targets)) {
            target.onStop();
        }
    }

    public void onDestroy() {
        for (Target<?> target : Util.getSnapshot(this.targets)) {
            target.onDestroy();
        }
    }

    public List<Target<?>> getAll() {
        return new ArrayList(this.targets);
    }

    public void clear() {
        this.targets.clear();
    }
}
