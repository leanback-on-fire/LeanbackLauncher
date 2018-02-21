package com.rockon999.android.leanbacklauncher.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
        ArrayList<Change> changes = new ArrayList<>();

        int lSize = left.size();
        int rSize = right.size();
        int offset = 0;
        Cursor<T> l = new Cursor<>();
        Cursor<T> r = new Cursor<>();
        Cursor<T> lookAhead = new Cursor<>();
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
                    if (l.index + offset == Change.Type.INSERTION.ordinal())
                        changes.add(new Change(Change.Type.INSERTION, count, 0));
                    else if (l.index + offset == Change.Type.REMOVAL.ordinal())
                        changes.add(new Change(Change.Type.REMOVAL, count, 0));
                    //changes.add(new Change(l.index + offset, count, null));
                    offset -= count;
                    l.index += count;
                } else if (comparison > 0) {
                    count = scanWhileLessThan(right, r, l, comparator);
                    if (l.index + offset == Change.Type.INSERTION.ordinal())
                        changes.add(new Change(Change.Type.INSERTION, count, 0));
                    else if (l.index + offset == Change.Type.REMOVAL.ordinal())
                        changes.add(new Change(Change.Type.REMOVAL, count, 0));
                    //changes.add(new Change(l.index + offset, count, null));
                    offset += count;
                    r.index += count;
                } else {
                    lookAhead.index = r.index;
                    count = scanForElement(right, l, comparator, lookAhead);
                    if (lookAhead.element != null) {
                        if (l.index + offset == Change.Type.INSERTION.ordinal())
                            changes.add(new Change(Change.Type.INSERTION, count, 0));
                        else if (l.index + offset == Change.Type.REMOVAL.ordinal())
                            changes.add(new Change(Change.Type.REMOVAL, count, 0));
                        //changes.add(new Change(l.index + offset, count, null));
                        offset += count;
                        l.index++;
                        r.index = lookAhead.index + 1;
                    } else {
                        lookAhead.index = l.index;
                        count = scanForElement(left, r, comparator, lookAhead);
                        if (l.index + offset == Change.Type.INSERTION.ordinal())
                            changes.add(new Change(Change.Type.INSERTION, count, 0));
                        else if (l.index + offset == Change.Type.REMOVAL.ordinal())
                            changes.add(new Change(Change.Type.REMOVAL, count, 0));
                        //changes.add(new Change(l.index + offset, count, null));
                        offset -= count;
                        l.index += count;
                    }
                }
            }
        }
        if (l.index < lSize) {
            if (l.index + offset == Change.Type.INSERTION.ordinal())
                changes.add(new Change(Change.Type.INSERTION, lSize - 1, 0));
            else if (l.index + offset == Change.Type.REMOVAL.ordinal())
                changes.add(new Change(Change.Type.REMOVAL, lSize - 1, 0));
            //changes.add(new Change(l.index + offset, lSize - l.index, null));
        } else if (r.index < rSize) {
            if (l.index + offset == Change.Type.INSERTION.ordinal())
                changes.add(new Change(Change.Type.INSERTION, rSize - r.index, 0));
            else if (l.index + offset == Change.Type.REMOVAL.ordinal())
                changes.add(new Change(Change.Type.REMOVAL, rSize - r.index, 0));
            //changes.add(new Change(lSize + offset, rSize - r.index, null));
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
        while (true) {
            outLookAhead.index++;
            if (outLookAhead.index < size) {
                outLookAhead.element = list.get(outLookAhead.index);
                if (!reference.element.equals(outLookAhead.element)) {
                    if (comparator.compare(reference.element, outLookAhead.element) != 0) {
                        break;
                    }
                } else {
                    return outLookAhead.index - startIndex;
                }
            }
            break;
        }
        outLookAhead.element = null;
        return outLookAhead.index - startIndex;
    }

    private Lists() {
    }
}
