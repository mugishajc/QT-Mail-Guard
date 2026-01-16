package rw.delasoft.qtmailguard.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import rw.delasoft.qtmailguard.core.security.DatabaseKeyManager
import rw.delasoft.qtmailguard.data.local.database.AppDatabase
import rw.delasoft.qtmailguard.data.local.database.EmailDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabaseKeyManager(
        @ApplicationContext context: Context
    ): DatabaseKeyManager {
        return DatabaseKeyManager(context)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        keyManager: DatabaseKeyManager
    ): AppDatabase {
        val passphrase = keyManager.getOrCreatePassphrase()
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "qt_mail_guard.db"
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideEmailDao(database: AppDatabase): EmailDao {
        return database.emailDao()
    }
}
