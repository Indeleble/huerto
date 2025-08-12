package com.wyllyw.huertoplan.domain.usecase.user

import com.wyllyw.huertoplan.data.dao.UserDao
import com.wyllyw.huertoplan.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val userDao: UserDao
) {
    suspend operator fun invoke(id: String): Result<User?> {
        return withContext(Dispatchers.IO) {
            try {
                if (id.isBlank()) {
                    Result.failure(IllegalArgumentException("User ID cannot be blank"))
                } else {
                    val user = userDao.getUserById(id)
                    Result.success(user)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}