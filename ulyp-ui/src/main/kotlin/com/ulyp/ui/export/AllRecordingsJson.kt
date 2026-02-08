package com.ulyp.ui.export

/**
 * The "all recordings in one file" JSON.
 * - count: how many recordings we exported
 * - recordings: left-pane summary + right-pane tree per recording
 */
data class AllRecordingsJson(
    val count: Int,
    val recordings: List<RecordingJson>
)
