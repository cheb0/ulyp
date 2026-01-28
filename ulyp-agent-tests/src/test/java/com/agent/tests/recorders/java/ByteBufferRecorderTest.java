package com.agent.tests.recorders.java;

import com.agent.tests.util.AbstractInstrumentationTest;
import com.agent.tests.util.ForkProcessBuilder;
import com.ulyp.core.recorders.basic.ByteBufferRecord;
import com.ulyp.storage.tree.CallRecord;
import org.junit.jupiter.api.Test;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ByteBufferRecorderTest extends AbstractInstrumentationTest {

    @Test
    void shouldRecordFileObject() {
        CallRecord root = runSubprocessAndReadFile(
                new ForkProcessBuilder()
                        .withMain(TestCase.class)
                        .withMethodToRecord("returnBuf")
        );

        ByteBufferRecord value = (ByteBufferRecord) root.getReturnValue();

        assertEquals(1024, value.getCapacity());
        assertEquals(512, value.getLimit());
        assertEquals(512, value.getRemaining());
        assertEquals(0, value.getPosition());
    }

    public static class TestCase {

        public static void main(String[] args) {
            System.out.println(returnBuf());
        }

        public static Buffer returnBuf() {
            Buffer byteBuffer = ByteBuffer.allocateDirect(1024);
            byteBuffer = byteBuffer.limit(512);
            return byteBuffer;
        }
    }
}
