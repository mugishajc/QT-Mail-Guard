package rw.delasoft.qtmailguard.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExtensionsTest {

    @Test
    fun `truncate returns original string when shorter than max`() {
        val input = "Short"
        val result = input.truncate(10)
        assertEquals("Short", result)
    }

    @Test
    fun `truncate adds suffix when string exceeds max`() {
        val input = "This is a long message"
        val result = input.truncate(10)
        assertEquals("This is...", result)
    }

    @Test
    fun `truncate with custom suffix works`() {
        val input = "Long text here"
        val result = input.truncate(8, "~")
        assertEquals("Long te~", result)
    }

    @Test
    fun `extractInitials returns first letters of words`() {
        val input = "Mugisha Jean Claude"
        val result = input.extractInitials()
        assertEquals("MJ", result)
    }

    @Test
    fun `extractInitials handles single word`() {
        val input = "Mugisha"
        val result = input.extractInitials()
        assertEquals("M", result)
    }

    @Test
    fun `extractInitials respects max chars`() {
        val input = "Alpha Beta Gamma Delta"
        val result = input.extractInitials(3)
        assertEquals("ABG", result)
    }

    @Test
    fun `extractInitials handles empty string`() {
        val input = ""
        val result = input.extractInitials()
        assertEquals("", result)
    }

    @Test
    fun `extractInitials handles blank words`() {
        val input = "  Jean   Claude  "
        val result = input.extractInitials()
        assertEquals("JC", result)
    }

    @Test
    fun `isNullOrEmpty returns true for null`() {
        val bytes: ByteArray? = null
        assertTrue(bytes.isNullOrEmpty())
    }

    @Test
    fun `isNullOrEmpty returns true for empty array`() {
        val bytes = ByteArray(0)
        assertTrue(bytes.isNullOrEmpty())
    }

    @Test
    fun `isNullOrEmpty returns false for non-empty array`() {
        val bytes = byteArrayOf(1, 2, 3)
        assertFalse(bytes.isNullOrEmpty())
    }

    @Test
    fun `toHexString converts bytes correctly`() {
        val bytes = byteArrayOf(0x0A, 0x1B, 0xFF.toByte())
        val result = bytes.toHexString()
        assertEquals("0a1bff", result)
    }

    @Test
    fun `toFormattedDate formats timestamp`() {
        val timestamp = 1704067200000L
        val result = timestamp.toFormattedDate("yyyy-MM-dd")
        assertTrue(result.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }
}
