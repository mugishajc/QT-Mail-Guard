package rw.delasoft.qtmailguard.domain.usecase

import rw.delasoft.qtmailguard.domain.model.Email
import rw.delasoft.qtmailguard.domain.model.VerificationStatus
import rw.delasoft.qtmailguard.proto.SecureEmail
import java.io.InputStream
import javax.inject.Inject

class ParseEmailFileUseCase @Inject constructor() {

    operator fun invoke(inputStream: InputStream): Result<Email> {
        return try {
            val protoEmail = SecureEmail.parseFrom(inputStream)

            val email = Email(
                senderName = protoEmail.senderName,
                senderEmailAddress = protoEmail.senderEmailAddress,
                subject = protoEmail.subject,
                body = protoEmail.body,
                attachedImage = protoEmail.attachedImage.toByteArray().takeIf { it.isNotEmpty() },
                bodyHash = protoEmail.bodyHash,
                imageHash = protoEmail.imageHash,
                verificationStatus = VerificationStatus.PENDING
            )

            Result.success(email)
        } catch (e: Exception) {
            Result.failure(EmailParseException("Failed to parse protobuf file: ${e.message}", e))
        }
    }
}

class EmailParseException(message: String, cause: Throwable? = null) : Exception(message, cause)
