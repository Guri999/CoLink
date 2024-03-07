package com.seven.colink.ui.home.child

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.seven.colink.databinding.FragmentHomeProjectBinding
import com.seven.colink.ui.home.HomeViewModel
import com.seven.colink.ui.post.register.PostActivity
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeProjectFragment : Fragment() {

    private var _binding: FragmentHomeProjectBinding? = null
    private val binding get() = _binding!!
//    private val homeViewModel: HomeViewModel by activityViewModels()
    private val homeViewModel: HomeChildViewModel by viewModels()
//    private val mAdapter by lazy { BottomHomeProjectAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.getBottomItems(5, GroupType.PROJECT)
        initViews()
        setObserve()
    }

    private fun initViews() {
        bottomViewsData()
    }

    private fun bottomViewsData() {
        homeViewModel._bottomItems.value?.forEachIndexed { index, bottom ->
            val bottomLayout = when (index) {
                0 -> binding.layProjectBottom1
                1 -> binding.layProjectBottom2
                2 -> binding.layProjectBottom3
                3 -> binding.layProjectBottom4
                else -> binding.layProjectBottom5
            }

            bottomLayout.apply {
                tvHomeBottomStudy.visibility = View.INVISIBLE
                tvHomeBottomProject.visibility = View.VISIBLE
                tvHomeBottomTitle.text = bottom.title
                tvHomeBottomDes.text = bottom.des
                tvHomeBottomKind.text = bottom.kind?.toString()
                viewHomeBottomDivider.visibility = View.INVISIBLE
                tvHomeBottomLv.visibility = View.INVISIBLE
                ivHomeBottomThumubnail.load(bottom.img)
                if (bottom.blind == ProjectStatus.END) {
                    viewHomeBottomBlind.visibility = View.VISIBLE
                    tvHomeBottomBlind.visibility = View.VISIBLE
                } else {
                    viewHomeBottomBlind.visibility = View.INVISIBLE
                    tvHomeBottomBlind.visibility = View.INVISIBLE
                }
                layBottom.setOnClickListener {
                    lifecycleScope.launch {
                        val key = bottom.key
                        val entity = key?.let { homeViewModel.getPost(it) }
                        if (entity != null) {
                            startActivity(
                                PostActivity.newIntent(
                                    context = requireActivity(),
                                    key = key
                                )
                            )
                        } else {
                            Toast.makeText(requireContext(), "다음에 다시 시도해주세요.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun setObserve() {
        homeViewModel._bottomItems.observe(viewLifecycleOwner) {
            bottomViewsData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}