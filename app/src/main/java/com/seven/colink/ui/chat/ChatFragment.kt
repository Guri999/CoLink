package com.seven.colink.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.data.firebase.repository.AuthRepositoryImpl
import com.seven.colink.data.firebase.repository.PostRepositoryImpl
import com.seven.colink.data.firebase.repository.UserRepositoryImpl
import com.seven.colink.databinding.FragmentChatBinding
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.ui.sign.signin.SignInActivity
import com.seven.colink.util.status.GroupType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ChatFragment : Fragment() {

    companion object {
        fun newInstance() = ChatFragment()
    }
    private var _binding: FragmentChatBinding? = null
    private lateinit var viewModel: ChatViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        initButton()
    }

    private fun initButton() = with(binding) {

        signup.setOnClickListener {
            lifecycleScope.launch {
                startActivity(Intent(requireActivity(), SignInActivity::class.java))
            }
        }

        login.setOnClickListener {
            lifecycleScope.launch {
                val id = AuthRepositoryImpl(FirebaseAuth.getInstance()).signIn("zxcasd@wdsad.com", "tlarbtkd")
                val user = UserRepositoryImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance()).getUserDetails(
                    id.getOrNull()?.uid ?: ""
                ).getOrNull()
                withContext(Dispatchers.Main) {
                    if (user!= null) {
                        testInfo.text = user.toString()
                    } else {
                        Toast.makeText(context, "등록된 사용자가 없습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        logout.setOnClickListener {
            lifecycleScope.launch {
                AuthRepositoryImpl(FirebaseAuth.getInstance()).signOut()
            }
        }

        makegle.setOnClickListener {

            lifecycleScope.launch {
                val a = PostRepositoryImpl(FirebaseFirestore.getInstance()).searchQuery("개발")
                testInfo.text = a.getOrNull()?.map {
                    it.title.toString() + it.description.toString()
                }.toString()
            }
        }

        seegle.setOnClickListener {
            val key = "unique_post_key"
            lifecycleScope.launch {
                val post = PostRepositoryImpl(FirebaseFirestore.getInstance()).getPost(key)
                testInfo.text = (post.getOrNull() as PostEntity).title
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}