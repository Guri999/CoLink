package com.seven.colink.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.widget.ViewPager2
import com.seven.colink.R
import com.seven.colink.databinding.ItemHomeHeaderBinding
import com.seven.colink.databinding.ItemHomeTopViewpagerBinding
import kotlin.math.ceil

class HomeMainAdapter : ListAdapter<HomeAdapterItems, ViewHolder>(HomeMainDiffUtil) {
    object HomeMainDiffUtil : DiffUtil.ItemCallback<HomeAdapterItems>() {
        override fun areItemsTheSame(
            oldItem: HomeAdapterItems, newItem: HomeAdapterItems): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: HomeAdapterItems, newItem: HomeAdapterItems): Boolean {
            return oldItem == newItem
        }
    }

    private val TOP_TYPE = 0
    private val HEADER_TYPE = 1
    private var bannerPosition = 0
    private val topAdapter by lazy { TopViewPagerAdapter() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            TOP_TYPE -> {
                val topItem = ItemHomeTopViewpagerBinding.inflate(inflater,parent,false)
                TopViewHolder(topItem)
            }
            else -> {
                val header = ItemHomeHeaderBinding.inflate(inflater,parent,false)
                HeaderViewHolder(header)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]

        if (item is HomeAdapterItems.TopView) {
            with(holder as TopViewHolder) {
                pager.adapter = item.adapter
                sum.text = item.adapter.currentList.size.toString()
            }
        }
        if (item is HomeAdapterItems.Header) {
            holder as HeaderViewHolder
            holder.header.text = item.header
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is HomeAdapterItems.TopView -> TOP_TYPE
            is HomeAdapterItems.Header -> HEADER_TYPE
        }
    }

    inner class TopViewHolder(binding : ItemHomeTopViewpagerBinding) : ViewHolder(binding.root) {
        val pager = binding.vpHomeTop
        var pos = binding.tvHomeTopCurrent
        val sum = binding.tvHomeTopSum
        private val left = binding.btnHomeBack
        private val right = binding.btnHomeFront

        init {
            left.setOnClickListener {
                val current = pager.currentItem
                if (current == 0) {
                    pager.setCurrentItem(6,false)
                }else {
                    pager.setCurrentItem(current-1,false)
                }
            }

            right.setOnClickListener {
                val current = pager.currentItem
                if (current == 6) {
                    pager.setCurrentItem(0,false)
                }else {
                    pager.setCurrentItem(current+1,false)
                }
            }

            pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    pos.text = ((position % TopViewPagerAdapter().itemCount)+1).toString()
                    Log.d("Top","#aaa position = $position")
                    Log.d("Top","#aaa size = ${TopViewPagerAdapter().itemCount}")
                    Log.d("Top","#aaa current = ${topAdapter.currentList.size}")
                }
            })

            bannerPosition = Int.MAX_VALUE / 2 - ceil(currentList.size.toDouble() / 2).toInt()
            pager.setCurrentItem(bannerPosition,false)
        }
    }

    inner class HeaderViewHolder (binding : ItemHomeHeaderBinding) : ViewHolder(binding.root) {
        val header = binding.tvHomeHeader
    }
}