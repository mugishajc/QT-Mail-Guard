package rw.delasoft.qtmailguard.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class DatabaseKeyManager(private val context: Context) {

    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "qt_mail_guard_db_key"
        private const val PREFS_NAME = "qt_mail_guard_secure_prefs"
        private const val PREF_ENCRYPTED_KEY = "encrypted_db_passphrase"
        private const val PREF_IV = "encryption_iv"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH = 128
    }

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
    }

    fun getOrCreatePassphrase(): ByteArray {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val storedEncrypted = prefs.getString(PREF_ENCRYPTED_KEY, null)
        val storedIv = prefs.getString(PREF_IV, null)

        if (storedEncrypted != null && storedIv != null) {
            return decryptPassphrase(
                Base64.decode(storedEncrypted, Base64.DEFAULT),
                Base64.decode(storedIv, Base64.DEFAULT)
            )
        }

        val newPassphrase = generateRandomPassphrase()
        val (encrypted, iv) = encryptPassphrase(newPassphrase)

        prefs.edit()
            .putString(PREF_ENCRYPTED_KEY, Base64.encodeToString(encrypted, Base64.DEFAULT))
            .putString(PREF_IV, Base64.encodeToString(iv, Base64.DEFAULT))
            .apply()

        return newPassphrase
    }

    private fun getOrCreateMasterKey(): SecretKey {
        if (keyStore.containsAlias(KEY_ALIAS)) {
            val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
            return entry.secretKey
        }

        val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)

        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGen.init(spec)
        return keyGen.generateKey()
    }

    private fun generateRandomPassphrase(): ByteArray {
        val bytes = ByteArray(32)
        java.security.SecureRandom().nextBytes(bytes)
        return bytes
    }

    private fun encryptPassphrase(passphrase: ByteArray): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateMasterKey())

        val encrypted = cipher.doFinal(passphrase)
        return encrypted to cipher.iv
    }

    private fun decryptPassphrase(encrypted: ByteArray, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateMasterKey(), spec)
        return cipher.doFinal(encrypted)
    }
}
