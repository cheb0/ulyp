package com.ulyp.core.recorders.basic;

import com.ulyp.core.TypeResolver;
import com.ulyp.core.bytes.BytesOut;
import com.ulyp.core.recorders.ObjectRecord;
import com.ulyp.core.util.ReflectionBasedTypeResolver;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class ByteBufferRecorderTest {

    private final ByteBufferRecorder recorder = new ByteBufferRecorder((byte) 1);
    private final TypeResolver typeResolver = new ReflectionBasedTypeResolver();

    @Test
    void shouldRecordEmptyByteBuffer() throws Exception {
        BytesOut out = BytesOut.expandableArray();
        ByteBuffer buffer = ByteBuffer.allocate(100);

        recorder.write(buffer, out, typeResolver);

        ObjectRecord objectRecord = recorder.read(
            typeResolver.get(ByteBuffer.class),
            out.flip(),
            typeResolver::getById
        );

        assertInstanceOf(ByteBufferRecord.class, objectRecord);
        ByteBufferRecord bufferRecord = (ByteBufferRecord) objectRecord;
        assertEquals(100, bufferRecord.getCapacity());
        assertEquals(0, bufferRecord.getPosition());
        assertEquals(100, bufferRecord.getLimit());
        assertEquals(100, bufferRecord.getRemaining());
    }

    @Test
    void shouldRecordByteBufferWithData() throws Exception {
        BytesOut out = BytesOut.expandableArray();
        ByteBuffer buffer = ByteBuffer.allocate(50);
        buffer.putInt(42);
        buffer.putInt(123);
        buffer.flip();

        recorder.write(buffer, out, typeResolver);

        ObjectRecord objectRecord = recorder.read(
            typeResolver.get(ByteBuffer.class),
            out.flip(),
            typeResolver::getById
        );

        assertInstanceOf(ByteBufferRecord.class, objectRecord);
        ByteBufferRecord bufferRecord = (ByteBufferRecord) objectRecord;
        assertEquals(50, bufferRecord.getCapacity());
        assertEquals(0, bufferRecord.getPosition());
        assertEquals(8, bufferRecord.getLimit());
        assertEquals(8, bufferRecord.getRemaining());
    }

    @Test
    void shouldRecordByteBufferInMiddleOfReading() throws Exception {
        BytesOut out = BytesOut.expandableArray();
        ByteBuffer buffer = ByteBuffer.allocate(100);
        buffer.putInt(42);
        buffer.putInt(123);
        buffer.putInt(456);
        buffer.flip();
        buffer.getInt(); // Read first int, position now at 4

        recorder.write(buffer, out, typeResolver);

        ObjectRecord objectRecord = recorder.read(
            typeResolver.get(ByteBuffer.class),
            out.flip(),
            typeResolver::getById
        );

        assertInstanceOf(ByteBufferRecord.class, objectRecord);
        ByteBufferRecord bufferRecord = (ByteBufferRecord) objectRecord;
        assertEquals(100, bufferRecord.getCapacity());
        assertEquals(4, bufferRecord.getPosition());
        assertEquals(12, bufferRecord.getLimit());
        assertEquals(8, bufferRecord.getRemaining());
    }

    @Test
    void shouldRecordDirectByteBuffer() throws Exception {
        BytesOut out = BytesOut.expandableArray();
        ByteBuffer buffer = ByteBuffer.allocateDirect(200);
        buffer.putLong(123456789L);
        buffer.position(4);

        recorder.write(buffer, out, typeResolver);

        ObjectRecord objectRecord = recorder.read(
            typeResolver.get(ByteBuffer.class),
            out.flip(),
            typeResolver::getById
        );

        assertInstanceOf(ByteBufferRecord.class, objectRecord);
        ByteBufferRecord bufferRecord = (ByteBufferRecord) objectRecord;
        assertEquals(200, bufferRecord.getCapacity());
        assertEquals(4, bufferRecord.getPosition());
        assertEquals(200, bufferRecord.getLimit());
        assertEquals(196, bufferRecord.getRemaining());
    }

    @Test
    void shouldRecordByteBufferWithCustomPositionAndLimit() throws Exception {
        BytesOut out = BytesOut.expandableArray();
        ByteBuffer buffer = ByteBuffer.allocate(100);
        buffer.position(10);
        buffer.limit(30);

        recorder.write(buffer, out, typeResolver);

        ObjectRecord objectRecord = recorder.read(
            typeResolver.get(ByteBuffer.class),
            out.flip(),
            typeResolver::getById
        );

        assertInstanceOf(ByteBufferRecord.class, objectRecord);
        ByteBufferRecord bufferRecord = (ByteBufferRecord) objectRecord;
        assertEquals(100, bufferRecord.getCapacity());
        assertEquals(10, bufferRecord.getPosition());
        assertEquals(30, bufferRecord.getLimit());
        assertEquals(20, bufferRecord.getRemaining());
    }

    @Test
    void shouldRecordFullByteBuffer() throws Exception {
        BytesOut out = BytesOut.expandableArray();
        ByteBuffer buffer = ByteBuffer.allocate(50);
        buffer.put(new byte[50]); // Fill the buffer
        buffer.flip();

        recorder.write(buffer, out, typeResolver);

        ObjectRecord objectRecord = recorder.read(
            typeResolver.get(ByteBuffer.class),
            out.flip(),
            typeResolver::getById
        );

        assertInstanceOf(ByteBufferRecord.class, objectRecord);
        ByteBufferRecord bufferRecord = (ByteBufferRecord) objectRecord;
        assertEquals(50, bufferRecord.getCapacity());
        assertEquals(0, bufferRecord.getPosition());
        assertEquals(50, bufferRecord.getLimit());
        assertEquals(50, bufferRecord.getRemaining());
    }

    @Test
    void shouldRecordEmptyByteBufferAtEnd() throws Exception {
        BytesOut out = BytesOut.expandableArray();
        ByteBuffer buffer = ByteBuffer.allocate(100);
        buffer.position(100); // At the end

        recorder.write(buffer, out, typeResolver);

        ObjectRecord objectRecord = recorder.read(
            typeResolver.get(ByteBuffer.class),
            out.flip(),
            typeResolver::getById
        );

        assertInstanceOf(ByteBufferRecord.class, objectRecord);
        ByteBufferRecord bufferRecord = (ByteBufferRecord) objectRecord;
        assertEquals(100, bufferRecord.getCapacity());
        assertEquals(100, bufferRecord.getPosition());
        assertEquals(100, bufferRecord.getLimit());
        assertEquals(0, bufferRecord.getRemaining());
    }

    @Test
    void shouldOnlySupportByteBufferClass() {
        assertTrue(recorder.supports(ByteBuffer.class));
        assertTrue(recorder.supports(ByteBuffer.allocate(10).getClass())); // Direct buffer
        assertFalse(recorder.supports(Object.class));
        assertFalse(recorder.supports(String.class));
        assertFalse(recorder.supports(Integer.class));
    }

    @Test
    void shouldSupportAsyncRecording() {
        assertTrue(recorder.supportsAsyncRecording());
    }

    @Test
    void shouldPreserveIdentityHashCode() throws Exception {
        BytesOut out = BytesOut.expandableArray();
        ByteBuffer buffer = ByteBuffer.allocate(100);

        recorder.write(buffer, out, typeResolver);

        ObjectRecord objectRecord = recorder.read(
            typeResolver.get(ByteBuffer.class),
            out.flip(),
            typeResolver::getById
        );

        assertInstanceOf(ByteBufferRecord.class, objectRecord);
        ByteBufferRecord bufferRecord = (ByteBufferRecord) objectRecord;
        assertEquals(System.identityHashCode(buffer), bufferRecord.getIdentityHashCode());
    }

    @Test
    void shouldHandleZeroCapacityBuffer() throws Exception {
        BytesOut out = BytesOut.expandableArray();
        ByteBuffer buffer = ByteBuffer.allocate(0);

        recorder.write(buffer, out, typeResolver);

        ObjectRecord objectRecord = recorder.read(
            typeResolver.get(ByteBuffer.class),
            out.flip(),
            typeResolver::getById
        );

        assertInstanceOf(ByteBufferRecord.class, objectRecord);
        ByteBufferRecord bufferRecord = (ByteBufferRecord) objectRecord;
        assertEquals(0, bufferRecord.getCapacity());
        assertEquals(0, bufferRecord.getPosition());
        assertEquals(0, bufferRecord.getLimit());
        assertEquals(0, bufferRecord.getRemaining());
    }

    @Test
    void shouldHandleLargeCapacityBuffer() throws Exception {
        BytesOut out = BytesOut.expandableArray();
        ByteBuffer buffer = ByteBuffer.allocate(1000000); // 1MB buffer
        buffer.position(500000);
        buffer.limit(750000);

        recorder.write(buffer, out, typeResolver);

        ObjectRecord objectRecord = recorder.read(
            typeResolver.get(ByteBuffer.class),
            out.flip(),
            typeResolver::getById
        );

        assertInstanceOf(ByteBufferRecord.class, objectRecord);
        ByteBufferRecord bufferRecord = (ByteBufferRecord) objectRecord;
        assertEquals(1000000, bufferRecord.getCapacity());
        assertEquals(500000, bufferRecord.getPosition());
        assertEquals(750000, bufferRecord.getLimit());
        assertEquals(250000, bufferRecord.getRemaining());
    }

    @Test
    void shouldHandleWrappedByteBuffer() throws Exception {
        BytesOut out = BytesOut.expandableArray();
        byte[] array = new byte[100];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.position(25);
        buffer.limit(75);

        recorder.write(buffer, out, typeResolver);

        ObjectRecord objectRecord = recorder.read(
            typeResolver.get(ByteBuffer.class),
            out.flip(),
            typeResolver::getById
        );

        assertInstanceOf(ByteBufferRecord.class, objectRecord);
        ByteBufferRecord bufferRecord = (ByteBufferRecord) objectRecord;
        assertEquals(100, bufferRecord.getCapacity());
        assertEquals(25, bufferRecord.getPosition());
        assertEquals(75, bufferRecord.getLimit());
        assertEquals(50, bufferRecord.getRemaining());
    }

    @Test
    void shouldHandleSliceByteBuffer() throws Exception {
        BytesOut out = BytesOut.expandableArray();
        ByteBuffer original = ByteBuffer.allocate(100);
        original.position(20);
        original.limit(80);
        ByteBuffer slice = original.slice();
        slice.position(10);
        slice.limit(40);

        recorder.write(slice, out, typeResolver);

        ObjectRecord objectRecord = recorder.read(
            typeResolver.get(ByteBuffer.class),
            out.flip(),
            typeResolver::getById
        );

        assertInstanceOf(ByteBufferRecord.class, objectRecord);
        ByteBufferRecord bufferRecord = (ByteBufferRecord) objectRecord;
        assertEquals(60, bufferRecord.getCapacity()); // slice capacity is 60 (80-20)
        assertEquals(10, bufferRecord.getPosition());
        assertEquals(40, bufferRecord.getLimit());
        assertEquals(30, bufferRecord.getRemaining());
    }
}
