package com.seven.colink.ui.evaluation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.evaluation.EvaluationActivity.Companion.EXTRA_GROUP_ENTITY
import com.seven.colink.util.status.PageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EvaluationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val handle: SavedStateHandle,
) : ViewModel() {
    private val _evalProjectMembersData = MutableLiveData<List<EvaluationData.EvalProject?>?>()
    val evalProjectMembersData: LiveData<List<EvaluationData.EvalProject?>?> get() = _evalProjectMembersData

    private val _evalStudyMembersData = MutableLiveData<List<EvaluationData.EvalStudy?>?>()
    val evalStudyMembersData: LiveData<List<EvaluationData.EvalStudy?>?> get() = _evalStudyMembersData

    private val _currentPage = MutableStateFlow(PageState.FIRST)
    val currentPage = _currentPage.asStateFlow()

    private val _currentGroup = MutableStateFlow(GroupEntity())
    val currentGroup = _currentGroup.asStateFlow()

    private val _currentUid = MutableStateFlow("")
    val currentUid = _currentUid.asStateFlow()
    private val groupKey get() = handle.get<String>(EXTRA_GROUP_ENTITY)

    init {
        viewModelScope.launch {
            _currentGroup.value =
                groupRepository.getGroupDetail(groupKey ?: return@launch).getOrNull()
                    ?: return@launch
        }

        viewModelScope.launch {
            _currentUid.value = authRepository.getCurrentUser().message
        }
    }


    fun getProjectMembers(groupEntity: GroupEntity, uid: String) {
        viewModelScope.launch {
            // 현재 유저
            // 액티비티에서 받은 그룹 키 값으로 GroupEntity의 memberIds를 받아옴
            _evalProjectMembersData.value = groupEntity.memberIds.filter {
                it != uid
            }.mapNotNull {// map으로 해당 그룹에서 속해 있는 UserEntity 받아옴
                userRepository.getUserDetails(it).getOrNull()?.convertEvalProjectData()
            }
            Log.d("Evaluation", "evalMembersData = ${_evalProjectMembersData.value}")
        }
    }

    fun updateProjectMembers(
        position: Int,
        q1: Float,
        q2: Float,
        q3: Float,
        q4: Float,
        q5: Float
    ) {
        val projectMembers = _evalProjectMembersData.value?.toMutableList() ?: return

        if (position >= 0 && position < projectMembers.size) {
            val member = projectMembers[position]
            member?.let {
                it.communication = q1
                it.technic = q2
                it.diligence = q3
                it.flexibility = q4
                it.creativity = q5
                it.grade = ((q1 + q2 + q3 + q4 + q5) / 5).toDouble()
            }
            _evalProjectMembersData.value = projectMembers
            Log.d("Evaluation", "### updateProjectMembers = $projectMembers")
        }
    }

    // 완료 버튼 클릭 시 user의 grade를 계산 후, 저장 시켜주기
    fun updateProjectUserGrade(groupEntity: GroupEntity, currentUid: String) {
        viewModelScope.launch {
            evalProjectMembersData.value?.map { data ->
                userRepository.getUserDetails(data?.uid!!).getOrNull().let { member ->
                    userRepository.updateUserInfo(
                        member!!.copy(
                            grade = ((member.grade!! * member.evaluatedNumber + data.grade!! * 2) / ++data.evalCount),
                            communication = data.communication,
                            technicalSkill = data.technic,
                            diligence = data.diligence,
                            flexibility = data.flexibility,
                            creativity = data.creativity,
                            evaluatedNumber = ++data.evalCount,
                        )
                    )
                }
            }
            Log.d("Evaluation", "@@@ evalProjectMembersData.value = ${evalProjectMembersData.value}")
            groupRepository.registerGroup(
                groupEntity.let {
                    it.copy(evaluateMember = it.evaluateMember?.plus(currentUid))
                }
            )
            Log.d("Evaluation", "@@@ currentUid = $currentUid")
            Log.d("Evaluation", "@@@ groupEntity.evaluateMember = ${groupEntity.evaluateMember}")
        }
    }


    private fun UserEntity.convertEvalProjectData() =
        EvaluationData.EvalProject(
            uid = uid,
            name = name,
            photoUrl = photoUrl,
            grade = grade,
            communication = communication,
            technic = technicalSkill,
            diligence = diligence,
            flexibility = flexibility,
            creativity = creativity,
            evalCount = evaluatedNumber
        )

    fun getStudyMembers(groupEntity: GroupEntity, uid: String) {
        viewModelScope.launch {
            _evalStudyMembersData.value = groupEntity.memberIds.filter {
                it != uid
            }.mapNotNull {
                userRepository.getUserDetails(it).getOrNull()?.convertEvalStudyData()
            }
            Log.d("Evaluation", "evalStudyMembersData = ${_evalStudyMembersData.value}")
        }
    }

    fun updateStudyMembers(position: Int, q1: Float, q2: Float, q3: Float) {
        val studyMembers = _evalStudyMembersData.value?.toMutableList() ?: return

        if (position >= 0 && position < studyMembers.size) {
            val member = studyMembers[position]
            member?.let {
                it.diligence = q1
                it.communication = q2
                it.flexibility = q3
                it.grade = ((q1 + q2 + q3) / 3).toDouble()
            }
            _evalStudyMembersData.value = studyMembers
            Log.d("Evaluation", "### updateStudyMembers = $studyMembers")
        }
    }

    fun updateStudyUserGrade(groupEntity: GroupEntity, currentUid: String) {
        viewModelScope.launch {
            evalStudyMembersData.value?.map { data ->
                userRepository.getUserDetails(data?.uid!!).getOrNull().let { member ->
                    userRepository.updateUserInfo(
                        member!!.copy(
                            grade = (member.grade!! * member.evaluatedNumber + data.grade!! * 2) / data.evalCount++,
                            diligence = data.diligence,
                            communication = data.communication,
                            flexibility = data.flexibility,
                            evaluatedNumber = data.evalCount
                        )
                    )
                }
            }
            groupRepository.registerGroup(
                groupEntity.let {
                    it.copy(evaluateMember = it.evaluateMember?.plus(currentUid))
                }
            )
        }
    }

    private fun UserEntity.convertEvalStudyData() =
        EvaluationData.EvalStudy(
            uid = uid,
            name = name,
            photoUrl = photoUrl,
            grade = grade,
            diligence = diligence,
            communication = communication,
            flexibility = flexibility,
            evalCount = evaluatedNumber
        )

    private fun EvaluationData.EvalStudy.convertStudyUserEntity() =
        UserEntity(
            uid = uid,
            name = name,
            photoUrl = photoUrl,
            grade = grade,
            diligence = diligence,
            communication = communication,
            flexibility = flexibility,
            evaluatedNumber = evalCount
        )

    fun updatePage(position: Int) {
        _currentPage.value =
            when (position) {
                0 -> if (currentGroup.value.memberIds.size - 2 > position) PageState.FIRST else PageState.LAST
                currentGroup.value.memberIds.size - 2 -> PageState.LAST
                else -> PageState.MIDDLE
            }.apply { num = position }
        Log.d("Evaluation", "_currentPage.value = ${_currentPage.value}")
        Log.d(
            "Evaluation",
            "currentGroup.value.memberIds.size = ${currentGroup.value.memberIds.size}"
        )
    }
}