package rw.delasoft.qtmailguard.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [EmailEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun emailDao(): EmailDao
}
