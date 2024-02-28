package com.seven.colink.domain.entity

import com.seven.colink.util.convert.convertLocalDateTime
import java.time.LocalDateTime

data class UserEntity(
    val uid: String? = "",
    val email: String? = "",
    val password: String? = "",
    val name: String? = "",
    val photoUrl: String? = null,
    val phoneNumber: String? = null,
    val level: Int? = 0,
    val mainSpecialty: String? = "",
    val specialty: String? = "",
    val grade: Double? = 5.0,
    val skill: List<String>? = emptyList(),
    val git: String? = null,
    val blog: String? = null,
    val link: String? = null,
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
