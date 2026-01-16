package rw.delasoft.qtmailguard.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emails")
data class EmailEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "sender_name")
    val senderName: String,

    @ColumnInfo(name = "sender_email")
    val senderEmailAddress: String,

    val subject: String,

    val body: String,

    @ColumnInfo(name = "attached_image", typeAffinity = ColumnInfo.BLOB)
    val attachedImage: ByteArray?,

    @ColumnInfo(name = "body_hash")
    val bodyHash: String,

    @ColumnInfo(name = "image_hash")
    val imageHash: String,

    @ColumnInfo(name = "verification_status")
    val verificationStatus: String,

    @ColumnInfo(name = "imported_at")
    val importedAt: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmailEntity) return false

        return id == other.id &&
                senderName == other.senderName &&
                senderEmailAddress == other.senderEmailAddress &&
                subject == other.subject &&
                body == other.body &&
                attachedImage.contentEquals(other.attachedImage) &&
                bodyHash == other.bodyHash &&
                imageHash == other.imageHash &&
                verificationStatus == other.verificationStatus &&
                importedAt == other.importedAt
    }

    override fun hashCode(): Int {
        var hash = id.hashCode()
        hash = 31 * hash + senderName.hashCode()
        hash = 31 * hash + senderEmailAddress.hashCode()
        hash = 31 * hash + subject.hashCode()
        hash = 31 * hash + body.hashCode()
        hash = 31 * hash + (attachedImage?.contentHashCode() ?: 0)
        hash = 31 * hash + bodyHash.hashCode()
        hash = 31 * hash + imageHash.hashCode()
        hash = 31 * hash + verificationStatus.hashCode()
        hash = 31 * hash + importedAt.hashCode()
        return hash
    }
}
