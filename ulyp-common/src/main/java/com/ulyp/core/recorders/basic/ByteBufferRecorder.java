package com.ulyp.core.recorders.basic;

import com.ulyp.core.ByIdTypeResolver;
import com.ulyp.core.Type;
import com.ulyp.core.TypeResolver;
import com.ulyp.core.bytes.BytesIn;
import com.ulyp.core.bytes.BytesOut;
import com.ulyp.core.recorders.ObjectRecorder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.ThreadSafe;
import java.nio.ByteBuffer;

@ThreadSafe
public class ByteBufferRecorder extends ObjectRecorder {

    public ByteBufferRecorder(byte id) {
        super(id);
    }

    @Override
    public boolean supports(Class<?> type) {
        return ByteBuffer.class.isAssignableFrom(type);
    }

    @Override
    public boolean supportsAsyncRecording() {
        return true;
    }

    @Override
    public ByteBufferRecord read(@NotNull Type objectType, BytesIn input, ByIdTypeResolver typeResolver) {
        int identityHashCode = input.readInt();
        int capacity = input.readInt();
        int position = input.readInt();
        int limit = input.readInt();
        int remaining = input.readInt();
        return new ByteBufferRecord(objectType, identityHashCode, capacity, position, limit, remaining);
    }

    @Override
    public void write(Object object, BytesOut out, TypeResolver typeResolver) throws Exception {
        ByteBuffer buffer = (ByteBuffer) object;
        out.write(System.identityHashCode(buffer));
        out.write(buffer.capacity());
        out.write(buffer.position());
        out.write(buffer.limit());
        out.write(buffer.remaining());
    }
}
