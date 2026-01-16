package rw.delasoft.qtmailguard.domain.usecase

import kotlinx.coroutines.flow.Flow
import rw.delasoft.qtmailguard.domain.model.Email
import rw.delasoft.qtmailguard.domain.repository.EmailRepository
import javax.inject.Inject

class GetEmailByIdUseCase @Inject constructor(
    private val repository: EmailRepository
) {
    operator fun invoke(emailId: Long): Flow<Email?> = repository.getEmailById(emailId)
}
