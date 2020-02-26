package com.basemvp.util.room

import androidx.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(user: User): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>): List<Long>

    //---------------------------------------------
    @Delete
    suspend fun deleteUsers(user: User): Int

    @Delete
    suspend fun deleteUsers(users: List<User>): Int

    @Query("DELETE FROM User WHERE id=:id")
    suspend fun deleteUsers(id: Int): Int

    //---------------------------------------------
    @Update
    suspend fun updateUsers(user: User): Int

    @Update
    suspend fun updateUsers(users: List<User>): Int

    @Query("UPDATE User SET name=:name WHERE id=:id")
    suspend fun updateName(name: String, id: Int): Int

    //---------------------------------------------
    @Query("SELECT * FROM User")
    suspend fun getAllUsers(): List<User>
}