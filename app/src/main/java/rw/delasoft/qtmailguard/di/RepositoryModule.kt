package rw.delasoft.qtmailguard.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import rw.delasoft.qtmailguard.data.repository.EmailRepositoryImpl
import rw.delasoft.qtmailguard.domain.repository.EmailRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEmailRepository(impl: EmailRepositoryImpl): EmailRepository
}
