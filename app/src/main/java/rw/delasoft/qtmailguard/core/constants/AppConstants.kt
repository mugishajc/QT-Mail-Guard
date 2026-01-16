package rw.delasoft.qtmailguard.core.constants

object AppConstants {
    const val DATABASE_NAME = "qt_mail_guard.db"
    const val DATABASE_VERSION = 1

    object Crypto {
        const val HASH_ALGORITHM = "SHA-256"
        const val KEY_SIZE = 256
        const val GCM_TAG_LENGTH = 128
        const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        const val KEY_ALIAS = "qt_mail_guard_db_key"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
    }

    object Prefs {
        const val SECURE_PREFS_NAME = "qt_mail_guard_secure_prefs"
        const val KEY_ENCRYPTED_PASSPHRASE = "encrypted_db_passphrase"
        const val KEY_IV = "encryption_iv"
    }

    object FileTypes {
        val SUPPORTED_MIME_TYPES = arrayOf(
            "application/octet-stream",
            "application/x-protobuf",
            "*/*"
        )
        const val PROTO_EXTENSION = ".pb"
    }

    object Animation {
        const val SHIMMER_DURATION_MS = 1200
        const val FADE_DURATION_MS = 300
        const val SLIDE_DURATION_MS = 400
    }
}
