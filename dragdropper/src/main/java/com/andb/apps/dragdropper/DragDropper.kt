package com.andb.apps.dragdropper

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragDropper() : ItemTouchHelper.Callback() {

    var elevateBy = dpToPx(8)
    var onDropped: (rv: RecyclerView, vh: RecyclerView.ViewHolder) -> Unit = { rv, vh -> }
    var excludeIf: (vh: RecyclerView.ViewHolder) -> Unit = { vh -> }
    var dragDirection: Int = DIRECTION_VERTICAL

    /**Function to find Pair representing first and last (inclusive) positions that a ViewHolder can move into**/
    private var constrainBy: (recycler: RecyclerView, vh: RecyclerView.ViewHolder) -> Pair<Int, Int> = { recycler, vh ->
        val size = recycler.adapter?.itemCount ?: 0
        Pair(0, size - 1)
    }

    //Original position of viewholder being dragged or -1 if none
    private var currentOriginalPosition = -1

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val directions = when (dragDirection) {
            DIRECTION_VERTICAL -> ItemTouchHelper.UP or ItemTouchHelper.DOWN
            DIRECTION_HORIZONTAL -> ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            else -> ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        }
        return makeFlag(
            ItemTouchHelper.ACTION_STATE_DRAG,
            makeMovementFlags(directions, 0)
        )
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        //Log.d("dragDropper", "onMove")
        if (currentOriginalPosition == -1) {
            currentOriginalPosition = viewHolder.adapterPosition
        }

        val constraints = constrainBy.invoke(recyclerView, viewHolder)
        if (target.adapterPosition in constraints.first..constraints.second) {
            recyclerView.adapter?.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
        }
        return true
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, startDX: Float, startDY: Float, actionState: Int, isCurrentlyActive: Boolean) {

        var dY = startDY
        var dX = startDX
        val topY = viewHolder.itemView.top + dY
        val bottomY = topY + viewHolder.itemView.height
        val leftX = viewHolder.itemView.left + dX
        val rightX = leftX + viewHolder.itemView.width
        val constraints = constrainBy.invoke(recyclerView, viewHolder)
        //Log.d("dragDropper", "onChildDraw - dY: $dY")
        //ViewHolder in top position that dragged VH can move into
        val topHolder = recyclerView.findViewHolderForAdapterPosition(constraints.first)
        //ViewHolder in bottom position that dragged VH can move into
        val bottomHolder = recyclerView.findViewHolderForAdapterPosition(constraints.second)

        val constraintTop = topHolder?.itemView?.top
        val constraintBottom = bottomHolder?.itemView?.bottom


        if (constraintTop != null && topY < constraintTop) {
            dY = (constraintTop - viewHolder.itemView.top).toFloat()
        } else if (constraintBottom != null && bottomY > constraintBottom) {
            dY = (constraintBottom - (viewHolder.itemView.bottom)).toFloat()
        }

        if (leftX < 0) {
            dX = 0f
        } else if (rightX > recyclerView.width) {
            dX = (recyclerView.width - viewHolder.itemView.width - viewHolder.itemView.left).toFloat()
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        onDropped.invoke(recyclerView, viewHolder)
        currentOriginalPosition = -1
    }

    fun onDropped(block: (oldPos: Int, newPos: Int) -> Unit) {
        onDropped = { rv, vh ->
            val oldPos = if (currentOriginalPosition > -1) currentOriginalPosition else vh.adapterPosition
            block.invoke(oldPos, vh.adapterPosition)
        }
    }

    fun constrainBy(block: (vh: RecyclerView.ViewHolder) -> Pair<Int, Int>) {
        constrainBy = { recycler, vh ->
            block.invoke(vh)
        }
    }

    companion object {
        const val DIRECTION_VERTICAL = 0
        const val DIRECTION_HORIZONTAL = 1
        const val DIRECTION_BOTH = 2
    }

}