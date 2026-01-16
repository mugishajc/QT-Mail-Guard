package rw.delasoft.qtmailguard.domain.model

/**
 * Detailed result of SHA-256 integrity verification.
 *
 * @property isBodyVerified True if the body hash matches
 * @property isImageVerified True if the image hash matches
 * @property computedBodyHash The SHA-256 hash computed from the body
 * @property computedImageHash The SHA-256 hash computed from the image
 * @property expectedBodyHash The hash stored in the protobuf file
 * @property expectedImageHash The hash stored in the protobuf file
 */
data class VerificationResult(
    val isBodyVerified: Boolean,
    val isImageVerified: Boolean,
    val computedBodyHash: String,
    val computedImageHash: String,
    val expectedBodyHash: String,
    val expectedImageHash: String
) {
    /**
     * Overall verification status based on both body and image verification.
     */
    val overallStatus: VerificationStatus
        get() = if (isBodyVerified && isImageVerified) {
            VerificationStatus.VERIFIED
        } else {
            VerificationStatus.VERIFICATION_FAILED
        }

    /**
     * Checks if the overall verification passed.
     */
    val isFullyVerified: Boolean
        get() = isBodyVerified && isImageVerified
}
