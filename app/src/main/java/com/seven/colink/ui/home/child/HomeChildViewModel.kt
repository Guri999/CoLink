package com.seven.colink.ui.home.child

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.ui.home.BottomItems
import com.seven.colink.util.status.GroupType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeChildViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {
    var _bottomItems: MutableLiveData<List<BottomItems>> = MutableLiveData(mutableListOf())

    fun getBottomItems(num: Int,type : GroupType?) {
        var getBottomItemList: MutableList<BottomItems> = mutableListOf()

        viewModelScope.launch {
            getBottomItemList.clear()
            val repository = postRepository.getRecentPost(num,type)
            kotlin.runCatching {
                repository.forEach {
                    var bottomRecentItem = BottomItems(it.groupType,it.title,it.description
                        ,it.tags,it.imageUrl,it.key,it.status,it.status)
                    getBottomItemList.add(bottomRecentItem)
                }
                _bottomItems.value = getBottomItemList
            }.onFailure { exception ->
                Log.e("HomeChildViewModel", "#aaa error $exception")
            }
        }
    }

    suspend fun getPost(key: String): PostEntity? {
        return postRepository.getPost(key).getOrNull()
    }
}