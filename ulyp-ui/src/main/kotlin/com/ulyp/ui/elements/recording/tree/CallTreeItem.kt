package com.ulyp.ui.elements.recording.tree

import com.ulyp.storage.tree.CallRecord
import com.ulyp.storage.tree.Recording
import com.ulyp.ui.RenderSettings
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.control.TreeItem
import java.util.function.Consumer

class CallTreeItem(private val recording: Recording, private val callRecordId: Long, private val renderSettings: RenderSettings) :
        TreeItem<CallNodeContent>(
                CallNodeContent(
                        recording.getCallRecord(callRecordId),
                        renderSettings,
                        recording.callCount(),
                        recording.rootDuration()
                )
        ) {

    private var loaded = false
    private var currentCallRecord: CallRecord = recording.getCallRecord(callRecordId)

    init {
        this.addEventHandler(branchCollapsedEvent(), EventHandler<TreeModificationEvent<CallNodeContent>> {
            val s = it.treeItem as CallTreeItem
            s.unloadChildren()
        })
    }

    fun refresh() {
        currentCallRecord = recording.getCallRecord(callRecordId)
        value = CallNodeContent(currentCallRecord, renderSettings, recording.callCount(), recording.rootDuration())

        if (loaded) {
            val newChildren = currentCallRecord.childrenCallIds
            val currentLoadedChildrenCount = children.size
            if (newChildren.size > currentLoadedChildrenCount) {
                for (i in currentLoadedChildrenCount until newChildren.size) {
                    children.add(CallTreeItem(recording, newChildren.getLong(i), renderSettings))
                }
            }
            children.forEach(Consumer { node: TreeItem<CallNodeContent> -> (node as CallTreeItem).refresh() })
        }
    }

    override fun getChildren(): ObservableList<TreeItem<CallNodeContent>> {
        if (!loaded) {
            loadChildren()
        }
        return super.getChildren()
    }

    override fun isLeaf(): Boolean {
        return if (loaded) {
            children.isEmpty()
        } else {
            currentCallRecord.childrenCallIds.isEmpty()
        }
    }

    private fun loadChildren() {
        val children: MutableList<CallTreeItem> = ArrayList()
        val childrenIds = currentCallRecord.childrenCallIds
        for (i in childrenIds.indices) {
            children.add(CallTreeItem(recording, childrenIds.getLong(i), renderSettings))
        }
        super.getChildren().setAll(children)
        loaded = true
    }

    /**
     * @return
     */
    fun toClipboardText(): String {
        val text = StringBuilder()

        text.append(callRecord.returnValue.toString())
            .append(" ")
            .append(callRecord.method.type.name)
            .append(".")
            .append(callRecord.method.name)
            .append(" (")

        callRecord.args.forEachIndexed { index, arg ->
            text.append(arg.toString())
            if (index != callRecord.args.size - 1) {
                text.append(", ")
            }
        }

        text.append(")")

        return text.toString();
    }

    private fun unloadChildren() {
        super.getChildren().setAll(ArrayList())
        loaded = false
    }

    val callRecord: CallRecord
        get() {
            return currentCallRecord
        }

    override fun toString(): String {
        return "FxCallRecord{" +
                "node=" + callRecord +
                '}'
    }
}