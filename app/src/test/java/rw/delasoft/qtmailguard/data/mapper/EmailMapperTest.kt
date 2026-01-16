package rw.delasoft.qtmailguard.data.mapper

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import rw.delasoft.qtmailguard.data.local.database.EmailEntity
import rw.delasoft.qtmailguard.domain.model.Email
import rw.delasoft.qtmailguard.domain.model.VerificationStatus

class EmailMapperTest {

    @Test
    fun `entity to domain maps all fields correctly`() {
        val entity = EmailEntity(
            id = 42,
            senderName = "Test User",
            senderEmailAddress = "test@domain.rw",
            subject = "Test Subject",
            body = "Test body content",
            attachedImage = byteArrayOf(1, 2, 3),
            bodyHash = "body_hash_value",
            imageHash = "image_hash_value",
            verificationStatus = "VERIFIED",
            importedAt = 1234567890L
        )

        val domain = entity.toDomain()

        assertEquals(42L, domain.id)
        assertEquals("Test User", domain.senderName)
        assertEquals("test@domain.rw", domain.senderEmailAddress)
        assertEquals("Test Subject", domain.subject)
        assertEquals("Test body content", domain.body)
        assertArrayEquals(byteArrayOf(1, 2, 3), domain.attachedImage)
        assertEquals("body_hash_value", domain.bodyHash)
        assertEquals("image_hash_value", domain.imageHash)
        assertEquals(VerificationStatus.VERIFIED, domain.verificationStatus)
        assertEquals(1234567890L, domain.importedAt)
    }

    @Test
    fun `domain to entity maps all fields correctly`() {
        val domain = Email(
            id = 99,
            senderName = "Sender Name",
            senderEmailAddress = "sender@email.rw",
            subject = "Email Subject",
            body = "Email body",
            attachedImage = byteArrayOf(4, 5, 6),
            bodyHash = "hash1",
            imageHash = "hash2",
            verificationStatus = VerificationStatus.VERIFICATION_FAILED,
            importedAt = 9876543210L
        )

        val entity = domain.toEntity()

        assertEquals(99L, entity.id)
        assertEquals("Sender Name", entity.senderName)
        assertEquals("sender@email.rw", entity.senderEmailAddress)
        assertEquals("Email Subject", entity.subject)
        assertEquals("Email body", entity.body)
        assertArrayEquals(byteArrayOf(4, 5, 6), entity.attachedImage)
        assertEquals("hash1", entity.bodyHash)
        assertEquals("hash2", entity.imageHash)
        assertEquals("VERIFICATION_FAILED", entity.verificationStatus)
        assertEquals(9876543210L, entity.importedAt)
    }

    @Test
    fun `entity with null image maps to domain with null image`() {
        val entity = EmailEntity(
            id = 1,
            senderName = "Name",
            senderEmailAddress = "email@test.rw",
            subject = "Subject",
            body = "Body",
            attachedImage = null,
            bodyHash = "hash",
            imageHash = "",
            verificationStatus = "PENDING",
            importedAt = 0L
        )

        val domain = entity.toDomain()

        assertNull(domain.attachedImage)
    }

    @Test
    fun `unknown verification status defaults to PENDING`() {
        val entity = EmailEntity(
            id = 1,
            senderName = "Name",
            senderEmailAddress = "email@test.rw",
            subject = "Subject",
            body = "Body",
            attachedImage = null,
            bodyHash = "hash",
            imageHash = "",
            verificationStatus = "UNKNOWN_STATUS",
            importedAt = 0L
        )

        val domain = entity.toDomain()

        assertEquals(VerificationStatus.PENDING, domain.verificationStatus)
    }
}
