package com.nbmlon.mushroomer.ui.commu.board

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.databinding.ItemPostImageBinding
import com.nbmlon.mushroomer.databinding.ItemPostTextBinding
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.model.PostDiffCallback
import com.nbmlon.mushroomer.ui.commu.home.HomeAdapterHolder


/**
 * 게시판 상세 화면에서 페이징 데이터를 보여줄 어댑터
 * **/
class AdapterBoardPaging(
    val boardType: BoardType,
    val cl : PostClickListener
) : ListAdapter<Post, RecyclerView.ViewHolder>(PostDiffCallback) {
    inner class TextPostViewHolder(private val itemBinding: ItemPostTextBinding) : RecyclerView.ViewHolder(itemBinding.root),
        HomeAdapterHolder {
        override fun bind(pos : Int){
            itemBinding.apply {
                post = getItem(pos)
                executePendingBindings()
                frame.setOnClickListener {
                    val target = getItem(pos)
                    if (target != null)
                        cl.openPost(target)
                    else
                        Toast.makeText(itemBinding.root.context,"연결을 확인하세요!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    inner class PhotoPostViewHolder(private val itemBinding: ItemPostImageBinding) : RecyclerView.ViewHolder(itemBinding.root),
        HomeAdapterHolder {
        override fun bind(pos : Int){
            itemBinding.apply {
                post = getItem(pos)
                executePendingBindings()
                frame.setOnClickListener {
                    val target = getItem(pos)
                    if (target != null)
                        cl.openPost(target)
                    else
                        Toast.makeText(itemBinding.root.context,"연결을 확인하세요!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return boardType.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == BoardType.PicBoard.ordinal){
            PhotoPostViewHolder( ItemPostImageBinding.inflate(LayoutInflater.from(parent.context),parent,false) )
        } else{
            TextPostViewHolder( ItemPostTextBinding.inflate(LayoutInflater.from(parent.context),parent,false) )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HomeAdapterHolder).bind(position)
    }
}