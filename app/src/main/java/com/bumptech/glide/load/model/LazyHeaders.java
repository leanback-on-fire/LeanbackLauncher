package com.bumptech.glide.load.model;

import android.text.TextUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class LazyHeaders implements Headers {
    private volatile Map<String, String> combinedHeaders;
    private final Map<String, List<LazyHeaderFactory>> headers;

    public static final class Builder {
        private static final Map<String, List<LazyHeaderFactory>> DEFAULT_HEADERS;
        private static final String DEFAULT_USER_AGENT = System.getProperty("http.agent");
        private boolean copyOnModify = true;
        private Map<String, List<LazyHeaderFactory>> headers = DEFAULT_HEADERS;
        private boolean isEncodingDefault = true;
        private boolean isUserAgentDefault = true;

        static {
            Map<String, List<LazyHeaderFactory>> temp = new HashMap(2);
            if (!TextUtils.isEmpty(DEFAULT_USER_AGENT)) {
                temp.put("User-Agent", Collections.singletonList(new StringHeaderFactory(DEFAULT_USER_AGENT)));
            }
            temp.put("Accept-Encoding", Collections.singletonList(new StringHeaderFactory("identity")));
            DEFAULT_HEADERS = Collections.unmodifiableMap(temp);
        }

        public LazyHeaders build() {
            this.copyOnModify = true;
            return new LazyHeaders(this.headers);
        }
    }

    static final class StringHeaderFactory implements LazyHeaderFactory {
        private final String value;

        StringHeaderFactory(String value) {
            this.value = value;
        }

        public String buildHeader() {
            return this.value;
        }

        public String toString() {
            String str = this.value;
            return new StringBuilder(String.valueOf(str).length() + 29).append("StringHeaderFactory{value='").append(str).append("'").append("}").toString();
        }

        public boolean equals(Object o) {
            if (!(o instanceof StringHeaderFactory)) {
                return false;
            }
            return this.value.equals(((StringHeaderFactory) o).value);
        }

        public int hashCode() {
            return this.value.hashCode();
        }
    }

    LazyHeaders(Map<String, List<LazyHeaderFactory>> headers) {
        this.headers = Collections.unmodifiableMap(headers);
    }

    public Map<String, String> getHeaders() {
        if (this.combinedHeaders == null) {
            synchronized (this) {
                if (this.combinedHeaders == null) {
                    this.combinedHeaders = Collections.unmodifiableMap(generateHeaders());
                }
            }
        }
        return this.combinedHeaders;
    }

    private Map<String, String> generateHeaders() {
        Map<String, String> combinedHeaders = new HashMap();
        for (Entry<String, List<LazyHeaderFactory>> entry : this.headers.entrySet()) {
            StringBuilder sb = new StringBuilder();
            List<LazyHeaderFactory> factories = (List) entry.getValue();
            int size = factories.size();
            for (int i = 0; i < size; i++) {
                String header = ((LazyHeaderFactory) factories.get(i)).buildHeader();
                if (!TextUtils.isEmpty(header)) {
                    sb.append(header);
                    if (i != factories.size() - 1) {
                        sb.append(',');
                    }
                }
            }
            if (!TextUtils.isEmpty(sb.toString())) {
                combinedHeaders.put((String) entry.getKey(), sb.toString());
            }
        }
        return combinedHeaders;
    }

    public String toString() {
        String valueOf = String.valueOf(this.headers);
        return new StringBuilder(String.valueOf(valueOf).length() + 21).append("LazyHeaders{headers=").append(valueOf).append("}").toString();
    }

    public boolean equals(Object o) {
        if (!(o instanceof LazyHeaders)) {
            return false;
        }
        return this.headers.equals(((LazyHeaders) o).headers);
    }

    public int hashCode() {
        return this.headers.hashCode();
    }
}
