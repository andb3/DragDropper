package com.andb.apps.dragdropper

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragDropper(val adapter: RecyclerView.Adapter<*>?) : ItemTouchHelper.Callback() {

    var onMoved: (rv: RecyclerView, vh: RecyclerView.ViewHolder) -> Unit = { rv, vh -> }

    private var currentOldPosition = -1

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeFlag(
            ItemTouchHelper.ACTION_STATE_DRAG,
            makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
        )
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        if (currentOldPosition == -1) {
            currentOldPosition = viewHolder.adapterPosition
        }

        adapter?.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, startDY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        var dY = startDY
        val topY = viewHolder.itemView.top + dY
        val bottomY = topY + viewHolder.itemView.height
        if (topY < 0) {
            dY = 0f
        } else if (bottomY > recyclerView.height) {
            dY = (recyclerView.height - viewHolder.itemView.height - viewHolder.itemView.top).toFloat()
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        onMoved.invoke(recyclerView, viewHolder)
    }

    fun onMoved(block: (oldPos: Int, newPos: Int) -> Unit) {
        onMoved = { rv, vh ->
            val oldPos = if (currentOldPosition > -1) currentOldPosition else vh.adapterPosition
            block.invoke(oldPos, vh.adapterPosition)
            currentOldPosition = -1
        }
    }

}