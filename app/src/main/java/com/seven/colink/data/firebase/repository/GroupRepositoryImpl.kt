package com.seven.colink.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.data.firebase.type.DataBaseType
import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.util.status.DataResultStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GroupRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : GroupRepository {
    override suspend fun registerGroup(group: GroupEntity) = suspendCoroutine { continuation ->
        firestore.collection(DataBaseType.GROUP.title).document(group.key).set(group)
            .addOnSuccessListener {
                continuation.resume(DataResultStatus.SUCCESS)
            }
            .addOnFailureListener { e ->
                continuation.resume(DataResultStatus.FAIL.apply {
                    message = e.message ?: "Unknown error"
                })
            }
    }

    override suspend fun getGroupDetail(key: String) = runCatching {
        firestore.collection(DataBaseType.GROUP.title).document(key).get().await()
            .toObject(GroupEntity::class.java)
    }

    override suspend fun updateGroup(key: String, updatedGroup: GroupEntity) =
        suspendCoroutine { continuation ->
            val updateMap = mutableMapOf<String, Any?>()

            if (updatedGroup.title != null) {
                updateMap["title"] = updatedGroup.title
            }
            if (updatedGroup.description != null) {
                updateMap["description"] = updatedGroup.description
            }
            if (updatedGroup.tags != null) {
                updateMap["tags"] = updatedGroup.tags
            }
            if (updatedGroup.imageUrl != null) {
                updateMap["imageUrl"] = updatedGroup.imageUrl
            }

            firestore.collection(DataBaseType.GROUP.title).document(key)
                .update(updateMap)
                .addOnSuccessListener {
                    continuation.resume(DataResultStatus.SUCCESS)
                }
                .addOnFailureListener { e ->
                    continuation.resume(DataResultStatus.FAIL.apply {
                        message = e.message ?: "Unknown error"
                    })
                }
        }

    override suspend fun getGroupByContainUserId(userId: String) = runCatching {
        firestore.collection(DataBaseType.GROUP.title)
            .whereArrayContains("memberIds", userId)
            .get().await()
            .documents.mapNotNull {
                it.toObject(GroupEntity::class.java)
            }
    }
}