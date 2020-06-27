package com.movieplayer.android.ui.movie_details

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.movieplayer.android.R
import com.movieplayer.android.adapters.RecyclerTouchListener
import com.movieplayer.android.adapters.TrailersAdapter
import com.movieplayer.android.base.MvpBaseActivity
import com.movieplayer.android.data.network.APIs
import com.movieplayer.android.data.network.api_response.MovieDetailsResponse
import com.movieplayer.android.data.network.api_response.MovieTrailerResponse
import com.movieplayer.android.file.PicassoImageLoader
import com.movieplayer.android.ui.youtube.MyYoutubeVideoPlayerActivity
import com.movieplayer.android.utils.Constants.IntentKeys
import com.movieplayer.android.utils.DateUtils
import com.movieplayer.android.utils.Navigator
import kotlinx.android.synthetic.main.activity_movie_details.*

class MovieDetailsActivity : MvpBaseActivity<MovieDetailsPresenter>(), MovieDetailsContract.View {


    private lateinit var trailersAdapter: TrailersAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var itemDecoration: DividerItemDecoration


    override fun getContentView(): Int {
        return R.layout.activity_movie_details
    }

    override fun onViewReady(savedInstanceState: Bundle?, intent: Intent) {
        supportActionBar?.title = "MovieDetail"
        setUpAdapter()
        val bundle = intent.getBundleExtra(IntentKeys.DATA_BUNDLE)
        if (bundle != null) {
            val movieId = bundle.getString(IntentKeys.MOVIE_ID)
            val apiKey = getString(R.string.the_moviedb_api_key)
            mPresenter.getMovieDetails(movieId, apiKey)
            mPresenter.getMovieTrailers(movieId, apiKey)
        }
    }

    private fun setUpAdapter() {

        trailersAdapter = TrailersAdapter(getContext())
        linearLayoutManager = LinearLayoutManager(getContext())
        itemDecoration = DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL)

        recyclerView.apply {
            adapter = trailersAdapter
            layoutManager = linearLayoutManager
            addItemDecoration(itemDecoration)
        }

        recyclerView.addOnItemTouchListener(
            RecyclerTouchListener(
                getContext(),
                recyclerView,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        // clicked item
                        val tappedTrailer = trailersAdapter.getItem(position) as MovieTrailerResponse.SingleTrailer
                        val bundle = Bundle()
                        bundle.putString(IntentKeys.TRAILER_KEY, tappedTrailer.key)
                        Navigator.sharedInstance.navigateWithBundle(
                            getContext(),
                            MyYoutubeVideoPlayerActivity::class.java,
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


    override fun movieDetailsDidReceived(response: MovieDetailsResponse) {
        val posterPath = "${APIs.THUMB_BASE}${response.posterPath}"
        val runtime = "${response.runtime}min"
        val rating = "${response.voteAverage}/10"
        val releaseYear = DateUtils.shared.formateDate("yyyy-MM-dd", response.releaseDate, "yyyy")

        tvMovieName.text = response.originalTitle
        PicassoImageLoader.sharedInstance.loadImage(getContext(), posterPath, imgMoviePoster)
        tvReleaseYear.text = releaseYear
        tvRuntime.text = runtime
        tvRating.text = rating
        tvMovieDetails.text = response.overview
    }

    override fun movieTrailersDidReceived(response: MovieTrailerResponse) {
        trailersAdapter.setData(response.results)
    }
}
