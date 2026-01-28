package com.ulyp.ui.elements.recording.tree

import com.ulyp.storage.tree.CallRecord
import com.ulyp.ui.RenderSettings
import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import java.time.Duration

class CallNodeContent(node: CallRecord, renderSettings: RenderSettings, totalNodeCountInTree: Int, rootDuration: Duration) : StackPane() {

    init {
        alignment = Pos.CENTER_LEFT
        children.addAll(
                CallWeight(renderSettings, node, totalNodeCountInTree, rootDuration),
                CallView(node, renderSettings)
        )
    }
}