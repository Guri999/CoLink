package com.seven.colink.ui.post.content

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.seven.colink.R
import com.seven.colink.databinding.FragmentPostContentBinding
import com.seven.colink.databinding.ItemPostCommentBinding
import com.seven.colink.ui.group.board.list.ApplyRequestFragment
import com.seven.colink.ui.post.content.adapter.PostContentListAdapter
import com.seven.colink.ui.post.content.model.ContentButtonUiState
import com.seven.colink.ui.post.content.model.DialogUiState
import com.seven.colink.ui.post.content.model.PostContentItem
import com.seven.colink.ui.post.content.viewmodel.PostContentViewModel
import com.seven.colink.ui.post.register.post.model.PostErrorMessage
import com.seven.colink.ui.post.register.post.PostFragment
import com.seven.colink.ui.post.register.viewmodel.PostSharedViewModel
import com.seven.colink.ui.sign.signin.SignInActivity
import com.seven.colink.ui.userdetail.UserDetailActivity
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.showToast
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PostContentFragment : Fragment() {
    private var _binding: FragmentPostContentBinding? = null
    private val binding: FragmentPostContentBinding get() = _binding!!
    private lateinit var commentBinding: ItemPostCommentBinding
    private val viewModel: PostContentViewModel by viewModels()
    private val sharedViewModel: PostSharedViewModel by activityViewModels()
    private val postContentListAdapter by lazy {
        PostContentListAdapter(
            onClickItem = { item ->
                when (item) {
                    is PostContentItem.MemberItem -> {
                        startActivity(
                            UserDetailActivity.newIntent(
                                requireActivity(),
                                item.userInfo?.uid ?: return@PostContentListAdapter
                            )
                        )
                    }

                    is PostContentItem.Item -> {
                        Log.d("Post", "isLike Clicked")
                        if (viewModel.checkLogin.value == true) {
                            Log.d("Post", "State = Login")
                            // 좋아요 버튼 클릭 이벤트
                            // if문 안에 viewModel.discernLike(key)로 isLike 구분 하도록 바꾸기
                            // ((contains(key) = true) == (isLike = true))
                            if (item.key?.let { viewModel.discernLike(it) } == false){
                                item.isLike = false
                                item.like = item.like?.minus(1)
                                Log.d("Post", "Like True to False = ${item.isLike}")
                                Log.d("Post", "Like Count = ${item.like}")
                            } else {
                                item.isLike = true
                                item.like = item.like?.plus(1)
                                Log.d("Post", "Like False to True = ${item.isLike}")
                                Log.d("Post", "Like Count = ${item.like}")
                            }
                        } else {
                            Log.d("Post", "State = Logout")
                            requireContext().setDialog(
                                title = "로그인 필요",
                                message = "서비스를 이용하기 위해서는 로그인이 필요합니다. \n로그인 페이지로 이동하시겠습니까?",
                                confirmAction = {
                                    val intent = Intent(requireContext(), SignInActivity::class.java)
                                    startActivity(intent)
                                    it.dismiss()
                                },
                                cancelAction = { it.dismiss() }
                            ).show()
                        }
                    }

                    else -> Unit
                }
            },
            onClickButton = { item, buttonUiState ->
                when (item) {
                    is PostContentItem.RecruitItem -> {
                        when (buttonUiState) {
                            ContentButtonUiState.User -> viewModel.createDialog(item)
                            ContentButtonUiState.Unknown -> {
                                startActivity(Intent(requireContext(), SignInActivity::class.java))
                            }

                            else -> Unit
                        }
                    }

                    else -> Unit
                }
            },
            onClickView = { view ->
                when (view.id) {
                    R.id.tv_apply_request -> {
                        parentFragmentManager.beginTransaction().apply {
                            replace(
                                R.id.fg_activity_post,
                                ApplyRequestFragment()
                            )
                            addToBackStack(null)
                            commit()
                        }
                    }
                }
            },
            onClickCommentButton = {
                viewModel.registerComment(it)
            },
            onClickCommentDeleteButton = {item->
                viewModel.deleteComment(item)
            },
            onClickCommentEditButton = {key, comment ->
                viewModel.editComment(key, comment)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        commentBinding = ItemPostCommentBinding.inflate(layoutInflater)
        _binding = FragmentPostContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
        initSharedViewModel()
    }

    private fun initView() = with(binding) {
        recyclerViewPostContent.adapter = postContentListAdapter
        recyclerViewPostContent.itemAnimator = null

        binding.ivFinish.setOnClickListener {
            activity?.finish()
        }

        binding.tvEdit.setOnClickListener {
            lifecycleScope.launch {
                sharedViewModel.setEntryType(PostEntryType.UPDATE)
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.fg_activity_post, PostFragment())
                    commit()
                }
            }
        }
    }

    private fun initViewModel() = with(viewModel) {
        uiState.observe(viewLifecycleOwner) { items ->
            postContentListAdapter.submitList(items)
            checkLogin()
        }

        updateButtonUiState.observe(viewLifecycleOwner) {
            binding.tvEdit.visibility =
                if (it == ContentButtonUiState.Manager) View.VISIBLE else View.GONE
        }

        dialogUiState.observe(viewLifecycleOwner) { state ->
            if (state == null) {
                return@observe
            }
            showDialog(state)
        }

        errorUiState.observe(requireActivity()) { errorState ->
            errorState ?: return@observe

            val messageResId = when (errorState.message) {
                PostErrorMessage.ALREADY_SUPPORT, PostErrorMessage.SUCCESS_SUPPORT -> {
                    errorState.message.message1
                }

                else -> return@observe
            }

            requireContext().showToast(getString(messageResId))
        }


    }

    private fun initSharedViewModel() = with(sharedViewModel) {
        lifecycleScope.launch {
            key.collect {
                if (it != null) {
                    viewModel.setEntity(it)
                    viewModel.initViewStateByEntity()
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun showDialog(state: DialogUiState?) {
        state?.recruitItem?.let {
            requireContext().setDialog(
                title = getString(R.string.support_dialog_title, state.title),
                message = getString(R.string.support_dialog_message, state.message, state.title),
                image = if (state.groupType == GroupType.PROJECT) R.drawable.img_dialog_project else R.drawable.img_dialog_study,
                confirmAction = {
                    it.dismiss()
                    lifecycleScope.launch {
                        viewModel.applyForProject(
                            recruitItem = state.recruitItem
                        )
                    }
                    it.dismiss()
                },
                cancelAction = { it.dismiss() }
            ).show()
        }
    }
}