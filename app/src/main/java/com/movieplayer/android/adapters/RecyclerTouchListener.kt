package com.movieplayer.android.adapters

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class RecyclerTouchListener(context: Context, recyclerView: androidx.recyclerview.widget.RecyclerView, private val clickListener: ClickListener?) : androidx.recyclerview.widget.RecyclerView.OnItemTouchListener {
    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val child = recyclerView.findChildViewUnder(e.x, e.y)
                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child, recyclerView.getChildPosition(child))
                }
            }
        })
    }

    override fun onInterceptTouchEvent(rv: androidx.recyclerview.widget.RecyclerView, e: MotionEvent): Boolean {

        val child = rv.findChildViewUnder(e.x, e.y)
        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onClick(child, rv.getChildPosition(child))
        }
        return false
    }

    override fun onTouchEvent(rv: androidx.recyclerview.widget.RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }

    interface ClickListener {

        fun onClick(view: View, position: Int)

        fun onLongClick(view: View?, position: Int)
    }
}