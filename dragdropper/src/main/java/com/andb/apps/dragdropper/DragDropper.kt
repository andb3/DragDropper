package com.andb.apps.dragdropper

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragDropper : ItemTouchHelper.Callback() {

    /** Elevation of the item being dragged. Adds to any elevation the item had originally, and removes after drop.*/
    var elevateBy = 8.dp

    /** Callback when item is dropped **/
    var onDropped: (rv: RecyclerView, vh: RecyclerView.ViewHolder) -> Unit = { rv, vh -> }

    /** Determines whether an item is draggable. For example can exclude headers from being dragged **/
    var canDrag: (vh: RecyclerView.ViewHolder) -> Boolean = { vh -> true }

    /** Sets the directions which an item can be dragged. Can be DIRECTION_VERTICAL (default), DIRECTION_HORIZONTAL, or DIRECTION_BOTH **/
    var dragDirection: Int = DIRECTION_VERTICAL

    /**Sets range (inclusive) of positions that a ViewHolder can move into**/
    private var constrainDrag: (recycler: RecyclerView, vh: RecyclerView.ViewHolder) -> IntRange =
        { recycler, vh ->
            val size = recycler.adapter?.itemCount ?: 0
            0 until size
        }

    //Original position of viewholder being dragged or -1 if none
    private var draggingOriginalPosition = -1
    private lateinit var dragging: RecyclerView.ViewHolder


    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val canDragItem = canDrag.invoke(viewHolder)
        if (!canDragItem) return makeMovementFlags(0, 0)

        val directions = when (dragDirection) {
            DIRECTION_VERTICAL -> ItemTouchHelper.UP or ItemTouchHelper.DOWN
            DIRECTION_HORIZONTAL -> ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            else -> ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        }
        return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, makeMovementFlags(directions, 0))
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        if (draggingOriginalPosition == -1) {
            draggingOriginalPosition = viewHolder.adapterPosition
        }

        val constraints = constrainDrag.invoke(recyclerView, viewHolder)
        if (target.adapterPosition in constraints) {
            recyclerView.adapter?.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
        }
        return true
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        startDX: Float,
        startDY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        var dY = startDY
        var dX = startDX
        val topY = viewHolder.itemView.top + dY
        val bottomY = topY + viewHolder.itemView.height
        val leftX = viewHolder.itemView.left + dX
        val rightX = leftX + viewHolder.itemView.width
        val constraints = constrainDrag.invoke(recyclerView, viewHolder)

        //ViewHolder in top position that dragged VH can move into
        val topHolder = recyclerView.findViewHolderForAdapterPosition(constraints.first)
        //ViewHolder in bottom position that dragged VH can move into
        val bottomHolder = recyclerView.findViewHolderForAdapterPosition(constraints.last)

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

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (viewHolder != null && actionState == ItemTouchHelper.ACTION_STATE_DRAG ) {
            dragging = viewHolder
            viewHolder.animateItemZ(elevateBy.toFloat())
        } else if (viewHolder == null) { //reset z here as clearView has too long of a delay to feel fluid
            dragging.animateItemZ(0f)
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        onDropped.invoke(recyclerView, viewHolder)
        draggingOriginalPosition = -1
    }

    /** Callback when item is dropped **/
    fun onDropped(block: (oldPos: Int, newPos: Int) -> Unit) {
        onDropped = { rv, vh ->
            val oldPos = if (draggingOriginalPosition > -1) draggingOriginalPosition else vh.adapterPosition
            block.invoke(oldPos, vh.adapterPosition)
        }
    }

    /**Set range (inclusive) of positions that a ViewHolder can move into**/
    fun constrainDrag(block: (vh: RecyclerView.ViewHolder) -> IntRange) {
        constrainDrag = { _, vh ->
            block.invoke(vh)
        }
    }

    companion object {
        const val DIRECTION_VERTICAL = 0
        const val DIRECTION_HORIZONTAL = 1
        const val DIRECTION_BOTH = 2
    }

}

private fun RecyclerView.ViewHolder.animateItemZ(elevateBy: Float){
    itemView.animate().setDuration(100).translationZ(elevateBy).start()
}