package com.bumptech.glide.load.engine;

import android.util.Log;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GlideException extends Exception {
    private static final StackTraceElement[] EMPTY_ELEMENTS = new StackTraceElement[0];
    private final List<Exception> causes;
    private Class<?> dataClass;
    private DataSource dataSource;
    private Key key;

    private static final class IndentedAppendable implements Appendable {
        private final Appendable appendable;
        private boolean printedNewLine = true;

        IndentedAppendable(Appendable appendable) {
            this.appendable = appendable;
        }

        public Appendable append(char c) throws IOException {
            boolean z = false;
            if (this.printedNewLine) {
                this.printedNewLine = false;
                this.appendable.append("  ");
            }
            if (c == '\n') {
                z = true;
            }
            this.printedNewLine = z;
            this.appendable.append(c);
            return this;
        }

        public Appendable append(CharSequence charSequence) throws IOException {
            charSequence = safeSequence(charSequence);
            return append(charSequence, 0, charSequence.length());
        }

        public Appendable append(CharSequence charSequence, int start, int end) throws IOException {
            boolean z = false;
            charSequence = safeSequence(charSequence);
            if (this.printedNewLine) {
                this.printedNewLine = false;
                this.appendable.append("  ");
            }
            if (charSequence.length() > 0 && charSequence.charAt(end - 1) == '\n') {
                z = true;
            }
            this.printedNewLine = z;
            this.appendable.append(charSequence, start, end);
            return this;
        }

        private CharSequence safeSequence(CharSequence sequence) {
            if (sequence == null) {
                return "";
            }
            return sequence;
        }
    }

    public GlideException(String message) {
        this(message, Collections.emptyList());
    }

    public GlideException(String detailMessage, Exception cause) {
        this(detailMessage, Collections.singletonList(cause));
    }

    public GlideException(String detailMessage, List<Exception> causes) {
        super(detailMessage);
        setStackTrace(EMPTY_ELEMENTS);
        this.causes = causes;
    }

    void setLoggingDetails(Key key, DataSource dataSource) {
        setLoggingDetails(key, dataSource, null);
    }

    void setLoggingDetails(Key key, DataSource dataSource, Class<?> dataClass) {
        this.key = key;
        this.dataSource = dataSource;
        this.dataClass = dataClass;
    }

    public Throwable fillInStackTrace() {
        return this;
    }

    public List<Exception> getCauses() {
        return this.causes;
    }

    public List<Exception> getRootCauses() {
        List<Exception> rootCauses = new ArrayList();
        addRootCauses(this, rootCauses);
        return rootCauses;
    }

    public void logRootCauses(String tag) {
        String valueOf = String.valueOf(getClass());
        String valueOf2 = String.valueOf(getMessage());
        Log.e(tag, new StringBuilder((String.valueOf(valueOf).length() + 2) + String.valueOf(valueOf2).length()).append(valueOf).append(": ").append(valueOf2).toString());
        List<Exception> causes = getRootCauses();
        int size = causes.size();
        for (int i = 0; i < size; i++) {
            Log.i(tag, "Root cause (" + (i + 1) + " of " + size + ")", (Throwable) causes.get(i));
        }
    }

    private void addRootCauses(Exception exception, List<Exception> rootCauses) {
        if (exception instanceof GlideException) {
            for (Exception e : ((GlideException) exception).getCauses()) {
                addRootCauses(e, rootCauses);
            }
            return;
        }
        rootCauses.add(exception);
    }

    public void printStackTrace() {
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintStream err) {
        printStackTrace((Appendable) err);
    }

    public void printStackTrace(PrintWriter err) {
        printStackTrace((Appendable) err);
    }

    private void printStackTrace(Appendable appendable) {
        appendExceptionMessage(this, appendable);
        appendCauses(getCauses(), new IndentedAppendable(appendable));
    }

    public String getMessage() {
        String valueOf;
        String valueOf2;
        String valueOf3;
        String valueOf4 = String.valueOf(super.getMessage());
        if (this.dataClass != null) {
            valueOf = String.valueOf(this.dataClass);
            valueOf = new StringBuilder(String.valueOf(valueOf).length() + 2).append(", ").append(valueOf).toString();
        } else {
            valueOf = "";
        }
        if (this.dataSource != null) {
            valueOf2 = String.valueOf(this.dataSource);
            valueOf2 = new StringBuilder(String.valueOf(valueOf2).length() + 2).append(", ").append(valueOf2).toString();
        } else {
            valueOf2 = "";
        }
        if (this.key != null) {
            valueOf3 = String.valueOf(this.key);
            valueOf3 = new StringBuilder(String.valueOf(valueOf3).length() + 2).append(", ").append(valueOf3).toString();
        } else {
            valueOf3 = "";
        }
        return new StringBuilder((((String.valueOf(valueOf4).length() + 0) + String.valueOf(valueOf).length()) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()).append(valueOf4).append(valueOf).append(valueOf2).append(valueOf3).toString();
    }

    private static void appendExceptionMessage(Exception e, Appendable appendable) {
        try {
            appendable.append(e.getClass().toString()).append(": ").append(e.getMessage()).append('\n');
        } catch (IOException e2) {
            throw new RuntimeException(e);
        }
    }

    private static void appendCauses(List<Exception> causes, Appendable appendable) {
        try {
            appendCausesWrapped(causes, appendable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void appendCausesWrapped(List<Exception> causes, Appendable appendable) throws IOException {
        int size = causes.size();
        for (int i = 0; i < size; i++) {
            appendable.append("Cause (").append(String.valueOf(i + 1)).append(" of ").append(String.valueOf(size)).append("): ");
            Exception cause = (Exception) causes.get(i);
            if (cause instanceof GlideException) {
                ((GlideException) cause).printStackTrace(appendable);
            } else {
                appendExceptionMessage(cause, appendable);
            }
        }
    }
}
