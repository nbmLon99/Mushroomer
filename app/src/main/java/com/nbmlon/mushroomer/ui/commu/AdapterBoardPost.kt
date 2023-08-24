package com.nbmlon.mushroomer.ui.commu

import android.content.ContentProvider
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.ItemPostImageBinding
import com.nbmlon.mushroomer.databinding.ItemPostTextBinding
import com.nbmlon.mushroomer.model.Post


/** CommuBoard에서 표시할 글들 Adapter
 * TODO 페이징 구현
 * **/

class AdapterBoardPost(val boardType: BoardType) : ListAdapter<Post, RecyclerView.ViewHolder>(PostDiffCallback()) {
    inner class TextPostViewHolder(private val itemBinding: ItemPostTextBinding) : RecyclerView.ViewHolder(itemBinding.root), HomeAdapterHolder {
        override fun bind(pos : Int){
            itemBinding.apply {
                post = getItem(pos)
                executePendingBindings()
            }
        }
    }

    inner class PhotoPostViewHolder(private val itemBinding: ItemPostImageBinding) : RecyclerView.ViewHolder(itemBinding.root), HomeAdapterHolder{
        override fun bind(pos : Int){
            itemBinding.apply {
                post = getItem(pos)
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return boardType.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == BoardType.PicBoard.ordinal){
            PhotoPostViewHolder( ItemPostImageBinding.inflate(LayoutInflater.from(parent.context)) )
        } else{
            TextPostViewHolder( ItemPostTextBinding.inflate(LayoutInflater.from(parent.context)) )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HomeAdapterHolder).bind(position)
    }
}