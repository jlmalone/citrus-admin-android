package com.citrus.admin.di

import com.citrus.admin.data.repository.AlertRepository
import com.citrus.admin.data.repository.AlertRepositoryImpl
import com.citrus.admin.data.repository.AnalyticsRepository
import com.citrus.admin.data.repository.AnalyticsRepositoryImpl
import com.citrus.admin.data.repository.ReceiptRepository
import com.citrus.admin.data.repository.ReceiptRepositoryImpl
import com.citrus.admin.data.repository.UserRepository
import com.citrus.admin.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindReceiptRepository(impl: ReceiptRepositoryImpl): ReceiptRepository

    @Binds
    @Singleton
    abstract fun bindAlertRepository(impl: AlertRepositoryImpl): AlertRepository

    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(impl: AnalyticsRepositoryImpl): AnalyticsRepository
}
