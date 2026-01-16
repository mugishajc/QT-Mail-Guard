package rw.delasoft.qtmailguard.data.mapper

import rw.delasoft.qtmailguard.data.local.database.EmailEntity
import rw.delasoft.qtmailguard.domain.model.Email
import rw.delasoft.qtmailguard.domain.model.VerificationStatus

fun EmailEntity.toDomain(): Email {
    return Email(
        id = id,
        senderName = senderName,
        senderEmailAddress = senderEmailAddress,
        subject = subject,
        body = body,
        attachedImage = attachedImage,
        bodyHash = bodyHash,
        imageHash = imageHash,
        verificationStatus = parseVerificationStatus(verificationStatus),
        importedAt = importedAt
    )
}

fun Email.toEntity(): EmailEntity {
    return EmailEntity(
        id = id,
        senderName = senderName,
        senderEmailAddress = senderEmailAddress,
        subject = subject,
        body = body,
        attachedImage = attachedImage,
        bodyHash = bodyHash,
        imageHash = imageHash,
        verificationStatus = verificationStatus.name,
        importedAt = importedAt
    )
}

private fun parseVerificationStatus(value: String): VerificationStatus {
    return try {
        VerificationStatus.valueOf(value)
    } catch (e: IllegalArgumentException) {
        VerificationStatus.PENDING
    }
}
