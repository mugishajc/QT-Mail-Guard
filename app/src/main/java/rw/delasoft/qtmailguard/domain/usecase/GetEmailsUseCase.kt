package rw.delasoft.qtmailguard.domain.usecase

import kotlinx.coroutines.flow.Flow
import rw.delasoft.qtmailguard.domain.model.Email
import rw.delasoft.qtmailguard.domain.repository.EmailRepository
import javax.inject.Inject

class GetEmailsUseCase @Inject constructor(
    private val repository: EmailRepository
) {
    operator fun invoke(): Flow<List<Email>> = repository.getEmails()
}
