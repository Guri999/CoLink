package com.seven.colink.domain.entity

import com.seven.colink.util.convert.convertLocalDateTime
import java.time.LocalDateTime
import java.util.UUID

data class UserEntity(
    val uid: String = "",
    val email: String = "",
    val password: String = "",
    val id: String = "",
    val name: String = "",
    val photoUrl: String? = null,
    val phoneNumber: String? = null,
    val level: Int = 0,
    val specialty: String = "",
    val grade: Double = 5.0,
    val skill: List<String> = emptyList(),
    val blog: List<String> = emptyList(),
    val info: String? = null,
    val registeredDate: String = LocalDateTime.now().convertLocalDateTime(),
    val communication: Int? = null,
    val technicalSkill: Int? = null,
    val diligence: Int? = null,
    val flexibility: Int? = null,
    val creativity: Int? = null,
    val evaluatedNumber: Int = 0,
    val chatRoomKeyList: List<String>? = emptyList(),
)