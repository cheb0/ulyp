package com.ulyp.ui.elements.recording.objects

import com.ulyp.core.recorders.basic.ByteBufferRecord
import com.ulyp.ui.RenderSettings
import com.ulyp.ui.util.ClassNameUtils
import com.ulyp.ui.util.Style
import com.ulyp.ui.util.StyledText.of
import javafx.scene.Node

class RenderedByteBuffer(record: ByteBufferRecord, renderSettings: RenderSettings) : RenderedObject() {

    init {
        val nodes = mutableListOf<Node>()
        if (renderSettings.showTypes) {
            nodes += of(record.type.name, Style.CALL_TREE_TYPE_NAME)
        } else {
            nodes += of(ClassNameUtils.toSimpleName(record.type.name), Style.CALL_TREE_TYPE_NAME)
        }
        nodes += of("@", Style.CALL_TREE_IDENTITY, Style.SMALLER_TEXT)
        nodes += of(Integer.toHexString(record.identityHashCode), Style.CALL_TREE_IDENTITY, Style.SMALLER_TEXT)
        nodes += of("(", Style.CALL_TREE_COLLECTION_BRACKET)

        nodes += of("pos=", Style.CALL_TREE)
        nodes += of(record.position.toString(), Style.CALL_TREE_NUMBER)
        nodes += of(", lim=", Style.CALL_TREE)
        nodes += of(record.limit.toString(), Style.CALL_TREE_NUMBER)
        nodes += of(", cap=", Style.CALL_TREE)
        nodes += of(record.capacity.toString(), Style.CALL_TREE_NUMBER)
        if (renderSettings.showTypes) {
            nodes += of(", rem=", Style.CALL_TREE)
            nodes += of(record.remaining.toString(), Style.CALL_TREE_NUMBER)
        }

        nodes += of(")", Style.CALL_TREE_COLLECTION_BRACKET)
        children.addAll(nodes)
    }
}