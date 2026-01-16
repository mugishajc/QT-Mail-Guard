package rw.delasoft.qtmailguard.domain.usecase

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import rw.delasoft.qtmailguard.core.security.Sha256HashGenerator
import rw.delasoft.qtmailguard.domain.model.Email
import rw.delasoft.qtmailguard.domain.model.VerificationStatus

class VerifyEmailIntegrityUseCaseTest {

    private lateinit var useCase: VerifyEmailIntegrityUseCase
    private lateinit var hashGenerator: Sha256HashGenerator

    @Before
    fun setup() {
        hashGenerator = Sha256HashGenerator()
        useCase = VerifyEmailIntegrityUseCase(hashGenerator)
    }

    @Test
    fun `verification passes when hashes match`() {
        val body = "Test email body content"
        val image = "fake image data".toByteArray()

        val email = Email(
            id = 1,
            senderName = "Test Sender",
            senderEmailAddress = "test@example.rw",
            subject = "Test Subject",
            body = body,
            attachedImage = image,
            bodyHash = hashGenerator.sha256(body),
            imageHash = hashGenerator.sha256(image),
            verificationStatus = VerificationStatus.PENDING
        )

        val result = useCase(email)

        assertTrue(result.isBodyVerified)
        assertTrue(result.isImageVerified)
        assertTrue(result.isFullyVerified)
    }

    @Test
    fun `verification fails when body hash mismatches`() {
        val body = "Test email body"
        val image = "image data".toByteArray()

        val email = Email(
            id = 1,
            senderName = "Sender",
            senderEmailAddress = "sender@mail.rw",
            subject = "Subject",
            body = body,
            attachedImage = image,
            bodyHash = "incorrect_hash_value",
            imageHash = hashGenerator.sha256(image),
            verificationStatus = VerificationStatus.PENDING
        )

        val result = useCase(email)

        assertFalse(result.isBodyVerified)
        assertTrue(result.isImageVerified)
        assertFalse(result.isFullyVerified)
    }

    @Test
    fun `verification fails when image hash mismatches`() {
        val body = "Email body"
        val image = "image bytes".toByteArray()

        val email = Email(
            id = 1,
            senderName = "Sender",
            senderEmailAddress = "sender@test.rw",
            subject = "Subject",
            body = body,
            attachedImage = image,
            bodyHash = hashGenerator.sha256(body),
            imageHash = "wrong_image_hash",
            verificationStatus = VerificationStatus.PENDING
        )

        val result = useCase(email)

        assertTrue(result.isBodyVerified)
        assertFalse(result.isImageVerified)
        assertFalse(result.isFullyVerified)
    }

    @Test
    fun `verification passes when no image and empty image hash`() {
        val body = "Body without image"

        val email = Email(
            id = 1,
            senderName = "Sender",
            senderEmailAddress = "sender@domain.rw",
            subject = "No Image",
            body = body,
            attachedImage = null,
            bodyHash = hashGenerator.sha256(body),
            imageHash = "",
            verificationStatus = VerificationStatus.PENDING
        )

        val result = useCase(email)

        assertTrue(result.isBodyVerified)
        assertTrue(result.isImageVerified)
        assertTrue(result.isFullyVerified)
    }

    @Test
    fun `verification is case insensitive for hash comparison`() {
        val body = "Test body"
        val image = "Test image".toByteArray()

        val bodyHash = hashGenerator.sha256(body).uppercase()
        val imageHash = hashGenerator.sha256(image).uppercase()

        val email = Email(
            id = 1,
            senderName = "Sender",
            senderEmailAddress = "sender@mail.rw",
            subject = "Case Test",
            body = body,
            attachedImage = image,
            bodyHash = bodyHash,
            imageHash = imageHash,
            verificationStatus = VerificationStatus.PENDING
        )

        val result = useCase(email)

        assertTrue(result.isBodyVerified)
        assertTrue(result.isImageVerified)
    }
}
