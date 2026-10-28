package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FavoriteEntity
import com.example.data.model.RoomListingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomListingDao {
  @Query("SELECT * FROM room_listings ORDER BY createdAt DESC")
  fun getAllListings(): Flow<List<RoomListingEntity>>

  @Query("SELECT * FROM room_listings WHERE ownerId = :ownerId ORDER BY createdAt DESC")
  fun getOwnerListings(ownerId: String): Flow<List<RoomListingEntity>>

  @Query("SELECT * FROM room_listings WHERE id = :id LIMIT 1")
  suspend fun getListingById(id: Long): RoomListingEntity?

  @Query("SELECT COUNT(*) FROM room_listings")
  suspend fun getCount(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertListing(listing: RoomListingEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(listings: List<RoomListingEntity>)

  @Update
  suspend fun updateListing(listing: RoomListingEntity)

  @Query("UPDATE room_listings SET isVacant = :isVacant WHERE id = :id")
  suspend fun updateVacancy(id: Long, isVacant: Boolean)

  @Query("UPDATE room_listings SET rentPerMonth = :newRent WHERE id = :id")
  suspend fun updateRent(id: Long, newRent: Int)

  @Delete
  suspend fun deleteListing(listing: RoomListingEntity)

  @Query("DELETE FROM room_listings WHERE id = :id")
  suspend fun deleteListingById(id: Long)

  // Favorites
  @Query("SELECT r.* FROM room_listings r INNER JOIN favorites f ON r.id = f.roomId WHERE f.userId = :userId ORDER BY r.createdAt DESC")
  fun getFavorites(userId: String): Flow<List<RoomListingEntity>>

  @Query("SELECT roomId FROM favorites WHERE userId = :userId")
  fun getFavoriteRoomIds(userId: String): Flow<List<Long>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addFavorite(favorite: FavoriteEntity)

  @Query("DELETE FROM favorites WHERE roomId = :roomId AND userId = :userId")
  suspend fun removeFavorite(roomId: Long, userId: String)
}
