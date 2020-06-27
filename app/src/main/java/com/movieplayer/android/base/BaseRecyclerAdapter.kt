package com.movieplayer.android.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.movieplayer.android.R
import com.movieplayer.android.data.prefs.SharedPrefManager

abstract class BaseRecyclerAdapter<D>(val context: Context) : RecyclerView.Adapter<BaseViewHolder>() {

    protected val TAG: String  by lazy {
        this.javaClass.simpleName
    }

    /** View Type */
    public val VIEW_TYPE_ITEM = 1
    public val VIEW_TYPE_FOOTER = 2

    /** Others */
    protected var mPrefs: SharedPrefManager? = SharedPrefManager(context)
    protected var dataList: ArrayList<D?> = ArrayList()
    protected var layoutInflater: LayoutInflater = LayoutInflater.from(context)


    override fun getItemViewType(position: Int): Int {
        return if (dataList[position] == null) VIEW_TYPE_FOOTER else VIEW_TYPE_ITEM
    }

    fun setData(dataList: ArrayList<D?>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    fun add(singleData: D?) {
        dataList.add(singleData)
        notifyItemInserted(dataList.size - 1)
    }

    fun addAll(dataList: ArrayList<D?>) {
        for (singleData in dataList) {
            add(singleData)
        }
    }

    fun getItem(position: Int): D? {
        return dataList[position]
    }

    fun addLoadingFooter() {
        dataList.add(null)
        notifyItemInserted(dataList.size - 1)
    }

    fun removeLoadingFooter() {
        dataList.removeAt(dataList.size - 1)
        notifyItemRemoved(dataList.size)
    }

    fun remove(singleData: D?) {
        if (dataList.contains(singleData)) {
            val position = dataList.indexOf(singleData)
            dataList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun remove(position: Int) {
        if (dataList.isNotEmpty()) {
            dataList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clearAdapter() {
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    override fun getItemCount(): Int {
        return if (dataList.isEmpty()) 0 else dataList.size
    }


    class FooterViewHolder(itemView: View) : BaseViewHolder(itemView) {

        override fun bind(position: Int) {
            // Nothing to do...........
        }
    }

    fun getFooterViewHolder(viewGroup: ViewGroup): BaseViewHolder {
        return FooterViewHolder(layoutInflater.inflate(R.layout.item_view_loading_footer, viewGroup, false))
    }
}