package com.ulyp.ui.export

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.ulyp.core.recorders.ObjectRecord
import com.ulyp.storage.tree.CallRecord
import com.ulyp.storage.tree.Recording
import java.io.File

object RecordingJsonExporter {

    // Single ObjectMapper instance; pretty printing enabled for readability in files.
    private val mapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

    /**
     * Export one Recording to a JSON file on disk.
     *
     * @param recording Parsed Recording object (NOT raw bytes)
     * @param outFile   Destination JSON file path
     */
    fun export(recording: Recording, outFile: File) {
        val dto = convertToRecordingJson(recording)
        outFile.parentFile?.mkdirs()
        mapper.writeValue(outFile, dto)
    }

    /** Builds RecordingJson (left-pane summary + right-pane tree) from a Recording. */
    fun convertToRecordingJson(recording: Recording): RecordingJson {
        // LEFT PANE: all precomputed in your model
        // Thread name / start time come from RecordingMetadata
        val md = recording.metadata
        // Recording identity
        val id: Int = recording.id
        val threadName: String? = md.threadName
        val startEpochMs: Long? = md.recordingStartedMillis
        // Root call duration (Duration) -> millis (matches UI "ms")
        val durationMs: Long = recording.rootDuration().toMillis()
        // Total number of calls in this recording (matches UI count)
        val totalCalls: Int = recording.callCount()


        // RIGHT PANE: walk the CallRecord tree starting from the root

        // Your Recording always has exactly one root call
        val rootCall: CallRecord = recording.root
        // Recursively convert the root CallRecord to a DTO tree
        val rootDto: NodeJson = convertToNodeJson(rootCall)

        // -------- Build the top-level DTO --------
        val recording = RecordingJson(
            id = id,
            threadName = threadName,
            startTimeEpochMs = startEpochMs,
            durationMillis = durationMs,
            totalCalls = totalCalls,
            root = rootDto
        )

        println("Converted recording $id with $totalCalls calls to JSON DTO")
        return recording
    }


    /**
     * RECURSION:
     *  Converts a CallRecord (one node) into NodeJson, then does the same for each child.
     */
    private fun convertToNodeJson(node: CallRecord): NodeJson {
        // Stable node id within the recording session (starts at 0 per your docs)
        val nodeId: Long? = node.id

        // Method owner (class) and method name come from node.getMethod()
        val ownerClass: String? = node.method.type.name
        val methodName: String? = node.method.name

        // Arguments and return value are ObjectRecord; stringifying is enough for the JSON
        val args: List<String> = node.args.map(::renderObjectRecord)
        val returnValue: String? = renderObjectRecordOrNull(node.returnValue)

        // Whether the method threw during this call
        val thrown: Boolean = node.hasThrown()

        // Duration of this call in nanoseconds (per-node timing available in CallRecord)
        val durationNanos: Long = node.nanosDuration

        // Children: CallRecord resolves children lazily via RecordingState using child ids
        val children: List<NodeJson> = node.getChildren().map { child -> convertToNodeJson(child) }

        return NodeJson(
            nodeId = nodeId,
            ownerClass = ownerClass,
            methodName = methodName,
            args = args,
            returnValue = returnValue,
            thrown = thrown,
            durationNanos = durationNanos,
            children = children
        )
    }

    // ---------- Small helpers to render ObjectRecord safely ----------

    private fun renderObjectRecord(obj: ObjectRecord): String {
        // ObjectRecord implementations have sensible toString() for UI display.
        // Using toString() keeps JSON aligned with what the right pane shows.
        return obj.toString()
    }

    private fun renderObjectRecordOrNull(obj: ObjectRecord?): String? {
        // Some return values may be "not recorded"; toString() still works,
        // but we'll keep it nullable in case an implementation returns null.
        return obj?.toString()
    }


}
