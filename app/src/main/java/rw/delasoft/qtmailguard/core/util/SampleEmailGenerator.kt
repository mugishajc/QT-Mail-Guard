package rw.delasoft.qtmailguard.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import rw.delasoft.qtmailguard.core.security.HashGenerator
import rw.delasoft.qtmailguard.proto.SecureEmail
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class SampleEmailGenerator @Inject constructor(
    private val hashGenerator: HashGenerator
) {
    fun generateSampleEmail(
        senderName: String = "Mugisha Jean Claude",
        senderEmail: String = "mugisha.jc@qtglobal.rw",
        subject: String = "Project Update - QTMail Guard Development",
        body: String = buildSampleBody(),
        imageBytes: ByteArray = generateSampleImage()
    ): SecureEmail {
        val bodyHash = hashGenerator.sha256(body)
        val imageHash = hashGenerator.sha256(imageBytes)

        return SecureEmail.newBuilder()
            .setSenderName(senderName)
            .setSenderEmailAddress(senderEmail)
            .setSubject(subject)
            .setBody(body)
            .setAttachedImage(com.google.protobuf.ByteString.copyFrom(imageBytes))
            .setBodyHash(bodyHash)
            .setImageHash(imageHash)
            .build()
    }

    fun saveToDisk(context: Context, email: SecureEmail, fileName: String = "sample_email.pb"): File {
        val file = File(context.getExternalFilesDir(null), fileName)
        FileOutputStream(file).use { output ->
            email.writeTo(output)
        }
        return file
    }

    private fun buildSampleBody(): String {
        return """
            Muraho,

            I hope this message finds you well. I wanted to share an update on the QTMail Guard project development progress.

            Key Milestones Achieved:
            - Implemented secure Protocol Buffer parsing
            - Added SHA-256 integrity verification
            - Integrated SQLCipher encrypted database
            - Completed Material 3 UI with dark mode support

            The application now successfully verifies email integrity by comparing SHA-256 hashes of the body content and attached images against stored values.

            Please review the attached diagram showing the verification flow.

            Murakoze,
            Mugisha Jean Claude
            Android Developer
            QT Global Software Ltd
            Kigali, Rwanda
        """.trimIndent()
    }

    private fun generateSampleImage(): ByteArray {
        val width = 400
        val height = 300
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(Color.parseColor("#E3F2FD"))

        val paint = Paint().apply {
            color = Color.parseColor("#1565C0")
            textSize = 32f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        canvas.drawText("QTMail Guard", width / 2f, height / 2f - 20, paint)

        paint.textSize = 18f
        paint.color = Color.parseColor("#424242")
        canvas.drawText("Sample Attachment", width / 2f, height / 2f + 20, paint)

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        bitmap.recycle()

        return stream.toByteArray()
    }
}
