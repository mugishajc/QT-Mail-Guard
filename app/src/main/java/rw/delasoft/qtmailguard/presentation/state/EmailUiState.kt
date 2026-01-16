package rw.delasoft.qtmailguard.presentation.state

import rw.delasoft.qtmailguard.domain.model.Email
import rw.delasoft.qtmailguard.domain.model.VerificationResult

data class EmailUiState(
    val isLoading: Boolean = false,
    val isParsing: Boolean = false,
    val isVerifying: Boolean = false,
    val email: Email? = null,
    val verificationResult: VerificationResult? = null,
    val errorMessage: String? = null,
    val emails: List<Email> = emptyList()
)

sealed interface EmailEvent {
    data object FileSelected : EmailEvent
    data object ParseStarted : EmailEvent
    data object ParseComplete : EmailEvent
    data object VerificationStarted : EmailEvent
    data object VerificationComplete : EmailEvent
    data object SaveComplete : EmailEvent
    data class Error(val message: String) : EmailEvent
}
