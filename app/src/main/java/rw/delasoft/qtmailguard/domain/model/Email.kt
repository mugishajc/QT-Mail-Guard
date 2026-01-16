package rw.delasoft.qtmailguard.domain.model

/**
 * Domain model representing an email with integrity verification status.
 *
 * This is the core business entity used throughout the application.
 * It contains all email fields plus verification metadata.
 *
 * @property id Unique identifier for the email (database primary key)
 * @property senderName Display name of the email sender
 * @property senderEmailAddress Email address of the sender
 * @property subject Email subject line
 * @property body Email body content
 * @property attachedImage Binary data of the attached image
 * @property bodyHash SHA-256 hash of the body for integrity verification
 * @property imageHash SHA-256 hash of the image for integrity verification
 * @property verificationStatus Current integrity verification status
 * @property importedAt Timestamp when the email was imported
 */
data class Email(
    val id: Long = 0,
    val senderName: String,
    val senderEmailAddress: String,
    val subject: String,
    val body: String,
    val attachedImage: ByteArray?,
    val bodyHash: String,
    val imageHash: String,
    val verificationStatus: VerificationStatus,
    val importedAt: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Email

        if (id != other.id) return false
        if (senderName != other.senderName) return false
        if (senderEmailAddress != other.senderEmailAddress) return false
        if (subject != other.subject) return false
        if (body != other.body) return false
        if (attachedImage != null) {
            if (other.attachedImage == null) return false
            if (!attachedImage.contentEquals(other.attachedImage)) return false
        } else if (other.attachedImage != null) return false
        if (bodyHash != other.bodyHash) return false
        if (imageHash != other.imageHash) return false
        if (verificationStatus != other.verificationStatus) return false
        if (importedAt != other.importedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + senderName.hashCode()
        result = 31 * result + senderEmailAddress.hashCode()
        result = 31 * result + subject.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + (attachedImage?.contentHashCode() ?: 0)
        result = 31 * result + bodyHash.hashCode()
        result = 31 * result + imageHash.hashCode()
        result = 31 * result + verificationStatus.hashCode()
        result = 31 * result + importedAt.hashCode()
        return result
    }
}
