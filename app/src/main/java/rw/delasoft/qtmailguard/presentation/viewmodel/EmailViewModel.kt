package rw.delasoft.qtmailguard.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rw.delasoft.qtmailguard.domain.model.Email
import rw.delasoft.qtmailguard.domain.model.VerificationStatus
import rw.delasoft.qtmailguard.core.util.SampleEmailGenerator
import rw.delasoft.qtmailguard.domain.usecase.GetEmailsUseCase
import rw.delasoft.qtmailguard.domain.usecase.ParseEmailFileUseCase
import rw.delasoft.qtmailguard.domain.usecase.SaveEmailUseCase
import rw.delasoft.qtmailguard.domain.usecase.VerifyEmailIntegrityUseCase
import rw.delasoft.qtmailguard.presentation.state.EmailEvent
import rw.delasoft.qtmailguard.presentation.state.EmailUiState
import javax.inject.Inject

@HiltViewModel
class EmailViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val parseEmailFile: ParseEmailFileUseCase,
    private val verifyIntegrity: VerifyEmailIntegrityUseCase,
    private val saveEmail: SaveEmailUseCase,
    private val getEmails: GetEmailsUseCase,
    private val sampleEmailGenerator: SampleEmailGenerator
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailUiState())
    val uiState: StateFlow<EmailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EmailEvent>()
    val events = _events.asSharedFlow()

    init {
        loadSavedEmails()
    }

    private fun loadSavedEmails() {
        getEmails()
            .onEach { list ->
                _uiState.update { it.copy(emails = list) }
            }
            .launchIn(viewModelScope)
    }

    fun processSelectedFile(uri: Uri) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isParsing = true, errorMessage = null, email = null, verificationResult = null)
            }
            _events.emit(EmailEvent.ParseStarted)

            try {
                val parseResult = withContext(Dispatchers.IO) {
                    val stream = appContext.contentResolver.openInputStream(uri)
                    if (stream == null) {
                        return@withContext Result.failure<Email>(Exception("Cannot open file"))
                    }
                    stream.use { inputStream ->
                        parseEmailFile(inputStream)
                    }
                }

                parseResult.fold(
                    onSuccess = { email ->
                        _uiState.update { it.copy(isParsing = false, email = email) }
                        _events.emit(EmailEvent.ParseComplete)
                        runVerification(email)
                    },
                    onFailure = { error ->
                        handleError(error.message ?: "Parse failed")
                    }
                )
            } catch (e: Exception) {
                handleError(e.message ?: "File read failed")
            }
        }
    }

    private suspend fun runVerification(email: Email) {
        _uiState.update { it.copy(isVerifying = true) }
        _events.emit(EmailEvent.VerificationStarted)

        delay(300)

        val verification = withContext(Dispatchers.Default) {
            verifyIntegrity(email)
        }

        val verifiedEmail = email.copy(verificationStatus = verification.overallStatus)

        _uiState.update {
            it.copy(
                isVerifying = false,
                email = verifiedEmail,
                verificationResult = verification
            )
        }
        _events.emit(EmailEvent.VerificationComplete)

        persistEmail(verifiedEmail)
    }

    private suspend fun persistEmail(email: Email) {
        try {
            withContext(Dispatchers.IO) {
                saveEmail(email)
            }
            _events.emit(EmailEvent.SaveComplete)
        } catch (e: Exception) {
            // Log silently - saving is not critical for display
        }
    }

    private suspend fun handleError(message: String) {
        _uiState.update {
            it.copy(
                isParsing = false,
                isVerifying = false,
                isLoading = false,
                errorMessage = message
            )
        }
        _events.emit(EmailEvent.Error(message))
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun selectEmailFromHistory(email: Email) {
        viewModelScope.launch {
            if (email.verificationStatus == VerificationStatus.PENDING) {
                _uiState.update { it.copy(email = email, verificationResult = null) }
                runVerification(email)
            } else {
                val verification = verifyIntegrity(email)
                _uiState.update {
                    it.copy(email = email, verificationResult = verification)
                }
            }
        }
    }

    fun clearCurrentEmail() {
        _uiState.update {
            it.copy(email = null, verificationResult = null, errorMessage = null)
        }
    }

    fun generateSampleEmail() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isGeneratingSample = true,
                    isParsing = false,
                    email = null,
                    verificationResult = null
                )
            }

            try {
                val file = withContext(Dispatchers.IO) {
                    val email = sampleEmailGenerator.generateSampleEmail()
                    sampleEmailGenerator.saveToDisk(appContext, email)
                }

                _uiState.update { it.copy(isGeneratingSample = false, isParsing = true) }
                _events.emit(EmailEvent.SampleGenerated(file.absolutePath))

                val parseResult = withContext(Dispatchers.IO) {
                    file.inputStream().use { inputStream ->
                        parseEmailFile(inputStream)
                    }
                }

                parseResult.fold(
                    onSuccess = { email ->
                        _uiState.update { it.copy(isParsing = false, email = email) }
                        _events.emit(EmailEvent.ParseComplete)
                        runVerification(email)
                    },
                    onFailure = { error ->
                        handleError(error.message ?: "Failed to parse generated sample")
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(isGeneratingSample = false, isParsing = false) }
                _events.emit(EmailEvent.Error("Failed to generate sample: ${e.message}"))
            }
        }
    }
}
