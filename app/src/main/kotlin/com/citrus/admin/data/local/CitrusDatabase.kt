package com.citrus.admin.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.citrus.admin.data.local.dao.ReceiptDao
import com.citrus.admin.data.local.dao.UserDao
import com.citrus.admin.data.local.entity.ReceiptEntity
import com.citrus.admin.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, ReceiptEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CitrusDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun receiptDao(): ReceiptDao
}
