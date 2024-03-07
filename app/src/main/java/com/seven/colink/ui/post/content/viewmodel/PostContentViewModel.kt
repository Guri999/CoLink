package com.seven.colink.ui.post.content.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.R
import com.seven.colink.domain.entity.ApplicationInfo
import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.domain.usecase.GetPostUseCase
import com.seven.colink.domain.usecase.RegisterPostUseCase
import com.seven.colink.ui.post.content.model.ContentOwnerButtonUiState
import com.seven.colink.ui.post.content.model.DialogUiState
import com.seven.colink.ui.post.content.model.PostContentItem
import com.seven.colink.ui.post.register.post.model.Post
import com.seven.colink.util.status.ApplicationStatus
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.GroupType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostContentViewModel @Inject constructor(
    private val app: Application,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val postRepository: PostRepository,
    private val getPostUseCase: GetPostUseCase,
    private val registerPostUseCase: RegisterPostUseCase,
) : ViewModel() {
    private lateinit var entity: Post
    private val _uiState = MutableLiveData<List<PostContentItem>>()
    val uiState: LiveData<List<PostContentItem>> get() = _uiState

    private val _dialogUiState = MutableLiveData(
        DialogUiState.init()
    )
    val dialogUiState: LiveData<DialogUiState> get() = _dialogUiState

    private val _updateButtonUiState = MutableLiveData<ContentOwnerButtonUiState>()
    val updateButtonUiState: LiveData<ContentOwnerButtonUiState> get() = _updateButtonUiState

    suspend fun setEntity(key: String) {
        entity = getPostUseCase(key)?: return
    }

    fun initViewStateByEntity() = viewModelScope.launch {
        determineUserButtonUiState(entity)
        updatePostContentItems(entity.recruit)
        incrementPostViews()
    }

    private suspend fun determineUserButtonUiState(post: Post) {
        _updateButtonUiState.value =
            if (post.authId == getCurrentUser()) ContentOwnerButtonUiState.Owner
            else ContentOwnerButtonUiState.User
    }

    private fun updatePostContentItems(updatedRecruitList: List<RecruitInfo>?) =
        viewModelScope.launch {
            val items = mutableListOf<PostContentItem>()

            entity.let { currentEntity ->
                items.add(createImageItem(currentEntity))
                items.add(createGroupTypeItem(currentEntity))
                items.add(createPostContentItem(currentEntity))
                items.add(createTitleItem(R.string.recruitment_status))
                items.addAll(createPostRecruit(updatedRecruitList))
                items.add(createTitleItem(if (currentEntity.groupType == GroupType.PROJECT) R.string.project_member_info else R.string.study_member_info))
                items.add(createSubTitleItem(R.string.project_team_member))
                items.addAll(createMember(currentEntity))

                _uiState.value = items
            }
        }


    private fun createGroupTypeItem(uiState: Post) = PostContentItem.GroupTypeItem(
        groupType = uiState.groupType,
    )

    private fun createPostContentItem(uiState: Post) = PostContentItem.Item(
        key = uiState.key,
        authId = uiState.authId,
        title = uiState.title,
        status = uiState.status,
        description = uiState.description,
        tags = uiState.tags,
        registeredDate = uiState.registeredDate,
        views = uiState.views
    )

    private fun createImageItem(uiState: Post) = PostContentItem.ImageItem(
        imageUrl = uiState.imageUrl.orEmpty()
    )

    private fun createTitleItem(titleRes: Int) = PostContentItem.TitleItem(titleRes = titleRes)

    private fun createSubTitleItem(subTitleRes: Int) =
        PostContentItem.SubTitleItem(titleRes = subTitleRes)

    private fun createPostRecruit(recruitList: List<RecruitInfo>?) =
        recruitList?.map { recruitInfo ->
            PostContentItem.RecruitItem(
                recruit = recruitInfo,
                buttonUiState = updateButtonUiState.value ?: ContentOwnerButtonUiState.User
            )
        } ?: emptyList()

    private suspend fun createMember(uiState: Post): List<PostContentItem.MemberItem> {
        return uiState.memberIds.mapNotNull { memberId ->
            userRepository.getUserDetails(memberId).getOrNull()?.let {
                PostContentItem.MemberItem(userInfo = it)
            }
        }
    }

    private suspend fun getCurrentUser(): String? {
        return authRepository.getCurrentUser().let {
            if (it == DataResultStatus.SUCCESS) it.message else null
        }
    }

    suspend fun applyForProject(recruitItem: PostContentItem.RecruitItem): DataResultStatus {
        if (isAlreadySupported(recruitItem)) {
            return DataResultStatus.FAIL.apply {
                message = app.getString(R.string.already_supported)
            }
        }

        val newApplicationInfo = ApplicationInfo(
            userId = getCurrentUser(),
            applicationStatus = ApplicationStatus.PENDING,
        )
        val updatedRecruitList = updateRecruitList(recruitItem, newApplicationInfo)

        return updatePost(updatedRecruitList, ApplicationStatus.PENDING)
    }


    private suspend fun isAlreadySupported(recruitItem: PostContentItem.RecruitItem): Boolean {
        return entity.recruit?.any { recruitInfo ->
            recruitInfo.type == recruitItem.recruit.type &&
                    recruitInfo.applicationInfos?.any { it.userId == getCurrentUser() } == true
        } == true
    }

    private fun updateRecruitList(
        recruitItem: PostContentItem.RecruitItem,
        newApplicationInfo: ApplicationInfo
    ): List<RecruitInfo>? {
        return entity.recruit?.map { recruitInfo ->
            if (recruitInfo.type == recruitItem.recruit.type) {
                recruitInfo.copy(applicationInfos = (recruitInfo.applicationInfos.orEmpty() + newApplicationInfo.copy(recruitId = recruitItem.recruit.key)).toList())
            } else {
                recruitInfo
            }
        }
    }

    private suspend fun updatePost(
        updatedRecruitList: List<RecruitInfo>?,
        applicationStatus: ApplicationStatus
    ): DataResultStatus {
        return entity.copy(recruit = updatedRecruitList).let { updatedEntity ->
            when (registerPostUseCase(updatedEntity)) {
                DataResultStatus.SUCCESS -> {
                    DataResultStatus.SUCCESS.apply {
                        updatePostContentItems(updatedEntity.recruit)
                        message = if (applicationStatus == ApplicationStatus.APPROVE)
                            app.getString(R.string.approved_processing_completed)
                        else
                            app.getString(R.string.refusal_completed)
                    }
                }

                else -> DataResultStatus.FAIL
            }
        }
    }

    fun createDialog(recruitItem: PostContentItem.RecruitItem) {
        _dialogUiState.value = _dialogUiState.value?.copy(
            title = if (entity.groupType == GroupType.PROJECT) app.getString(R.string.project_kor) else app.getString(
                R.string.study_kor
            ),
            message = entity.title,
            recruitItem = recruitItem
        )
    }

    suspend fun getUserEntitiesFromRecruit(item: PostContentItem.RecruitItem): List<UserEntity> {
        return item.recruit.applicationInfos
            ?.filter { it.applicationStatus == ApplicationStatus.PENDING }
            ?.mapNotNull { it.userId?.trim() }
            ?.mapNotNull { userRepository.getUserDetails(it).getOrNull() }
            ?: emptyList()
    }

    suspend fun updateApplicationStatus(
        applicationStatus: ApplicationStatus,
        userEntity: UserEntity,
        item: PostContentItem.RecruitItem
    ): DataResultStatus {
        val updatedRecruitList = updateRecruitList(applicationStatus, userEntity, item)

        if (applicationStatus == ApplicationStatus.APPROVE) {
            if (updateMemberList(userEntity) != DataResultStatus.SUCCESS) {
                return DataResultStatus.FAIL.apply {
                    message = app.getString(R.string.failed_error)
                }
            }
        }

        return updateApplicationStatus(updatedRecruitList, applicationStatus)
    }

    private fun updateRecruitList(
        applicationStatus: ApplicationStatus,
        userEntity: UserEntity,
        item: PostContentItem.RecruitItem
    ): List<RecruitInfo>? {
        return entity.recruit?.map { recruitInfo ->
            if (recruitInfo.type == item.recruit.type) {
                recruitInfo.copy(
                    applicationInfos = recruitInfo.applicationInfos?.map { applicationInfo ->
                        if (applicationInfo.userId == userEntity.uid) {
                            applicationInfo.copy(applicationStatus = applicationStatus)
                        } else {
                            applicationInfo
                        }
                    }.orEmpty()
                )
            } else {
                recruitInfo
            }
        }
    }

    private fun updateMemberList(userEntity: UserEntity): DataResultStatus {
        return entity.let { currentEntity ->
            currentEntity.memberIds.toMutableList().apply {
                userEntity.uid?.let { add(it) }
                entity = currentEntity.copy(memberIds = this)
            }

            DataResultStatus.SUCCESS
        }
    }

    private suspend fun updateApplicationStatus(
        updatedRecruitList: List<RecruitInfo>?,
        applicationStatus: ApplicationStatus
    ): DataResultStatus {
        return entity.copy(recruit = updatedRecruitList).let { updatedEntity ->
            when (val updateResult = registerPostUseCase(updatedEntity)) {
                DataResultStatus.SUCCESS -> {
                    updatePostContentItems(updatedEntity.recruit)
                    DataResultStatus.SUCCESS.apply {
                        message = if (applicationStatus == ApplicationStatus.APPROVE)
                            app.getString(R.string.approved_processing_completed)
                        else
                            app.getString(R.string.refusal_completed)
                    }
                }

                else -> updateResult
            }
        }
    }


    private suspend fun incrementPostViews(): DataResultStatus =
        postRepository.incrementPostViews(entity.key)


}
