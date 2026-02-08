package com.ulyp.ui.export

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.ulyp.storage.tree.Recording
import java.io.File

object RecordingMultipleJsonExporter {

    // Single ObjectMapper instance; pretty printing enabled for readability in files.
    private val mapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

    /**
     * Export one Recording to a JSON file on disk.
     *
     * @param recording Parsed Recording object (NOT raw bytes)
     * @param outFile   Destination JSON file path
     */
    fun export(recording: Recording, outFile: File) {
        val dto = RecordingJsonConverter.toRecordingJson(recording)
        outFile.parentFile?.mkdirs()
        mapper.writeValue(outFile, dto)
    }

}
