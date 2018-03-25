package com.rockon999.android.leanbacklauncher.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.rockon999.android.leanbacklauncher.util.Lists.Change.*;

public final class Lists {

    public static final class Change {
        public final int count;
        public final int index;
        public final Type type;

        public enum Type {
            INSERTION,
            REMOVAL
        }

        private Change(Type type, int index, int count) {
            this.type = type;
            this.index = index;
            this.count = count;
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append(this.type.name().toLowerCase()).append("@").append(this.index);
            if (this.count > 1) {
                buf.append("x").append(this.count);
            }
            return buf.toString();
        }
    }

    private static final class Cursor<T> {
        T element;
        int index;

        private Cursor() {
        }
    }

    public static <T> List<Change> getChanges(List<T> left, List<T> right, Comparator<T> comparator) {
        ArrayList<Change> changes = new ArrayList();
        int lSize = left.size();
        int rSize = right.size();
        int offset = 0;
        Cursor<T> l = new Cursor();
        Cursor<T> r = new Cursor();
        Cursor<T> lookAhead = new Cursor();
        while (l.index < lSize && r.index < rSize) {
            l.element = left.get(l.index);
            r.element = right.get(r.index);
            if (l.element.equals(r.element)) {
                l.index++;
                r.index++;
            } else {
                int comparison = comparator.compare(l.element, r.element);
                int count;
                if (comparison < 0) {
                    count = scanWhileLessThan(left, l, r, comparator);
                    changes.add(new Change(Type.REMOVAL, l.index + offset, count));
                    offset -= count;
                    l.index += count;
                } else if (comparison > 0) {
                    count = scanWhileLessThan(right, r, l, comparator);
                    changes.add(new Change(Type.INSERTION, l.index + offset, count));
                    offset += count;
                    r.index += count;
                } else {
                    lookAhead.index = r.index;
                    count = scanForElement(right, l, comparator, lookAhead);
                    if (lookAhead.element != null) {
                        changes.add(new Change(Type.INSERTION, l.index + offset, count));
                        offset += count;
                        l.index++;
                        r.index = lookAhead.index + 1;
                    } else {
                        lookAhead.index = l.index;
                        count = scanForElement(left, r, comparator, lookAhead);
                        changes.add(new Change(Type.REMOVAL, l.index + offset, count));
                        offset -= count;
                        l.index += count;
                    }
                }
            }
        }
        if (l.index < lSize) {
            changes.add(new Change(Type.REMOVAL, l.index + offset, lSize - l.index));
        } else if (r.index < rSize) {
            changes.add(new Change(Type.INSERTION, lSize + offset, rSize - r.index));
        }
        return changes;
    }

    private static <T> int scanWhileLessThan(List<T> list, Cursor<T> cursor, Cursor<T> reference, Comparator<T> comparator) {
        int size = list.size();
        int i = cursor.index;
        do {
            i++;
            if (i >= size) {
                break;
            }
        } while (comparator.compare(list.get(i), reference.element) < 0);
        return i - cursor.index;
    }

    private static <T> int scanForElement(List<T> list, Cursor<T> reference, Comparator<T> comparator, Cursor<T> outLookAhead) {
        int startIndex = outLookAhead.index;
        int size = list.size();
        do {
            outLookAhead.index++;
            if (outLookAhead.index >= size) {
                break;
            }
            outLookAhead.element = list.get(outLookAhead.index);
            if (reference.element.equals(outLookAhead.element)) {
                return outLookAhead.index - startIndex;
            }
        } while (comparator.compare(reference.element, outLookAhead.element) == 0);
        outLookAhead.element = null;
        return outLookAhead.index - startIndex;
    }
}
