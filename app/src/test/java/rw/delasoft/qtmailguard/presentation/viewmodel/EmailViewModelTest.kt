package rw.delasoft.qtmailguard.presentation.viewmodel

import android.content.Context
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import rw.delasoft.qtmailguard.core.util.SampleEmailGenerator
import rw.delasoft.qtmailguard.domain.model.Email
import rw.delasoft.qtmailguard.domain.model.VerificationResult
import rw.delasoft.qtmailguard.domain.model.VerificationStatus
import rw.delasoft.qtmailguard.domain.usecase.GetEmailsUseCase
import rw.delasoft.qtmailguard.domain.usecase.ParseEmailFileUseCase
import rw.delasoft.qtmailguard.domain.usecase.SaveEmailUseCase
import rw.delasoft.qtmailguard.domain.usecase.VerifyEmailIntegrityUseCase
import rw.delasoft.qtmailguard.presentation.state.EmailEvent

@OptIn(ExperimentalCoroutinesApi::class)
class EmailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: EmailViewModel
    private lateinit var mockContext: Context
    private lateinit var mockParseEmailFile: ParseEmailFileUseCase
    private lateinit var mockVerifyIntegrity: VerifyEmailIntegrityUseCase
    private lateinit var mockSaveEmail: SaveEmailUseCase
    private lateinit var mockGetEmails: GetEmailsUseCase
    private lateinit var mockSampleEmailGenerator: SampleEmailGenerator

    private val testEmail = Email(
        id = 1,
        senderName = "Test Sender",
        senderEmailAddress = "test@example.rw",
        subject = "Test Subject",
        body = "Test body content",
        attachedImage = null,
        bodyHash = "abc123",
        imageHash = "",
        verificationStatus = VerificationStatus.PENDING
    )

    private val verifiedResult = VerificationResult(
        isBodyVerified = true,
        isImageVerified = true,
        computedBodyHash = "abc123",
        computedImageHash = "",
        expectedBodyHash = "abc123",
        expectedImageHash = ""
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockContext = mockk(relaxed = true)
        mockParseEmailFile = mockk()
        mockVerifyIntegrity = mockk()
        mockSaveEmail = mockk(relaxed = true)
        mockGetEmails = mockk()
        mockSampleEmailGenerator = mockk()

        every { mockGetEmails() } returns flowOf(emptyList())
        every { mockVerifyIntegrity(any()) } returns verifiedResult

        viewModel = EmailViewModel(
            appContext = mockContext,
            parseEmailFile = mockParseEmailFile,
            verifyIntegrity = mockVerifyIntegrity,
            saveEmail = mockSaveEmail,
            getEmails = mockGetEmails,
            sampleEmailGenerator = mockSampleEmailGenerator
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has no email and no errors`() = runTest {
        val state = viewModel.uiState.value

        assertNull(state.email)
        assertNull(state.verificationResult)
        assertNull(state.errorMessage)
        assertFalse(state.isLoading)
        assertFalse(state.isParsing)
        assertFalse(state.isVerifying)
        assertFalse(state.isGeneratingSample)
    }

    @Test
    fun `clearCurrentEmail resets email state`() = runTest {
        viewModel.selectEmailFromHistory(testEmail)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearCurrentEmail()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.email)
        assertNull(state.verificationResult)
        assertNull(state.errorMessage)
    }

    @Test
    fun `selectEmailFromHistory triggers verification for pending email`() = runTest {
        val pendingEmail = testEmail.copy(verificationStatus = VerificationStatus.PENDING)

        viewModel.selectEmailFromHistory(pendingEmail)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(VerificationStatus.VERIFIED, state.email?.verificationStatus)
        assertTrue(state.verificationResult?.isFullyVerified == true)
    }

    @Test
    fun `selectEmailFromHistory shows verification for already verified email`() = runTest {
        val verifiedEmail = testEmail.copy(verificationStatus = VerificationStatus.VERIFIED)

        viewModel.selectEmailFromHistory(verifiedEmail)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(verifiedEmail.id, state.email?.id)
        assertTrue(state.verificationResult != null)
    }

    @Test
    fun `verification result shows failed status when body hash mismatches`() = runTest {
        val failedResult = VerificationResult(
            isBodyVerified = false,
            isImageVerified = true,
            computedBodyHash = "computed",
            computedImageHash = "",
            expectedBodyHash = "different",
            expectedImageHash = ""
        )
        every { mockVerifyIntegrity(any()) } returns failedResult

        viewModel.selectEmailFromHistory(testEmail)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(VerificationStatus.VERIFICATION_FAILED, state.email?.verificationStatus)
        assertFalse(state.verificationResult?.isFullyVerified == true)
    }

    @Test
    fun `emails list is loaded on init`() = runTest {
        val emailList = listOf(testEmail, testEmail.copy(id = 2))
        every { mockGetEmails() } returns flowOf(emailList)

        val newViewModel = EmailViewModel(
            appContext = mockContext,
            parseEmailFile = mockParseEmailFile,
            verifyIntegrity = mockVerifyIntegrity,
            saveEmail = mockSaveEmail,
            getEmails = mockGetEmails,
            sampleEmailGenerator = mockSampleEmailGenerator
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, newViewModel.uiState.value.emails.size)
    }

    @Test
    fun `events flow emits VerificationComplete after selecting email`() = runTest {
        viewModel.events.test {
            viewModel.selectEmailFromHistory(testEmail)
            testDispatcher.scheduler.advanceUntilIdle()

            val event = awaitItem()
            assertTrue(event is EmailEvent.VerificationStarted || event is EmailEvent.VerificationComplete)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearError removes error message from state`() = runTest {
        viewModel.clearError()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.errorMessage)
    }
}
