package rw.delasoft.qtmailguard.core.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class Sha256HashGeneratorTest {

    private lateinit var hashGenerator: Sha256HashGenerator

    @Before
    fun setup() {
        hashGenerator = Sha256HashGenerator()
    }

    @Test
    fun `sha256 of empty string returns correct hash`() {
        val expected = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        val result = hashGenerator.sha256("")
        assertEquals(expected, result)
    }

    @Test
    fun `sha256 of known string returns correct hash`() {
        val input = "Hello, Kigali!"
        val result = hashGenerator.sha256(input)
        assertEquals(64, result.length)
        assertEquals(result, hashGenerator.sha256(input))
    }

    @Test
    fun `sha256 produces different hashes for different inputs`() {
        val hash1 = hashGenerator.sha256("message one")
        val hash2 = hashGenerator.sha256("message two")
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun `sha256 of byte array matches string equivalent`() {
        val text = "test message"
        val hashFromString = hashGenerator.sha256(text)
        val hashFromBytes = hashGenerator.sha256(text.toByteArray(Charsets.UTF_8))
        assertEquals(hashFromString, hashFromBytes)
    }

    @Test
    fun `sha256 is case sensitive`() {
        val hashLower = hashGenerator.sha256("hello")
        val hashUpper = hashGenerator.sha256("HELLO")
        assertNotEquals(hashLower, hashUpper)
    }

    @Test
    fun `sha256 handles unicode characters`() {
        val input = "Muraho! Amakuru?"
        val result = hashGenerator.sha256(input)
        assertEquals(64, result.length)
    }

    @Test
    fun `sha256 of empty byte array returns correct hash`() {
        val expected = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        val result = hashGenerator.sha256(ByteArray(0))
        assertEquals(expected, result)
    }

    @Test
    fun `sha256 returns lowercase hex string`() {
        val result = hashGenerator.sha256("test")
        assertEquals(result, result.lowercase())
    }
}
