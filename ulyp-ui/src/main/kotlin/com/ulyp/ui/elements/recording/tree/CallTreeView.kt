package com.ulyp.ui.elements.recording.tree

import com.ulyp.core.ProcessMetadata
import com.ulyp.ui.code.SourceCode
import com.ulyp.ui.code.SourceCodeView
import com.ulyp.ui.code.find.SourceCodeFinder
import com.ulyp.ui.settings.Settings
import com.ulyp.ui.util.Style
import javafx.application.Platform
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

class CallTreeView(
    recording: CallTreeItem,
    private val settings: Settings,
    processMetadata: ProcessMetadata,
    private val sourceCodeView: SourceCodeView) : TreeView<CallNodeContent>(recording) {

    init {
        styleClass += "ulyp-call-tree-view"
        styleClass += Style.ZERO_PADDING.cssClasses

        onKeyPressed = EventHandler { key: KeyEvent ->
            if (key.code == KeyCode.EQUALS) {
                settings.recordingTreeFontSize.value += 1
            }
            if (key.code == KeyCode.MINUS) {
                settings.recordingTreeFontSize.value -= 1
            }
        }

        if (settings.sourceCodeViewerEnabled.get()) {
            val sourceCodeFinder = SourceCodeFinder(processMetadata.classpath)
            selectionModel.selectedItemProperty()
                .addListener { observable: ObservableValue<out TreeItem<CallNodeContent>?>?, oldValue: TreeItem<CallNodeContent>?, newValue: TreeItem<CallNodeContent>? ->
                    val selectedNode = newValue as CallTreeItem?
                    if (selectedNode?.callRecord != null) {
                        val sourceCodeFuture = sourceCodeFinder.find(
                            selectedNode.callRecord.method.type.name
                        )
                        sourceCodeFuture.thenAccept { sourceCode: SourceCode? ->
                            Platform.runLater {
                                val currentlySelected = selectionModel.selectedItem
                                val currentlySelectedNode = currentlySelected as CallTreeItem
                                if (selectedNode.callRecord.id == currentlySelectedNode.callRecord.id) {
                                    sourceCodeView.setText(sourceCode, currentlySelectedNode.callRecord.method.name)
                                }
                            }
                        }
                    }
                }
        }
    }
}