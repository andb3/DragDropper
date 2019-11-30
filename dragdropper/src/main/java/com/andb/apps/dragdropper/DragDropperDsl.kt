package com.andb.apps.dragdropper

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.dragDropWith(block: DragDropper.() -> Unit) {
    /*if(adapter==null){
        throw Exception("DragDropper must be attached to a RecyclerView with an adapter!")
    }*/
    val callback = DragDropper()
    block.invoke(callback)
    ItemTouchHelper(callback).attachToRecyclerView(this)
}