package com.pigeoff.library.database

import androidx.room.*

@Dao
interface RmListDAO {

    //GET requests
    @Query("SELECT * FROM RmList")
    fun getAllList(): List<RmList>

    //POST Requests
    @Insert
    fun addList(list: RmList)

    @Update
    fun updateList(list: RmList)

    @Delete
    fun deleteList(list: RmList)
}