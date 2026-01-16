package rw.delasoft.qtmailguard.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import rw.delasoft.qtmailguard.data.local.database.EmailDao
import rw.delasoft.qtmailguard.data.mapper.toDomain
import rw.delasoft.qtmailguard.data.mapper.toEntity
import rw.delasoft.qtmailguard.domain.model.Email
import rw.delasoft.qtmailguard.domain.repository.EmailRepository
import javax.inject.Inject

class EmailRepositoryImpl @Inject constructor(
    private val emailDao: EmailDao
) : EmailRepository {

    override fun getEmails(): Flow<List<Email>> {
        return emailDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getEmailById(id: Long): Flow<Email?> {
        return emailDao.observeById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun saveEmail(email: Email): Long {
        return emailDao.insert(email.toEntity())
    }

    override suspend fun deleteEmail(id: Long) {
        emailDao.deleteById(id)
    }

    override suspend fun clearAllEmails() {
        emailDao.deleteAll()
    }
}
