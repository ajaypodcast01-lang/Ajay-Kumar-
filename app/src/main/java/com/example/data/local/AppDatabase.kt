package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.ChatMessageEntity
import com.example.data.model.FavoriteEntity
import com.example.data.model.RentReceiptEntity
import com.example.data.model.RoomListingEntity
import com.example.data.model.ShadiLocationEntity
import com.example.data.model.UserEntity

@Database(
  entities = [
    UserEntity::class,
    RoomListingEntity::class,
    FavoriteEntity::class,
    ChatMessageEntity::class,
    RentReceiptEntity::class,
    ShadiLocationEntity::class
  ],
  version = 6,
  exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
  abstract fun roomListingDao(): RoomListingDao
  abstract fun chatMessageDao(): ChatMessageDao
  abstract fun rentReceiptDao(): RentReceiptDao
  abstract fun shadiLocationDao(): ShadiLocationDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "mairul_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
