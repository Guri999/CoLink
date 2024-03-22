package com.seven.colink.ui.promotion.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.ProductEntity
import com.seven.colink.domain.entity.TempProductEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.ImageRepository
import com.seven.colink.domain.repository.ProductRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.promotion.model.ProductPromotionItems
import com.seven.colink.util.convert.convertLocalDateTime
import com.seven.colink.util.status.DataResultStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductPromotionEditViewModel @Inject constructor(
    private val context: Application,
    private val groupRepository: GroupRepository,
    private val productRepository : ProductRepository,
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {
    var entity = ProductEntity()

    private val _result = MutableLiveData<DataResultStatus>()
    private val _product = MutableLiveData<ProductEntity>()
    private val _setLeader = MutableLiveData<ProductPromotionItems.ProjectLeaderItem>()
    private val _setMember = MutableLiveData<MutableList<ProductPromotionItems.ProjectMember>>()
    private val _isLoading = MutableLiveData<Boolean>()
    private val _key = MutableLiveData<String>()
    val key : LiveData<String> get() = _key
    val result : LiveData<DataResultStatus> get() = _result
    val product : LiveData<ProductEntity> get() = _product
    val setLeader : LiveData<ProductPromotionItems.ProjectLeaderItem> get() = _setLeader
    val setMember : MutableLiveData<MutableList<ProductPromotionItems.ProjectMember>> get() = _setMember
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun init(key: String) {
        if (entity.title?.isEmpty() == true) {
            initPostToProduct(key)
        }else {
            initProduct(key)
        }
    }

    private fun initPostToProduct(key: String) {  //포스트에서 프로덕트로 만들때
        viewModelScope.launch {
            val ids = groupRepository.getGroupDetail(key)

            entity = ProductEntity(
                key = "PRD_" + UUID.randomUUID().toString(),
                projectId = "",
                authId = ids.getOrNull()?.authId ?: "",
                memberIds = ids.getOrNull()?.memberIds ?: emptyList(),
                title = "",
                imageUrl = "",
                description = "",
                desImg = "",
                tags = emptyList(),
                registeredDate = LocalDateTime.now().convertLocalDateTime(),
                referenceUrl = null,
                aosUrl = null,
                iosUrl = null
            )
            getMemberDetail(key)
        }
    }

    private fun initProduct(key: String) {  //프로덕트를 편집할때
        viewModelScope.launch {
            val id = productRepository.getProductDetail(key)
            id.onSuccess {
                _product.value = it
            }
        }
    }

    fun getMemberDetail(key: String) {
        var memberList = mutableListOf<ProductPromotionItems.ProjectMember>()

        viewModelScope.launch {
            val ids = groupRepository.getGroupDetail(key)

            val getLeaderDetail = ids.getOrNull()?.authId?.let { authId -> userRepository.getUserDetails(authId) }
            val setLeaderItem = ProductPromotionItems.ProjectLeaderItem(getLeaderDetail)

            entity = ProductEntity(authId = ids.getOrNull()?.authId)

            val memIds = ids.getOrNull()?.memberIds
            if (memIds != null) {
                var memberDetailList = mutableListOf<ProductPromotionItems.ProjectMember>()
                memIds.forEach { id ->
                    val detail = userRepository.getUserDetails(id)
                    val userNt = detail.getOrNull()
                    val user = UserEntity().copy(
                                uid = userNt?.uid,
                                name = userNt?.name,
                                photoUrl = userNt?.photoUrl,
                                level = userNt?.level,
                                grade = userNt?.grade,
                                skill = userNt?.skill,
                                info = userNt?.info,
                                participantsChatRoomIds = userNt?.participantsChatRoomIds,
                                chatRoomKeyList = userNt?.chatRoomKeyList
                            )

                    memberDetailList.add(ProductPromotionItems.ProjectMember(user))
                    val delAuth = ids.getOrNull()?.authId
                    val delList = memberDetailList.filterNot { member ->
                        member.userInfo?.uid == delAuth
                    }
                    val list = mutableListOf<String>()
                    delList.forEach {
                        it.userInfo?.uid?.let { uid -> list.add(uid) }
                    }
                    memberDetailList = delList.toMutableList()
                    entity = entity.copy(memberIds = list)

                }
                    val setMemberItem = memberDetailList
                    memberList.plus(setMemberItem)
                memberList = memberList.plus(setMemberItem).toMutableList()
                }
            _setMember.value = memberList
            _setLeader.value = setLeaderItem
            }
        }

    fun saveEntity(nt: ProductEntity) {
        entity = entity.copy(
            title = nt.title,
            imageUrl = nt.imageUrl,
            description = nt.description,
            desImg = nt.desImg,
            referenceUrl = nt.referenceUrl,
            aosUrl = nt.aosUrl,
            iosUrl = nt.iosUrl
        )
    }

    suspend fun uploadImage(uri: Uri): String =
        imageRepository.uploadImage(uri).getOrThrow().toString()

    fun registerProduct()  {
        _product.value = entity
        saveProduct(entity)
        viewModelScope.launch {
            _result.value = productRepository.registerProduct(entity)
        }
        _key.value = entity.key
    }

    private fun saveProduct(nt : ProductEntity) {
        _product.value = nt
    }
}