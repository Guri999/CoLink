package com.seven.colink.ui.home.child

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.FragmentHomeProjectBinding
import com.seven.colink.databinding.FragmentHomeStudyBinding
import com.seven.colink.ui.home.BottomItems

class HomeProjectFragment : Fragment() {
    private var _binding: FragmentHomeProjectBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeProjectBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews(){
        bottomViews()
    }

    private fun bottomViews() {
        val bottomList = mutableListOf(
            BottomItems("Project", "이거 뭘로 정하지", "설명을 뭐로하지 일단 설명을 길게 길게 길게 길게 길게길게길게길게", "AOS", "초심자", R.drawable.img_dialog_project, false, false),
            BottomItems("Project", "저거 뭘로 정하지", "설명", "Kotlin", "학생", R.drawable.img_user_grade, false, false),
            BottomItems("Project", "그거 뭘로 정하지", "뭐로하지", "Java", "없음", R.drawable.img_dialog_study, false, false),
            BottomItems("Project", "이곳 뭘로 정하지", "설명을 해야하나", "C언어", "중급자", R.drawable.img_dialog_project, true, true),
            BottomItems("Project", "지금 뭘로 정하지", "뭘 해야하누", "AOS", "초급자", R.drawable.img_dialog_project, true, true)
        )

        val layout = listOf(
            binding.layProjectBottom1,binding.layProjectBottom2,
            binding.layProjectBottom3,binding.layProjectBottom4,
            binding.layProjectBottom5
        )

        layout.forEachIndexed { index, bottom ->
            val item = bottomList[index]
            with(bottom) {
                tvHomeBottomStudy.visibility = View.INVISIBLE
                tvHomeBottomProject.visibility = View.VISIBLE
                tvHomeBottomProject.text = item.typeId
                tvHomeBottomTitle.text = item.title
                tvHomeBottomDes.text = item.des
                tvHomeBottomKind.text = item.kind
                tvHomeBottomLv.text = item.lv
                ivHomeBottomThumubnail.load(item.img)
                if (item.blind && item.complete) {
                    viewHomeBottomBlind.visibility = View.VISIBLE
                    tvHomeBottomBlind.visibility = View.VISIBLE
                }else {
                    viewHomeBottomBlind.visibility = View.INVISIBLE
                    tvHomeBottomBlind.visibility = View.INVISIBLE
                }
            }
        }
    }
}