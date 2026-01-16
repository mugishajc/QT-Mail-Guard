package rw.delasoft.qtmailguard.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailDao {

    @Query("SELECT * FROM emails ORDER BY imported_at DESC")
    fun observeAll(): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE id = :emailId")
    fun observeById(emailId: Long): Flow<EmailEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: EmailEntity): Long

    @Query("DELETE FROM emails WHERE id = :emailId")
    suspend fun deleteById(emailId: Long)

    @Query("DELETE FROM emails")
    suspend fun deleteAll()
}
