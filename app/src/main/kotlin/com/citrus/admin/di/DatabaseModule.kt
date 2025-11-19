package com.citrus.admin.di

import android.content.Context
import androidx.room.Room
import com.citrus.admin.data.local.CitrusDatabase
import com.citrus.admin.data.local.dao.ReceiptDao
import com.citrus.admin.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CitrusDatabase {
        return Room.databaseBuilder(
            context,
            CitrusDatabase::class.java,
            "citrus_admin_database"
        ).build()
    }

    @Provides
    fun provideUserDao(database: CitrusDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideReceiptDao(database: CitrusDatabase): ReceiptDao {
        return database.receiptDao()
    }
}
