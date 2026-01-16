package rw.delasoft.qtmailguard.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toFormattedDate(pattern: String = "MMM dd, yyyy HH:mm"): String {
    return try {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.format(Date(this))
    } catch (e: Exception) {
        ""
    }
}

fun String.truncate(maxLength: Int, suffix: String = "..."): String {
    return if (this.length > maxLength) {
        this.take(maxLength - suffix.length) + suffix
    } else {
        this
    }
}

fun String.extractInitials(maxChars: Int = 2): String {
    return this.split(" ")
        .filter { it.isNotBlank() }
        .take(maxChars)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
}

fun ByteArray?.isNullOrEmpty(): Boolean = this == null || this.isEmpty()

fun ByteArray.toHexString(): String {
    return joinToString("") { byte -> "%02x".format(byte) }
}
