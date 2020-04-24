package com.pigeoff.library.database

import androidx.room.*

@Database(entities = arrayOf(Book::class, RmList::class), version = 3)
@TypeConverters(RmConverters::class)
abstract class RmDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDAO
    abstract fun listDao(): RmListDAO
}
