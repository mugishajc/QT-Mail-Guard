package rw.delasoft.qtmailguard.domain.model

/**
 * Represents the integrity verification status of an email.
 */
enum class VerificationStatus {
    /**
     * Both body and image hashes match the computed values.
     * Data integrity is confirmed.
     */
    VERIFIED,

    /**
     * One or more hashes do not match the computed values.
     * Data may have been tampered with or corrupted.
     */
    VERIFICATION_FAILED,

    /**
     * Verification has not been performed yet.
     */
    PENDING
}
