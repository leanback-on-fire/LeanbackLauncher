package com.amazon.tv.leanbacklauncher.logging;

public interface LeanbackProto {
/*
    public static final class App extends ExtendableMessageNano<App> {
        private static volatile App[] _emptyArray;
        public String appTitle;
        public String packageName;
        public Integer position;
        public Float score;

        public static App[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new App[0];
                    }
                }
            }
            return _emptyArray;
        }

        public App() {
            clear();
        }

        public App clear() {
            this.position = null;
            this.packageName = null;
            this.appTitle = null;
            this.score = null;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.position != null) {
                output.writeUInt32(1, this.position.intValue());
            }
            if (this.packageName != null) {
                output.writeString(2, this.packageName);
            }
            if (this.appTitle != null) {
                output.writeString(3, this.appTitle);
            }
            if (this.score != null) {
                output.writeFloat(8, this.score.floatValue());
            }
            super.writeTo(output);
        }

        protected int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (this.position != null) {
                size += CodedOutputByteBufferNano.computeUInt32Size(1, this.position.intValue());
            }
            if (this.packageName != null) {
                size += CodedOutputByteBufferNano.computeStringSize(2, this.packageName);
            }
            if (this.appTitle != null) {
                size += CodedOutputByteBufferNano.computeStringSize(3, this.appTitle);
            }
            if (this.score != null) {
                return size + CodedOutputByteBufferNano.computeFloatSize(8, this.score.floatValue());
            }
            return size;
        }

        public App mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        break;
                    case 8:
                        this.position = Integer.valueOf(input.readUInt32());
                        continue;
                    case 18:
                        this.packageName = input.readString();
                        continue;
                    case 26:
                        this.appTitle = input.readString();
                        continue;
                    case 69:
                        this.score = Float.valueOf(input.readFloat());
                        continue;
                    default:
                        if (!storeUnknownField(input, tag)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }
    }

    public static final class AppRankAction extends ExtendableMessageNano<AppRankAction> {
        public App[] apps;

        public AppRankAction() {
            clear();
        }

        public AppRankAction clear() {
            this.apps = App.emptyArray();
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.apps != null && this.apps.length > 0) {
                for (App element : this.apps) {
                    if (element != null) {
                        output.writeMessage(1, element);
                    }
                }
            }
            super.writeTo(output);
        }

        protected int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (this.apps != null && this.apps.length > 0) {
                for (App element : this.apps) {
                    if (element != null) {
                        size += CodedOutputByteBufferNano.computeMessageSize(1, element);
                    }
                }
            }
            return size;
        }

        public AppRankAction mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        break;
                    case 10:
                        int i;
                        int arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 10);
                        if (this.apps == null) {
                            i = 0;
                        } else {
                            i = this.apps.length;
                        }
                        App[] newArray = new App[(i + arrayLength)];
                        if (i != 0) {
                            System.arraycopy(this.apps, 0, newArray, 0, i);
                        }
                        while (i < newArray.length - 1) {
                            newArray[i] = new App();
                            input.readMessage(newArray[i]);
                            input.readTag();
                            i++;
                        }
                        newArray[i] = new App();
                        input.readMessage(newArray[i]);
                        this.apps = newArray;
                        continue;
                    default:
                        if (!storeUnknownField(input, tag)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }
    }

    public static final class LeanbackEvent extends ExtendableMessageNano<LeanbackEvent> {
        public AppRankAction appRankAction;
        public LeanbackHeader header;
        public RankerAction rankerAction;
        public RecommendationInsertAction recommendationInsertAction;
        public RecommendationRankAction recommendationRankAction;
        public Long timestamp;
        public Integer type;

        public LeanbackEvent() {
            clear();
        }

        public LeanbackEvent clear() {
            this.type = null;
            this.timestamp = null;
            this.rankerAction = null;
            this.recommendationRankAction = null;
            this.appRankAction = null;
            this.recommendationInsertAction = null;
            this.header = null;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.type != null) {
                output.writeInt32(1, this.type.intValue());
            }
            if (this.timestamp != null) {
                output.writeInt64(2, this.timestamp.longValue());
            }
            if (this.rankerAction != null) {
                output.writeMessage(3, this.rankerAction);
            }
            if (this.recommendationRankAction != null) {
                output.writeMessage(4, this.recommendationRankAction);
            }
            if (this.appRankAction != null) {
                output.writeMessage(5, this.appRankAction);
            }
            if (this.recommendationInsertAction != null) {
                output.writeMessage(6, this.recommendationInsertAction);
            }
            if (this.header != null) {
                output.writeMessage(7, this.header);
            }
            super.writeTo(output);
        }

        protected int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (this.type != null) {
                size += CodedOutputByteBufferNano.computeInt32Size(1, this.type.intValue());
            }
            if (this.timestamp != null) {
                size += CodedOutputByteBufferNano.computeInt64Size(2, this.timestamp.longValue());
            }
            if (this.rankerAction != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(3, this.rankerAction);
            }
            if (this.recommendationRankAction != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(4, this.recommendationRankAction);
            }
            if (this.appRankAction != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(5, this.appRankAction);
            }
            if (this.recommendationInsertAction != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(6, this.recommendationInsertAction);
            }
            if (this.header != null) {
                return size + CodedOutputByteBufferNano.computeMessageSize(7, this.header);
            }
            return size;
        }

        public LeanbackEvent mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        break;
                    case 8:
                        int value = input.readInt32();
                        switch (value) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                                this.type = Integer.valueOf(value);
                                break;
                            default:
                                continue;
                        }
                    case 16:
                        this.timestamp = Long.valueOf(input.readInt64());
                        continue;
                    case 26:
                        if (this.rankerAction == null) {
                            this.rankerAction = new RankerAction();
                        }
                        input.readMessage(this.rankerAction);
                        continue;
                    case 34:
                        if (this.recommendationRankAction == null) {
                            this.recommendationRankAction = new RecommendationRankAction();
                        }
                        input.readMessage(this.recommendationRankAction);
                        continue;
                    case 42:
                        if (this.appRankAction == null) {
                            this.appRankAction = new AppRankAction();
                        }
                        input.readMessage(this.appRankAction);
                        continue;
                    case 50:
                        if (this.recommendationInsertAction == null) {
                            this.recommendationInsertAction = new RecommendationInsertAction();
                        }
                        input.readMessage(this.recommendationInsertAction);
                        continue;
                    case 58:
                        if (this.header == null) {
                            this.header = new LeanbackHeader();
                        }
                        input.readMessage(this.header);
                        continue;
                    default:
                        if (!storeUnknownField(input, tag)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }
    }

    public static final class LeanbackHeader extends ExtendableMessageNano<LeanbackHeader> {
        public String launcherVersion;

        public LeanbackHeader() {
            clear();
        }

        public LeanbackHeader clear() {
            this.launcherVersion = null;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.launcherVersion != null) {
                output.writeString(1, this.launcherVersion);
            }
            super.writeTo(output);
        }

        protected int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (this.launcherVersion != null) {
                return size + CodedOutputByteBufferNano.computeStringSize(1, this.launcherVersion);
            }
            return size;
        }

        public LeanbackHeader mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        break;
                    case 10:
                        this.launcherVersion = input.readString();
                        continue;
                    default:
                        if (!storeUnknownField(input, tag)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }
    }

    public static final class RankerAction extends ExtendableMessageNano<RankerAction> {
        public Integer actionType;
        public String packageName;
        public Integer rowPosition;
        public String tag;

        public RankerAction() {
            clear();
        }

        public RankerAction clear() {
            this.packageName = null;
            this.actionType = null;
            this.rowPosition = null;
            this.tag = null;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.packageName != null) {
                output.writeString(1, this.packageName);
            }
            if (this.actionType != null) {
                output.writeUInt32(2, this.actionType.intValue());
            }
            if (this.rowPosition != null) {
                output.writeUInt32(3, this.rowPosition.intValue());
            }
            if (this.tag != null) {
                output.writeString(4, this.tag);
            }
            super.writeTo(output);
        }

        protected int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (this.packageName != null) {
                size += CodedOutputByteBufferNano.computeStringSize(1, this.packageName);
            }
            if (this.actionType != null) {
                size += CodedOutputByteBufferNano.computeUInt32Size(2, this.actionType.intValue());
            }
            if (this.rowPosition != null) {
                size += CodedOutputByteBufferNano.computeUInt32Size(3, this.rowPosition.intValue());
            }
            if (this.tag != null) {
                return size + CodedOutputByteBufferNano.computeStringSize(4, this.tag);
            }
            return size;
        }

        public RankerAction mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        break;
                    case 10:
                        this.packageName = input.readString();
                        continue;
                    case 16:
                        this.actionType = Integer.valueOf(input.readUInt32());
                        continue;
                    case 24:
                        this.rowPosition = Integer.valueOf(input.readUInt32());
                        continue;
                    case 34:
                        this.tag = input.readString();
                        continue;
                    default:
                        if (!storeUnknownField(input, tag)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }
    }

    public static final class Recommendation extends ExtendableMessageNano<Recommendation> {
        private static volatile Recommendation[] _emptyArray;
        public Integer bucketId;
        public Float normalizedPriority;
        public Integer notificationId;
        public String packageName;
        public Integer position;
        public Float score;
        public String tagName;
        public Integer unnormalizedPriority;

        public static Recommendation[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Recommendation[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Recommendation() {
            clear();
        }

        public Recommendation clear() {
            this.position = null;
            this.packageName = null;
            this.tagName = null;
            this.bucketId = null;
            this.notificationId = null;
            this.unnormalizedPriority = null;
            this.normalizedPriority = null;
            this.score = null;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.position != null) {
                output.writeUInt32(1, this.position.intValue());
            }
            if (this.packageName != null) {
                output.writeString(2, this.packageName);
            }
            if (this.tagName != null) {
                output.writeString(3, this.tagName);
            }
            if (this.bucketId != null) {
                output.writeUInt32(4, this.bucketId.intValue());
            }
            if (this.notificationId != null) {
                output.writeUInt32(5, this.notificationId.intValue());
            }
            if (this.unnormalizedPriority != null) {
                output.writeInt32(6, this.unnormalizedPriority.intValue());
            }
            if (this.normalizedPriority != null) {
                output.writeFloat(7, this.normalizedPriority.floatValue());
            }
            if (this.score != null) {
                output.writeFloat(8, this.score.floatValue());
            }
            super.writeTo(output);
        }

        protected int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (this.position != null) {
                size += CodedOutputByteBufferNano.computeUInt32Size(1, this.position.intValue());
            }
            if (this.packageName != null) {
                size += CodedOutputByteBufferNano.computeStringSize(2, this.packageName);
            }
            if (this.tagName != null) {
                size += CodedOutputByteBufferNano.computeStringSize(3, this.tagName);
            }
            if (this.bucketId != null) {
                size += CodedOutputByteBufferNano.computeUInt32Size(4, this.bucketId.intValue());
            }
            if (this.notificationId != null) {
                size += CodedOutputByteBufferNano.computeUInt32Size(5, this.notificationId.intValue());
            }
            if (this.unnormalizedPriority != null) {
                size += CodedOutputByteBufferNano.computeInt32Size(6, this.unnormalizedPriority.intValue());
            }
            if (this.normalizedPriority != null) {
                size += CodedOutputByteBufferNano.computeFloatSize(7, this.normalizedPriority.floatValue());
            }
            if (this.score != null) {
                return size + CodedOutputByteBufferNano.computeFloatSize(8, this.score.floatValue());
            }
            return size;
        }

        public Recommendation mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        break;
                    case 8:
                        this.position = Integer.valueOf(input.readUInt32());
                        continue;
                    case 18:
                        this.packageName = input.readString();
                        continue;
                    case 26:
                        this.tagName = input.readString();
                        continue;
                    case 32:
                        this.bucketId = Integer.valueOf(input.readUInt32());
                        continue;
                    case 40:
                        this.notificationId = Integer.valueOf(input.readUInt32());
                        continue;
                    case 48:
                        this.unnormalizedPriority = Integer.valueOf(input.readInt32());
                        continue;
                    case 61:
                        this.normalizedPriority = Float.valueOf(input.readFloat());
                        continue;
                    case 69:
                        this.score = Float.valueOf(input.readFloat());
                        continue;
                    default:
                        if (!storeUnknownField(input, tag)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }
    }

    public static final class RecommendationInsertAction extends ExtendableMessageNano<RecommendationInsertAction> {
        public Integer bucketId;
        public Float normalizedPriority;
        public String packageName;
        public Integer position;
        public Float score;
        public String tagName;
        public Integer unnormalizedPriority;

        public RecommendationInsertAction() {
            clear();
        }

        public RecommendationInsertAction clear() {
            this.position = null;
            this.packageName = null;
            this.tagName = null;
            this.bucketId = null;
            this.unnormalizedPriority = null;
            this.normalizedPriority = null;
            this.score = null;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.position != null) {
                output.writeUInt32(1, this.position.intValue());
            }
            if (this.packageName != null) {
                output.writeString(2, this.packageName);
            }
            if (this.tagName != null) {
                output.writeString(3, this.tagName);
            }
            if (this.bucketId != null) {
                output.writeUInt32(4, this.bucketId.intValue());
            }
            if (this.unnormalizedPriority != null) {
                output.writeInt32(5, this.unnormalizedPriority.intValue());
            }
            if (this.normalizedPriority != null) {
                output.writeFloat(6, this.normalizedPriority.floatValue());
            }
            if (this.score != null) {
                output.writeFloat(7, this.score.floatValue());
            }
            super.writeTo(output);
        }

        protected int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (this.position != null) {
                size += CodedOutputByteBufferNano.computeUInt32Size(1, this.position.intValue());
            }
            if (this.packageName != null) {
                size += CodedOutputByteBufferNano.computeStringSize(2, this.packageName);
            }
            if (this.tagName != null) {
                size += CodedOutputByteBufferNano.computeStringSize(3, this.tagName);
            }
            if (this.bucketId != null) {
                size += CodedOutputByteBufferNano.computeUInt32Size(4, this.bucketId.intValue());
            }
            if (this.unnormalizedPriority != null) {
                size += CodedOutputByteBufferNano.computeInt32Size(5, this.unnormalizedPriority.intValue());
            }
            if (this.normalizedPriority != null) {
                size += CodedOutputByteBufferNano.computeFloatSize(6, this.normalizedPriority.floatValue());
            }
            if (this.score != null) {
                return size + CodedOutputByteBufferNano.computeFloatSize(7, this.score.floatValue());
            }
            return size;
        }

        public RecommendationInsertAction mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        break;
                    case 8:
                        this.position = Integer.valueOf(input.readUInt32());
                        continue;
                    case 18:
                        this.packageName = input.readString();
                        continue;
                    case 26:
                        this.tagName = input.readString();
                        continue;
                    case 32:
                        this.bucketId = Integer.valueOf(input.readUInt32());
                        continue;
                    case 40:
                        this.unnormalizedPriority = Integer.valueOf(input.readInt32());
                        continue;
                    case 53:
                        this.normalizedPriority = Float.valueOf(input.readFloat());
                        continue;
                    case 61:
                        this.score = Float.valueOf(input.readFloat());
                        continue;
                    default:
                        if (!storeUnknownField(input, tag)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }
    }

    public static final class RecommendationRankAction extends ExtendableMessageNano<RecommendationRankAction> {
        public Recommendation[] recommendations;

        public RecommendationRankAction() {
            clear();
        }

        public RecommendationRankAction clear() {
            this.recommendations = Recommendation.emptyArray();
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.recommendations != null && this.recommendations.length > 0) {
                for (Recommendation element : this.recommendations) {
                    if (element != null) {
                        output.writeMessage(1, element);
                    }
                }
            }
            super.writeTo(output);
        }

        protected int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (this.recommendations != null && this.recommendations.length > 0) {
                for (Recommendation element : this.recommendations) {
                    if (element != null) {
                        size += CodedOutputByteBufferNano.computeMessageSize(1, element);
                    }
                }
            }
            return size;
        }

        public RecommendationRankAction mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        break;
                    case 10:
                        int i;
                        int arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 10);
                        if (this.recommendations == null) {
                            i = 0;
                        } else {
                            i = this.recommendations.length;
                        }
                        Recommendation[] newArray = new Recommendation[(i + arrayLength)];
                        if (i != 0) {
                            System.arraycopy(this.recommendations, 0, newArray, 0, i);
                        }
                        while (i < newArray.length - 1) {
                            newArray[i] = new Recommendation();
                            input.readMessage(newArray[i]);
                            input.readTag();
                            i++;
                        }
                        newArray[i] = new Recommendation();
                        input.readMessage(newArray[i]);
                        this.recommendations = newArray;
                        continue;
                    default:
                        if (!storeUnknownField(input, tag)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }
    }*/
}
