package com.movieplayer.android.data.network.api_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MovieTrailerResponse(

    @Expose
    @SerializedName("id")
    val id: Int,
    @Expose
    @SerializedName("results")
    val results: ArrayList<SingleTrailer?>
){
    data class SingleTrailer(
        @Expose
        @SerializedName("id")
        val id: String,
        @Expose
        @SerializedName("iso_639_1")
        val iso6391: String,
        @Expose
        @SerializedName("iso_3166_1")
        val iso31661: String,
        @Expose
        @SerializedName("key")
        val key: String,
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("site")
        val site: String,
        @Expose
        @SerializedName("size")
        val size: Int,
        @Expose
        @SerializedName("type")
        val type: String
    )
}

