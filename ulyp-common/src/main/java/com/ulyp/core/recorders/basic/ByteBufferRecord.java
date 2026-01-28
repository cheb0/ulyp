package com.ulyp.core.recorders.basic;

import com.ulyp.core.Type;
import com.ulyp.core.recorders.ObjectRecord;
import lombok.Getter;

@Getter
public class ByteBufferRecord extends ObjectRecord {

    private final int identityHashCode;
    private final int capacity;
    private final int position;
    private final int limit;
    private final int remaining;

    public ByteBufferRecord(Type type, int identityHashCode, int capacity, int position, int limit, int remaining) {
        super(type);
        this.identityHashCode = identityHashCode;
        this.capacity = capacity;
        this.position = position;
        this.limit = limit;
        this.remaining = remaining;
    }

    @Override
    public String toString() {
        return "ByteBuffer(capacity=" + capacity + ", position=" + position + 
               ", limit=" + limit + ", remaining=" + remaining + ")";
    }
} 