package com.ulyp.ui.util

import com.ulyp.core.EnterMethodCall
import com.ulyp.core.ExitMethodCall
import com.ulyp.storage.search.SearchResultListener
import com.ulyp.ui.elements.recording.tree.FileRecordingsTab
import javafx.application.Platform

class SearchListener(private val fileRecordingsTab: FileRecordingsTab) : SearchResultListener {

    private val map: MutableSet<Int> = mutableSetOf()

    override fun onStart() {
        fileRecordingsTab.recordingList.clearHighlights();
    }

    override fun onMatch(recordingId: Int, enterMethodCall: EnterMethodCall) {
        if (map.add(recordingId)) {
            Platform.runLater {
                fileRecordingsTab.recordingList.highlight(recordingId)
            }
        }
    }

    override fun onMatch(recordingId: Int, exitMethodCall: ExitMethodCall) {
        if (map.add(recordingId)) {
            Platform.runLater {
                fileRecordingsTab.recordingList.highlight(recordingId)
            }
        }
    }

    override fun onEnd() {

    }
}