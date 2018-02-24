package com.bumptech.glide.load.engine.bitmap_recycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GroupedLinkedMap<K extends Poolable, V> {
    private final LinkedEntry<K, V> head = new LinkedEntry();
    private final Map<K, LinkedEntry<K, V>> keyToEntry = new HashMap();

    private static class LinkedEntry<K, V> {
        private final K key;
        LinkedEntry<K, V> next;
        LinkedEntry<K, V> prev;
        private List<V> values;

        public LinkedEntry() {
            this(null);
        }

        public LinkedEntry(K key) {
            this.prev = this;
            this.next = this;
            this.key = key;
        }

        public V removeLast() {
            int valueSize = size();
            return valueSize > 0 ? this.values.remove(valueSize - 1) : null;
        }

        public int size() {
            return this.values != null ? this.values.size() : 0;
        }

        public void add(V value) {
            if (this.values == null) {
                this.values = new ArrayList();
            }
            this.values.add(value);
        }
    }

    GroupedLinkedMap() {
    }

    public void put(K key, V value) {
        LinkedEntry<K, V> entry = (LinkedEntry) this.keyToEntry.get(key);
        if (entry == null) {
            entry = new LinkedEntry(key);
            makeTail(entry);
            this.keyToEntry.put(key, entry);
        } else {
            key.offer();
        }
        entry.add(value);
    }

    public V get(K key) {
        LinkedEntry<K, V> entry = (LinkedEntry) this.keyToEntry.get(key);
        if (entry == null) {
            entry = new LinkedEntry(key);
            this.keyToEntry.put(key, entry);
        } else {
            key.offer();
        }
        makeHead(entry);
        return entry.removeLast();
    }

    public V removeLast() {
        for (LinkedEntry<K, V> last = this.head.prev; !last.equals(this.head); last = last.prev) {
            V removed = last.removeLast();
            if (removed != null) {
                return removed;
            }
            removeEntry(last);
            this.keyToEntry.remove(last.key);
            ((Poolable) last.key).offer();
        }
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("GroupedLinkedMap( ");
        boolean hadAtLeastOneItem = false;
        for (LinkedEntry<K, V> current = this.head.next; !current.equals(this.head); current = current.next) {
            hadAtLeastOneItem = true;
            sb.append('{').append(current.key).append(':').append(current.size()).append("}, ");
        }
        if (hadAtLeastOneItem) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.append(" )").toString();
    }

    private void makeHead(LinkedEntry<K, V> entry) {
        removeEntry(entry);
        entry.prev = this.head;
        entry.next = this.head.next;
        updateEntry(entry);
    }

    private void makeTail(LinkedEntry<K, V> entry) {
        removeEntry(entry);
        entry.prev = this.head.prev;
        entry.next = this.head;
        updateEntry(entry);
    }

    private static <K, V> void updateEntry(LinkedEntry<K, V> entry) {
        entry.next.prev = entry;
        entry.prev.next = entry;
    }

    private static <K, V> void removeEntry(LinkedEntry<K, V> entry) {
        entry.prev.next = entry.next;
        entry.next.prev = entry.prev;
    }
}
