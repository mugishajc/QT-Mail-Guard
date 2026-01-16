package rw.delasoft.qtmailguard.domain.repository

import kotlinx.coroutines.flow.Flow
import rw.delasoft.qtmailguard.domain.model.Email

interface EmailRepository {

    fun getEmails(): Flow<List<Email>>

    fun getEmailById(id: Long): Flow<Email?>

    suspend fun saveEmail(email: Email): Long

    suspend fun deleteEmail(id: Long)

    suspend fun clearAllEmails()
}
