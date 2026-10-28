package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.RentReceiptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RentReceiptDao {
  @Query("SELECT * FROM rent_receipts ORDER BY timestamp DESC")
  fun getAllReceipts(): Flow<List<RentReceiptEntity>>

  @Query("SELECT * FROM rent_receipts WHERE senderPhone = :phone OR receiverPhone = :phone ORDER BY timestamp DESC")
  fun getReceiptsForUser(phone: String): Flow<List<RentReceiptEntity>>

  @Query("SELECT * FROM rent_receipts WHERE receiptType = :type ORDER BY timestamp DESC")
  fun getReceiptsByType(type: String): Flow<List<RentReceiptEntity>>

  @Query("SELECT * FROM rent_receipts WHERE id = :id LIMIT 1")
  suspend fun getReceiptById(id: Long): RentReceiptEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReceipt(receipt: RentReceiptEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(receipts: List<RentReceiptEntity>)

  @Query("DELETE FROM rent_receipts WHERE id = :id")
  suspend fun deleteReceipt(id: Long)

  @Query("SELECT COUNT(*) FROM rent_receipts")
  suspend fun getCount(): Int
}
