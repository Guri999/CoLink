package com.seven.colink.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.seven.colink.data.firebase.type.DataBaseType
import com.seven.colink.domain.entity.ApplicationInfoEntity
import com.seven.colink.domain.entity.RecruitEntity
import com.seven.colink.domain.repository.RecruitRepository
import com.seven.colink.util.status.DataResultStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RecruitRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): RecruitRepository {
    override suspend fun registerRecruit(recruit: RecruitEntity) = suspendCoroutine { continuation ->
        firestore.collection(DataBaseType.RECRUIT.title).document(recruit.key).set(recruit)
            .addOnSuccessListener {
                continuation.resume(DataResultStatus.SUCCESS.apply { message = recruit.key })
            }
            .addOnFailureListener {
                continuation.resume(DataResultStatus.FAIL.apply { message = it.message?: "Unknown error" })
            }
    }

    override suspend fun registerApplicationInfo(appInfo: ApplicationInfoEntity) = suspendCoroutine { continuation ->
        firestore.collection(DataBaseType.APPINFO.title).document(appInfo.key).set(appInfo)
            .addOnSuccessListener {
                continuation.resume(DataResultStatus.SUCCESS.apply { message = appInfo.key })
            }
            .addOnFailureListener {
                continuation.resume(DataResultStatus.FAIL.apply { message = it.message?: "Unknown error" })
            }
    }

    override suspend fun getRecruit(key: String) = run {
        firestore.collection(DataBaseType.APPINFO.title).document(key).get().await()
            .toObject(RecruitEntity::class.java)
    }

    override suspend fun getApplicationInfo(key: String) = run {
        firestore.collection(DataBaseType.APPINFO.title).document(key).get().await()
            .toObject(ApplicationInfoEntity::class.java)
    }
}