package com.ulyp.core.serializers;

import com.ulyp.core.ExitMethodCall;
import com.ulyp.core.Type;
import com.ulyp.core.TypeResolver;
import com.ulyp.core.bytes.BytesIn;
import com.ulyp.core.bytes.BytesOut;
import com.ulyp.core.recorders.ObjectRecord;
import com.ulyp.core.recorders.ObjectRecorder;
import com.ulyp.core.recorders.ObjectRecorderRegistry;
import com.ulyp.core.recorders.RecorderChooser;
import com.ulyp.core.repository.ReadableRepository;

import java.util.Optional;

public class ExitMethodCallSerializer {

    public static final ExitMethodCallSerializer instance = new ExitMethodCallSerializer();

    public static final byte EXIT_METHOD_CALL_ID = 2;

    public void serializeExitMethodCall(BytesOut out, int callId, TypeResolver typeResolver, boolean thrown, Object returnValue, long nanoTime) {
        out.write(EXIT_METHOD_CALL_ID);
        out.writeVarInt(callId);
        out.write(thrown);
        out.write(nanoTime);

        Type type = typeResolver.get(returnValue);
        out.writeVarInt(type.getId());

        ObjectRecorder recorderHint = type.getRecorderHint();
        if (returnValue != null && recorderHint == null) {
            recorderHint = RecorderChooser.getInstance().chooseForType(returnValue.getClass());
            type.setRecorderHint(recorderHint);
        }

        ObjectRecorder recorder = returnValue != null ?
                (thrown ? ObjectRecorderRegistry.THROWABLE_RECORDER.getInstance() : recorderHint) :
                ObjectRecorderRegistry.NULL_RECORDER.getInstance();

        out.write(recorder.getId());

        try {
            recorder.write(returnValue, out, typeResolver);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ObjectRecord deserializeObject(BytesIn input, ReadableRepository<Integer, Type> typeResolver) {
        int typeId = input.readVarInt();
        byte recorderId = input.readByte();
        Type type = Optional.ofNullable(typeResolver.get(typeId)).orElse(Type.unknown());
        ObjectRecorder objectRecorder = ObjectRecorderRegistry.recorderForId(recorderId);
        return objectRecorder.read(
                type,
                input,
                id -> Optional.ofNullable(typeResolver.get(id)).orElse(Type.unknown())
        );
    }

    public static ExitMethodCall deserialize(BytesIn input, ReadableRepository<Integer, Type> typeResolver) {

        int callId = input.readVarInt();
        boolean thrown = input.readBoolean();
        long nanoTime = input.readLong();

        return ExitMethodCall.builder()
                .returnValue(deserializeObject(input, typeResolver))
                .thrown(thrown)
                .callId(callId)
                .nanoTime(nanoTime)
                .build();
    }
}
