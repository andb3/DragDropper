package com.andb.apps.dragdropper

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.dragDropWith(block: DragDropper.() -> Unit) {
    val callback = DragDropper(adapter)
    block.invoke(callback)
    ItemTouchHelper(callback).attachToRecyclerView(this)
}