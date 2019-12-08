package com.e15.alarmnats.adapter

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.viewholder.ViewHolderEvent

//Interface ItemTouchHelper.SimpleCallBack
//This is a utility class to (add swipe) to dismiss and (drag & drop) support to RecyclerView.
//It works with a RecyclerView and (a Callback class),
// which configures what (type of interactions) are enabled and also receives events when user performs these actions.
class RecyclerItemTouchHelper : ItemTouchHelper.SimpleCallback{

    var listener:RecyclerItemTouchHelperListener

    constructor(dragDirs: Int, swipeDirs: Int, listener: RecyclerItemTouchHelperListener) : super(dragDirs, swipeDirs) {
        this.listener = listener
    }


    //Called when ItemTouchHelper wants to move the dragged
    // item from (its old position) to the (new position).
    //If this method (returns true), ItemTouchHelper assumes
    // viewHolder has been moved to (the adapter position) of (target ViewHolder)
    override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
    ): Boolean {

        return true
    }

    //Called when the (ViewHolder) (swiped or dragged) by the (ItemTouchHelper) is changed.
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {

        if(viewHolder!=null){

            var foregroundView=(viewHolder as ViewHolderEvent).viewForeground

            ItemTouchHelper.Callback.getDefaultUIUtil().onSelected(foregroundView)

        }

    }

    //If you would like to customize
    // how your View's respond to user interactions, this is a good place to override.
    //Default implementation translates the child by the given dX, dY.
    // (ItemTouchHelper) also takes care of drawing the child
    // after (other children) if it is being dragged.
    // This is done using child (re-ordering mechanism).
    // On platforms prior to L, this is achieved via android.view
    override fun onChildDrawOver(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder?,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
    ) {
        var foregroundView=(viewHolder as ViewHolderEvent).viewForeground

        ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(c, recyclerView,foregroundView,dX,dY, actionState,isCurrentlyActive)

    }

    //Called by the (ItemTouchHelper) when the user interaction
    // with an element is (over) and it also (completed its animation).
    //This is a good place to (clear all changes) on the View
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {

        var foregroundView=(viewHolder as ViewHolderEvent).viewForeground

        ItemTouchHelper.Callback.getDefaultUIUtil().clearView(foregroundView)

    }

    //Called by ItemTouchHelper on RecyclerView's onDraw callback.
    //If you would like to customize how your View's respond to
    // user interactions, this is a good place to override.
    //Default implementation translates the child by the given dX, dY.
    // ItemTouchHelper also takes care of drawing the child after other
    // children if it is being dragged. This is done using child re-ordering mechanism
    override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
    ) {

        var foregroundView=(viewHolder as ViewHolderEvent).viewForeground

        ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(c, recyclerView, foregroundView,dX,dY,actionState,isCurrentlyActive)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    //Called when a ViewHolder is swiped by the user.
    //If you are returning (relative directions) (START , END)
    // from the getMovementFlags(RecyclerView, RecyclerView.ViewHolder) method,
    // this method will also use relative directions. Otherwise, it will use absolute directions.
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        listener.onSwiped(viewHolder, direction,viewHolder.adapterPosition)

    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {

        return super.convertToAbsoluteDirection(flags, layoutDirection)

    }

    //interface is used to implements listens to (onSwiped function)
    interface RecyclerItemTouchHelperListener {

        fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction:Int, position:Int)

    }
}