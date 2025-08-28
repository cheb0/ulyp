package com.ulyp.ui.export

/**
 * Top-level JSON model for one Recording.
 * Mirrors the left pane summary + the right pane call tree.
 */
data class RecordingJson(
    val id: Int,
    val threadName: String?,
    val startTimeEpochMs: Long?,
    val durationMillis: Long,
    val totalCalls: Int,
    val root: NodeJson             // the right-pane call tree (single root per recording)
)

/**
 * One node in the call tree (right pane).
 * Built by walking CallRecord -> children recursively.
 */
data class NodeJson(
    val nodeId: Long?,             // callRecord.getId() (nullable in case of future changes)
    val ownerClass: String?,       // callRecord.getMethod().getType().getName()
    val methodName: String?,       // callRecord.getMethod().getName()
    val args: List<String>,        // callRecord.getArgs() stringified
    val returnValue: String?,      // callRecord.getReturnValue() stringified
    val thrown: Boolean,           // callRecord.hasThrown()
    val durationNanos: Long,       // callRecord.getNanosDuration()
    val children: List<NodeJson>   // recursively filled with child call records
)
