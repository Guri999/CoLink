package com.seven.colink.ui.promotion

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentProductPromotionEditBinding
import com.seven.colink.domain.entity.ProductEntity
import com.seven.colink.ui.promotion.adapter.ProductPromotionEditAdapter
import com.seven.colink.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ProductPromotionEditFragment : Fragment() {

    private var _binding : FragmentProductPromotionEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var editAdapter : ProductPromotionEditAdapter
    private lateinit var getResultMainImg : ActivityResultLauncher<Intent>
    private lateinit var getResultDesImg : ActivityResultLauncher<Intent>
    private val editViewModel : ProductPromotionEditViewModel by viewModels()
    private var key : String? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var viewList = mutableListOf(
        ProductPromotionItems.Img(null),
        ProductPromotionItems.Title("","",""),
        ProductPromotionItems.MiddleImg(null),
        ProductPromotionItems.Link("","",""),
        ProductPromotionItems.ProjectHeader("dd"),
        ProductPromotionItems.ProjectLeaderHeader(""),
        ProductPromotionItems.ProjectLeaderItem(null),
        ProductPromotionItems.ProjectMemberHeader(""),
        ProductPromotionItems.ProjectMember(null)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            key = it.getString(Constants.EXTRA_ENTITY_KEY)
            Log.d("Edit","key = $key")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductPromotionEditBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Edit","Edit으로 뜨니?")
        initView()
        clickComplete()
        clickBackButton()
        initLauncher()
    }

    private fun initView() {
        viewList.clear()
        viewList.addAll(listOf(
            ProductPromotionItems.Img(null),
            ProductPromotionItems.Title("","",""),
            ProductPromotionItems.MiddleImg(null),
            ProductPromotionItems.Link("","",""),
            ProductPromotionItems.ProjectHeader(""),
            ProductPromotionItems.ProjectLeaderHeader(""),
            ProductPromotionItems.ProjectLeaderItem(null),
            ProductPromotionItems.ProjectMemberHeader(""),
            ProductPromotionItems.ProjectMember(null)
        ))
        editAdapter = ProductPromotionEditAdapter(binding.rvPromotionEdit,viewList)
        binding.rvPromotionEdit.adapter = editAdapter
        binding.rvPromotionEdit.layoutManager = LinearLayoutManager(requireContext())
        setObserve()
    }

    private fun initLauncher() {
        getResultMainImg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectUri: Uri? = data?.data
                editAdapter.updateImage(selectUri)
            }
        }
        getResultDesImg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectUri: Uri? = data?.data
                editAdapter.updateDesImage(selectUri)
            }
        }
        editAdapter.initResult(getResultMainImg,getResultDesImg)
    }

    private fun setObserve(){
        key?.let { editViewModel.getMemberDetail(it) }
        editViewModel.product.observe(viewLifecycleOwner) {
            key?.let { key -> editViewModel.init(key) }
        }

        editViewModel.setLeader.observe(viewLifecycleOwner) { leader ->
            editAdapter.setLeader(leader)
        }

        editViewModel.setMember.observe(viewLifecycleOwner) { memberItem ->
            editAdapter.setMember(memberItem)
        }
    }

    private fun clickComplete(){
        binding.tvPromotionEditComplete.visibility = View.VISIBLE
        binding.tvPromotionEditComplete.setTextColor(ContextCompat.getColor(requireContext(),R.color.forth_color))
        binding.tvPromotionEditComplete.text = "완료"
        binding.tvPromotionEditComplete.setOnClickListener {
            editAdapter.editTextViewAllFocusOut()
            saveDataToViewModel()
        }
    }

    private fun saveDataToViewModel(){


        coroutineScope.launch {
            val tempData = editAdapter.getTempData()

            val mainImg = withContext(Dispatchers.Main) {
                val imgItem = editAdapter.getTempData()
                imgItem.selectMainImgUri?.let { editViewModel.uploadImage(it) }
            }

            val desImg = withContext(Dispatchers.Main) {
                val imgItem = editAdapter.getTempData()
                imgItem.selectMiddleImgUri?.let { editViewModel.uploadImage(it) }
            }

            editAdapter.tempEntity.mainImg = mainImg
            editAdapter.tempEntity.desImg = desImg

            val entity = ProductEntity(
                title = tempData.title,
                imageUrl = tempData.mainImg,
                description = tempData.des,
                desImg = tempData.desImg,
                referenceUrl = tempData.web,
                aosUrl = tempData.aos,
                iosUrl = tempData.ios
            )
            with(editViewModel) {
                saveEntity(entity)
                registerProduct()
            }
        }
    }

    private fun clickBackButton() {
        binding.ivPromotionEditFinish.setOnClickListener {
            activity?.finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}