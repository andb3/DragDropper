package com.andb.apps.dragdropper

import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.viewHolders(): List<RecyclerView.ViewHolder> {
    val list = mutableListOf<RecyclerView.ViewHolder>()
    val size = this.adapter?.itemCount ?: 0
    for (i in 0..size) {
        val vh = findViewHolderForAdapterPosition(i)
        if (vh != null) {
            list.add(vh)
        }
    }
    return list
}

fun dpToPx(dp: Int): Int {
    val scale = Resources.getSystem().displayMetrics.density
    return (dp * scale).toInt()
}