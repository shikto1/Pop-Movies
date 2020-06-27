package com.movieplayer.android.adapters

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationScrollListener protected constructor(private val layoutManager: GridLayoutManager) :
    androidx.recyclerview.widget.RecyclerView.OnScrollListener() {

    abstract val isLoading: Boolean

    override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (dx == 0 && dy == 0)
            return

        onScrolled()
        hideFab()
        if (dy > 0) onScrolledDown() else if (dy < 0) onScrolledUp()

        val totalItemCount = layoutManager.itemCount
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        if (lastVisibleItemPosition == totalItemCount - 1) {
            if (!isLoading && hasNextPage()) {
                loadMoreItems()
            }
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            showFab()
        }
        super.onScrollStateChanged(recyclerView, newState)
    }

    protected abstract fun loadMoreItems()

    protected abstract fun hasNextPage(): Boolean

    open fun onScrolled() {}
    open fun onScrolledUp() {}
    open fun onScrolledDown() {}

    // For hiding floating action button while scrolling...
    open fun showFab() {}

    open fun hideFab() {}
}