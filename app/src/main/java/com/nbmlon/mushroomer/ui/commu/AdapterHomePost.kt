package com.nbmlon.mushroomer.ui.commu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.databinding.ItemCommuHomeIamgeBinding
import com.nbmlon.mushroomer.databinding.ItemCommuHomeTextBinding
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.model.PostDiffCallback


/**
 * 커뮤니티 탭을 처음 클릭했을 떄 표시할 최시글들을 담을 ListAdpater
 * **/
class AdapterHomePost(val cl : PostClickListener) : ListAdapter<Post, RecyclerView.ViewHolder>(PostDiffCallback) {
    inner class TextPostViewHolder(private val itemBinding: ItemCommuHomeTextBinding) : RecyclerView.ViewHolder(itemBinding.root), HomeAdapterHolder {
        override fun bind(pos : Int){
            itemBinding.apply {
                post = getItem(pos)
                executePendingBindings()
                frame.setOnClickListener { cl.openPost(getItem(pos)) }
            }
        }
    }

    inner class PhotoPostViewHolder(private val itemBinding: ItemCommuHomeIamgeBinding) : RecyclerView.ViewHolder(itemBinding.root), HomeAdapterHolder{
        override fun bind(pos : Int){
            itemBinding.apply {
                post = getItem(pos)
                executePendingBindings()
                frame.setOnClickListener { cl.openPost(getItem(pos)) }
            }
        }
    }




    override fun getItemViewType(position: Int): Int {
        return getItem(position).boardType.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == BoardType.PicBoard.ordinal){
            PhotoPostViewHolder( ItemCommuHomeIamgeBinding.inflate(LayoutInflater.from(parent.context),parent,false) )
        } else{
            TextPostViewHolder( ItemCommuHomeTextBinding.inflate(LayoutInflater.from(parent.context),parent,false) )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HomeAdapterHolder).bind(position)
    }
}

fun interface HomeAdapterHolder {
    fun bind(position : Int)
}