package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ShadiLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShadiLocationDao {
  @Query("SELECT * FROM shadi_locations ORDER BY timestamp DESC")
  fun getAllShadiLocations(): Flow<List<ShadiLocationEntity>>

  @Query("SELECT * FROM shadi_locations WHERE city = :city ORDER BY timestamp DESC")
  fun getShadiLocationsByCity(city: String): Flow<List<ShadiLocationEntity>>

  @Query("SELECT * FROM shadi_locations WHERE area LIKE '%' || :area || '%' ORDER BY timestamp DESC")
  fun getShadiLocationsByArea(area: String): Flow<List<ShadiLocationEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(shadi: ShadiLocationEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(shadis: List<ShadiLocationEntity>)

  @Query("DELETE FROM shadi_locations WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("SELECT COUNT(*) FROM shadi_locations")
  suspend fun getCount(): Int
}
