package com.seven.colink.ui.userdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.ChatRoomEntity
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.domain.usecase.GetChatRoomUseCase
import com.seven.colink.ui.mypage.MyPagePostModel
import com.seven.colink.ui.userdetail.UserDetailActivity.Companion.EXTRA_USER_KEY
import com.seven.colink.util.convert.convertToDaysAgo
import com.seven.colink.util.status.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val getChatRoomUseCase: GetChatRoomUseCase,
    handle: SavedStateHandle,
): ViewModel() {

    private val _userDetails = MutableLiveData<UserDetailModel>()
    private val _userDetailPosts = MutableLiveData<List<UserDetailPostModel>>()
    val userDetails: LiveData<UserDetailModel> = _userDetails
    val userDetailPost: LiveData<List<UserDetailPostModel>> = _userDetailPosts
    private var userId: String

    private val _chatRoom = MutableSharedFlow<ChatRoomEntity>()
    val chatRoom = _chatRoom.asSharedFlow()

    private val _detailEvent = MutableSharedFlow<String>()
    val detailEvent = _detailEvent.asSharedFlow()
    init {
        userId = handle.get<String>(EXTRA_USER_KEY)?: ""
        loadUserDetails()
        loadUserPost()
    }

     fun detailEvent(){
        viewModelScope.launch {
            _detailEvent.emit(userId)
        }
    }

    private fun loadUserDetails(){
        viewModelScope.launch {
            val result = userRepository.getUserDetails(userId)
            result.onSuccess { user->
                _userDetails.postValue(user?.convertUserEntity())
            }
        }
    }

    private fun loadUserPost() {
        viewModelScope.launch {
            val result = postRepository.getPostByAuthId(userId)
            result.onSuccess { post ->
                _userDetailPosts.postValue(post.map { it.convertPostEntity() })
            }
        }
    }

    fun registerChatRoom() {
        viewModelScope.launch{
            _chatRoom.emit(getChatRoomUseCase(userId))
        }
    }

    suspend fun getPost(key: String) : PostEntity? {
        return postRepository.getPost(key).getOrNull()
    }


    private fun UserEntity.convertUserEntity() = UserDetailModel(
        userName = name,
        userProfile = photoUrl,
        userLevel = level,
        userMainSpecialty = mainSpecialty,
        userBlog = blog,
        userGit = git,
        userSkill = skill,
        userInfo = info,
        userscore = grade,
        userLink = link
    )

    private fun PostEntity.convertPostEntity() = UserDetailPostModel(
        key = key,
        title = title,
        ing = status,
        grouptype = groupType,
        time = registeredDate?.convertToDaysAgo()
    )

}