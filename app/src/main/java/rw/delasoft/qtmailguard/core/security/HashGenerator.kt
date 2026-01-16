package rw.delasoft.qtmailguard.core.security

import java.security.MessageDigest
import javax.inject.Inject

interface HashGenerator {
    fun sha256(input: String): String
    fun sha256(bytes: ByteArray): String
}

class Sha256HashGenerator @Inject constructor() : HashGenerator {

    override fun sha256(input: String): String {
        return sha256(input.toByteArray(Charsets.UTF_8))
    }

    override fun sha256(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(bytes)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
