package com.seven.colink.domain.repository

import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.util.status.DataResultStatus

interface UserRepository {
    suspend fun registerUser(user: UserEntity): DataResultStatus
    suspend fun getUserDetails(id: String): Result<UserEntity?>
}