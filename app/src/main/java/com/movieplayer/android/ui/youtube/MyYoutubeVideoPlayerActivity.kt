package com.movieplayer.android.ui.youtube

import android.os.Bundle
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.movieplayer.android.R
import com.movieplayer.android.utils.Constants.IntentKeys
import kotlinx.android.synthetic.main.activity_my_youtube_video_player.*

class MyYoutubeVideoPlayerActivity : YouTubeBaseActivity() {

    private var trailerKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_youtube_video_player)

        val bundle = intent.getBundleExtra(IntentKeys.DATA_BUNDLE)
        if (bundle != null) {
            trailerKey = bundle.getString(IntentKeys.TRAILER_KEY)
        }
        myYouTubePlayerView.initialize("AIzaSyC-4NmMpMOQaWE_6b6feFmQiUc_aGAcpkQ", myYouTubePlayerInitListener)
    }


    private var myYouTubePlayerInitListener = object : YouTubePlayer.OnInitializedListener {
        override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, player: YouTubePlayer?, p2: Boolean) {
            player?.loadVideo(trailerKey)
        }

        override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {

        }
    }
}
