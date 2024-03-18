package com.seven.colink.ui.promotion.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.databinding.ItemPostMemberInfoBinding
import com.seven.colink.databinding.ItemProductImgBinding
import com.seven.colink.databinding.ItemProductLinkBinding
import com.seven.colink.databinding.ItemProductMemberHeaderBinding
import com.seven.colink.databinding.ItemProductProjectHeaderBinding
import com.seven.colink.databinding.ItemProductTitleBinding
import com.seven.colink.ui.promotion.ProductPromotionItems
import com.seven.colink.util.setLevelIcon

class ProductPromotionViewAdapter : ListAdapter<ProductPromotionItems, RecyclerView.ViewHolder>(
    ProductPromotionDiffUtil
) {
    object ProductPromotionDiffUtil : DiffUtil.ItemCallback<ProductPromotionItems>() {
        override fun areItemsTheSame(
            oldItem: ProductPromotionItems, newItem: ProductPromotionItems
        ): Boolean {
            return when {
                oldItem is ProductPromotionItems.Img && newItem is ProductPromotionItems.Img -> {
                    oldItem == newItem
                }

                oldItem is ProductPromotionItems.Title && newItem is ProductPromotionItems.Title -> {
                    oldItem == newItem
                }

                oldItem is ProductPromotionItems.ProjectLeaderItem && newItem is ProductPromotionItems.ProjectLeaderItem -> {
                    oldItem.userInfo?.onSuccess { it?.uid } == newItem.userInfo?.onSuccess { it?.uid }
                }

                oldItem is ProductPromotionItems.MiddleImg && newItem is ProductPromotionItems.MiddleImg -> {
                    oldItem.img == newItem.img
                }

                oldItem is ProductPromotionItems.Link && newItem is ProductPromotionItems.Link -> {
                    oldItem == newItem
                }

                oldItem is ProductPromotionItems.ProjectMember && newItem is ProductPromotionItems.ProjectMember -> {
                    oldItem.userInfo == newItem.userInfo
                }

                else -> {
                    oldItem == newItem
                }
            }
        }

        override fun areContentsTheSame(
            oldItem: ProductPromotionItems, newItem: ProductPromotionItems
        ): Boolean {
            return oldItem == newItem
        }
    }

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick : ItemClick? = null

    private val FIRST_TYPE = 0
    private val SECOND_TYPE = 1
    private val THIRD_TYPE = 2
    private val FORTH_TYPE = 3
    private val FIFTH_TYPE = 4
    private val SIXTH_TYPE = 5
    private val SEVENTH_TYPE = 6
    private val EIGHTH_TYPE = 7
    private val NINETH_TYPE = 8

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            FIRST_TYPE -> {
                val first = ItemProductImgBinding.inflate(inflater,parent,false)
                FirstViewHolder(first)
            }
            SECOND_TYPE -> {
                val second = ItemProductTitleBinding.inflate(inflater,parent,false)
                SecondViewHolder(second)
            }
            THIRD_TYPE -> {
                val third = ItemProductImgBinding.inflate(inflater,parent,false)
                ThirdViewHolder(third)
            }
            FORTH_TYPE -> {
                val forth = ItemProductLinkBinding.inflate(inflater,parent,false)
                ForthViewHolder(forth)
            }
            FIFTH_TYPE -> {
                val fifth = ItemProductProjectHeaderBinding.inflate(inflater,parent,false)
                FifthViewHolder(fifth)
            }
            SIXTH_TYPE -> {
                val sixth = ItemProductMemberHeaderBinding.inflate(inflater,parent,false)
                SixthViewHolder(sixth)
            }
            SEVENTH_TYPE -> {
                val seventh = ItemPostMemberInfoBinding.inflate(inflater,parent,false)
                SeventhViewHolder(seventh)
            }
            EIGHTH_TYPE -> {
                val eighth = ItemProductMemberHeaderBinding.inflate(inflater,parent,false)
                EighthViewHolder(eighth)
            }
            else -> {
                val nineth = ItemPostMemberInfoBinding.inflate(inflater,parent,false)
                NinethViewHolder(nineth)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        if (item is ProductPromotionItems.Img) {
            holder as FirstViewHolder
            holder.img.load(item.img)
        }

        if (item is ProductPromotionItems.Title) {
            holder as SecondViewHolder
            with(holder) {
                viewDes.visibility = View.VISIBLE
                viewTitle.visibility = View.VISIBLE
                editDes.visibility = View.GONE
                editTitle.visibility = View.GONE

                date.text = item.date
                viewDes.text = item.des
                viewTitle.text = item.title
            }
        }

        if (item is ProductPromotionItems.MiddleImg) {
            holder as ThirdViewHolder
            holder.img.load(item.img)
        }

        if (item is ProductPromotionItems.Link) {
            holder as ForthViewHolder
            with(holder) {
                viewLink.visibility = View.VISIBLE

                viewWebLink.text = item.webLink

                if (item.aosLink?.isNotEmpty() == true) {
                    viewPlayStore.visibility = View.VISIBLE
                }else {
                    viewPlayStore.visibility = View.INVISIBLE
                }

                if (item.iosLink?.isNotEmpty() == true) {
                    viewAppStore.visibility = View.VISIBLE
                }else {
                    viewAppStore.visibility = View.INVISIBLE
                }

            }
        }

        if (item is ProductPromotionItems.ProjectHeader) {
            holder as FifthViewHolder
            holder.header.text = "프로젝트 멤버"
        }

        if (item is ProductPromotionItems.ProjectLeaderHeader) {
            holder as SixthViewHolder
            holder.header.text = "리더"
        }

        if (item is ProductPromotionItems.ProjectLeaderItem) {
            holder as SeventhViewHolder
            val items = item.userInfo
            with(holder) {
                img.load(items?.onSuccess { it?.photoUrl })
                name.text = items?.onSuccess { it?.name }?.toString()
                intro.text = items?.onSuccess { it?.info }?.toString()
                grade.text = items?.onSuccess { it?.grade }?.toString()
                level.text = items?.onSuccess { it?.level }?.toString()
                items?.onSuccess { it?.level?.let { color -> levelColor.setLevelIcon(color) } }
//                img.load(item.userInfo.photoUrl)
//                name.text = item.userInfo.name
//                intro.text = item.userInfo.info
//                grade.text = item.userInfo.grade?.toString()
//                level.text = item.userInfo.level?.toString()
//                item.userInfo.level?.let { levelColor.setLevelIcon(it) }
            }
        }

        if (item is ProductPromotionItems.ProjectMemberHeader) {
            holder as EighthViewHolder
            holder.header.text = "멤버"
        }

        if (item is ProductPromotionItems.ProjectMember) {
            holder as NinethViewHolder
            val items = item.userInfo
            with(holder) {
                items?.forEach { member ->
                    img.load(member?.onSuccess { it?.photoUrl })
                    name.text = member?.onSuccess { it?.name }.toString()
                    intro.text = member?.onSuccess { it?.info }.toString()
                    grade.text = member?.onSuccess { it?.grade }.toString()
                    level.text = member?.onSuccess { it?.level }.toString()
                    member?.onSuccess {
                        it?.level?.let { color ->
                            levelColor.setLevelIcon(color)
                        }
                    }
                }
            }

//            with(holder) {
//                item.userInfo.forEach {
//                    img.load(it.photoUrl)
//                    name.text = it.name
//                    intro.text = it.info
//                    grade.text = it.grade?.toString()
//                    level.text = it.level?.toString()
//                    it.level?.let { color ->
//                        levelColor.setLevelIcon(color) }
//                }
//            }
        }
    }

    override fun getItemCount(): Int = currentList.size

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is ProductPromotionItems.Img -> FIRST_TYPE
            is ProductPromotionItems.Title -> SECOND_TYPE
            is ProductPromotionItems.MiddleImg -> THIRD_TYPE
            is ProductPromotionItems.Link -> FORTH_TYPE
            is ProductPromotionItems.ProjectHeader -> FIFTH_TYPE
            is ProductPromotionItems.ProjectLeaderHeader -> SIXTH_TYPE
            is ProductPromotionItems.ProjectLeaderItem -> SEVENTH_TYPE
            is ProductPromotionItems.ProjectMemberHeader -> EIGHTH_TYPE
            is ProductPromotionItems.ProjectMember -> NINETH_TYPE
        }
    }

    inner class FirstViewHolder(binding: ItemProductImgBinding) : RecyclerView.ViewHolder(binding.root) {
        val img = binding.ivProductPromotion
    }

    inner class SecondViewHolder(binding : ItemProductTitleBinding) : RecyclerView.ViewHolder(binding.root) {
        val editTitle = binding.etProductTitle
        val date = binding.tvProductDate
        val editDes = binding.etProductDes
        val viewTitle = binding.tvProductTitle
        val viewDes = binding.tvProductDes

    }

    inner class ThirdViewHolder(binding: ItemProductImgBinding) : RecyclerView.ViewHolder(binding.root) {
        val img = binding.ivProductPromotion
    }

    inner class ForthViewHolder(binding : ItemProductLinkBinding) : RecyclerView.ViewHolder(binding.root) {
        val viewLink = binding.layProductViewLink
        val viewPlayStore = binding.ivPlaystore
        val viewAppStore = binding.ivAppstore
        val viewWebLink = binding.tvProductWebLink
        val etWebLink = binding.etProductWebLink
        val etPlayStore = binding.etProductPlaystoreLink
        val etAppStore = binding.etProductAppstoreLink
    }

    inner class FifthViewHolder(binding: ItemProductProjectHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        val header = binding.tvProductPromotionHeader
    }

    inner class SixthViewHolder(binding: ItemProductMemberHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        val header = binding.tvProductPromotionLeaderHeader
    }

    inner class SeventhViewHolder(binding: ItemPostMemberInfoBinding) : RecyclerView.ViewHolder(binding.root) {
        val img = binding.ivUser
        val name = binding.tvUserName
        val intro = binding.tvUserIntroduction
        val grade = binding.tvUserGrade
        val levelColor = binding.ivLevelDiaIcon
        val level = binding.tvLevelDiaIcon
    }

    inner class EighthViewHolder(binding : ItemProductMemberHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        val header = binding.tvProductPromotionLeaderHeader
    }

    inner class NinethViewHolder(binding : ItemPostMemberInfoBinding) : RecyclerView.ViewHolder(binding.root) {
        val img = binding.ivUser
        val name = binding.tvUserName
        val intro = binding.tvUserIntroduction
        val grade = binding.tvUserGrade
        val levelColor = binding.ivLevelDiaIcon
        val level = binding.tvLevelDiaIcon
    }
}