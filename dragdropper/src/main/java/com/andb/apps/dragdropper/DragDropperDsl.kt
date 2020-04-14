package com.andb.apps.dragdropper

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.dragDropWith(block: DragDropper.() -> Unit): ItemTouchHelper {
    /*if(adapter==null){
        throw Exception("DragDropper must be attached to a RecyclerView with an adapter!")
    }*/
    val callback = DragDropper()
    block.invoke(callback)
    val ith = ItemTouchHelper(callback)
    ith.attachToRecyclerView(this)
    return ith
}