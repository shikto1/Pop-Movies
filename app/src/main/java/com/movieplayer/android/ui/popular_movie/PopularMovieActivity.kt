package com.movieplayer.android.ui.popular_movie

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.movieplayer.android.R
import com.movieplayer.android.adapters.PaginationScrollListener
import com.movieplayer.android.adapters.PopularMovieAdapter
import com.movieplayer.android.adapters.RecyclerTouchListener
import com.movieplayer.android.base.MvpBaseActivity
import com.movieplayer.android.data.network.api_response.PopularMovieResponse
import com.movieplayer.android.ui.movie_details.MovieDetailsActivity
import com.movieplayer.android.utils.Constants.IntentKeys
import com.movieplayer.android.utils.Navigator
import com.movieplayer.android.utils.showToast
import kotlinx.android.synthetic.main.activity_popular_movie.*


class PopularMovieActivity : MvpBaseActivity<PopularMoviePresenter>(), PopularMovieContract.View {

    private lateinit var popularMovieAdapter: PopularMovieAdapter
    private lateinit var gridLayoutManager: GridLayoutManager

    private var currentPage: Int = 1
    private var totalPages: Int = -1 // This is a dummy value
    private var isLoadingAdded = false

    override fun getContentView(): Int {
        return R.layout.activity_popular_movie
    }

    override fun onViewReady(savedInstanceState: Bundle?, intent: Intent) {
        supportActionBar?.title = "Pop Movies"
        setUpAdapter()
        mPresenter.getPopularMovies(getString(R.string.the_moviedb_api_key), currentPage)
    }

    private fun setUpAdapter() {
        popularMovieAdapter = PopularMovieAdapter(getContext())
        popularMovieAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        gridLayoutManager = if (currentScreenOrientation() == Configuration.ORIENTATION_PORTRAIT) GridLayoutManager(getContext(), 2) else GridLayoutManager(getContext(), 3)

        recyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = popularMovieAdapter
        }


        // Spanning row for loading footer of Grid Layout Manager..............
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (popularMovieAdapter.getItemViewType(position)) {
                    popularMovieAdapter.VIEW_TYPE_FOOTER -> gridLayoutManager.spanCount
                    popularMovieAdapter.VIEW_TYPE_ITEM -> 1
                    else -> -1
                }
            }
        }


        recyclerView.addOnScrollListener(object : PaginationScrollListener(gridLayoutManager) {
            override val isLoading: Boolean
                get() = isLoadingAdded

            override fun loadMoreItems() {
                isLoadingAdded = true
                popularMovieAdapter.addLoadingFooter()
                mPresenter.getPopularMovies(getString(R.string.the_moviedb_api_key), ++currentPage)
            }

            override fun hasNextPage(): Boolean {
                return currentPage < totalPages
            }

        })

        // Passing movie id to movie details on click movie item
        recyclerView.addOnItemTouchListener(
            RecyclerTouchListener(
                getContext(),
                recyclerView,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        // clicked item
                        val tappedMovie = popularMovieAdapter.getItem(position) as PopularMovieResponse.SingleMovie
                        val bundle = Bundle()
                        bundle.putString(IntentKeys.MOVIE_ID, tappedMovie.id.toString())

                        Navigator.sharedInstance.navigateWithBundle(
                            getContext(),
                            MovieDetailsActivity::class.java,
                            IntentKeys.DATA_BUNDLE,
                            bundle
                        )
                    }

                    override fun onLongClick(view: View?, position: Int) {
                    }

                }
            )
        )
    }

    override fun onNetworkCallStarted(loadingMessage: String) {
        if (!isLoadingAdded) super.onNetworkCallStarted(loadingMessage)
    }

    override fun moviesDidReceived(response: PopularMovieResponse) {
        currentPage = response.page
        totalPages = response.totalPages
        if (isLoadingAdded) {
            isLoadingAdded = false
            popularMovieAdapter.removeLoadingFooter()
            popularMovieAdapter.addAll(response.movies)
        } else
            popularMovieAdapter.setData(response.movies)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        gridLayoutManager.spanCount = if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3
    }
}
