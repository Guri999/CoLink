package com.seven.colink.ui.userdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.seven.colink.R
import com.seven.colink.databinding.FragmentUserDetailBinding
import com.seven.colink.ui.mypage.MyPageItem
import com.seven.colink.ui.mypage.MyPageSkilItemManager
import com.seven.colink.util.skillCategory

class UserDetailFragment : Fragment() {
    private lateinit var binding: FragmentUserDetailBinding
    private lateinit var viewModel: UserDetailViewModel
    private lateinit var adapter: UserSkillAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {

        binding = FragmentUserDetailBinding.inflate(layoutInflater)


        viewModel.userDetails.observe(viewLifecycleOwner) {userDetails ->
            updateUI(userDetails)
            adapter.changeDataset(userDetails.userSkill?.map { UserSkillItem(it,UserSkillItemManager.addItem(it)) }?: emptyList())
        }


        return binding.root
    }

    private fun updateUI(user: UserDetailModel){
        binding.tvUserdetailName.text = user.userName
        binding.tvUserdetailAboutMe.text = user.userInfo
        binding.tvUserdetailSpecialty.text = user.userMainSpecialty
        binding.tvUserdetailScore.text = user.userscore.toString()
    }

    private fun userSkill(){

    }

}