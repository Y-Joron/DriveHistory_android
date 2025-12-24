package com.joron.waffle.drivehistory.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.joron.waffle.drivehistory.databinding.LibraryListItemBinding
import com.joron.waffle.drivehistory.domain.model.TrackItem

class LibraryListAdapter(
    private val context: Context,
    private var libraryList: List<TrackItem>,
    private var onItemClickListener: ((TrackItem) -> Unit)? = null
) : BaseAdapter() {
    override fun getCount(): Int {
        return libraryList.size
    }

    override fun getItem(index: Int): TrackItem {
        return libraryList[index]
    }

    override fun getItemId(index: Int): Long {
        return index.toLong()
    }

    override fun getView(
        index: Int,
        convertView: View?,
        parent: ViewGroup?,
    ): View? {
        val binding = if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            val tmpBinding = LibraryListItemBinding.inflate(inflater, parent, false)
            tmpBinding.root.tag = tmpBinding
            tmpBinding
        } else {
            convertView.tag as LibraryListItemBinding
        }
        binding.libraryItem = getItem(index)
        binding.executePendingBindings()
        return binding.root.apply {
            setOnClickListener {
                onItemClickListener?.invoke(getItem(index))
            }
        }
    }

    fun updateList(libraryList: List<TrackItem>) {
        this.libraryList = libraryList
        notifyDataSetChanged()
    }
}