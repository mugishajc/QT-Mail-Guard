package rw.delasoft.qtmailguard.domain.usecase

import rw.delasoft.qtmailguard.core.security.HashGenerator
import rw.delasoft.qtmailguard.domain.model.Email
import rw.delasoft.qtmailguard.domain.model.VerificationResult
import javax.inject.Inject

class VerifyEmailIntegrityUseCase @Inject constructor(
    private val hashGenerator: HashGenerator
) {
    operator fun invoke(email: Email): VerificationResult {
        val bodyHashComputed = hashGenerator.sha256(email.body)
        val imageHashComputed = email.attachedImage?.let { hashGenerator.sha256(it) } ?: ""

        val bodyMatches = bodyHashComputed.equals(email.bodyHash, ignoreCase = true)
        val imageMatches = if (email.attachedImage == null && email.imageHash.isEmpty()) {
            true
        } else {
            imageHashComputed.equals(email.imageHash, ignoreCase = true)
        }

        return VerificationResult(
            isBodyVerified = bodyMatches,
            isImageVerified = imageMatches,
            computedBodyHash = bodyHashComputed,
            computedImageHash = imageHashComputed,
            expectedBodyHash = email.bodyHash,
            expectedImageHash = email.imageHash
        )
    }
}
