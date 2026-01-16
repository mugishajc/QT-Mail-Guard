package rw.delasoft.qtmailguard.domain.usecase

import rw.delasoft.qtmailguard.domain.model.Email
import rw.delasoft.qtmailguard.domain.repository.EmailRepository
import javax.inject.Inject

class SaveEmailUseCase @Inject constructor(
    private val repository: EmailRepository
) {
    suspend operator fun invoke(email: Email): Long = repository.saveEmail(email)
}
