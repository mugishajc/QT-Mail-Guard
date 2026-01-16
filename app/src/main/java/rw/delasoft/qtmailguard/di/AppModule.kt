package rw.delasoft.qtmailguard.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import rw.delasoft.qtmailguard.core.security.HashGenerator
import rw.delasoft.qtmailguard.core.security.Sha256HashGenerator

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindHashGenerator(impl: Sha256HashGenerator): HashGenerator
}
