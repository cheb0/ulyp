package com.ulyp.storage.reader;

import com.ulyp.core.ProcessMetadata;
import com.ulyp.core.EnterMethodCall;
import com.ulyp.core.ExitMethodCall;
import com.ulyp.core.Type;
import com.ulyp.core.repository.ReadableRepository;
import com.ulyp.storage.StorageException;

import java.util.concurrent.CompletableFuture;

/**
 * The entry point to all data recorded by the agent
 */
public interface RecordingDataReader extends AutoCloseable {

    CompletableFuture<Void> submitReaderJob(RecordingDataReaderJob job);

    EnterMethodCall readEnterMethodCall(long address, ReadableRepository<Integer, Type> typeRepository);

    ExitMethodCall readExitMethodCall(long address, ReadableRepository<Integer, Type> typeRepository);

    ProcessMetadata getProcessMetadata();

    long bytesAvailable();

    void close() throws StorageException;
}
