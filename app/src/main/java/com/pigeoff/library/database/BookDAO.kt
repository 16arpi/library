package com.pigeoff.library.database

import androidx.room.*

@Dao
interface BookDAO {

    //GET requests
    @Query("SELECT * FROM Book WHERE list != 'wish' ORDER BY title")
    fun getAllBooksFromLibrary(): List<Book>

    @Query("SELECT * FROM Book WHERE list LIKE :list ORDER BY :order")
    fun getAllBooksFromList(list: String, order: String): List<Book>

    @Query("SELECT * FROM Book WHERE id LIKE :id")
    fun getBook(id: Int) : Book

    @Query("SELECT * FROM Book WHERE isBorrowed LIKE 1 ORDER BY borrowedDeadline")
    fun getAllBorrowed(): List<Book>

    @Query("SELECT * FROM Book WHERE tag LIKE '%' || :tag  || '%' ORDER BY title")
    fun getAllFromTag(tag: String): List<Book>

    //POST Requests
    @Insert
    fun addBook(book: Book)

    @Update
    fun updateBook(book: Book)

    @Query("DELETE FROM Book WHERE id LIKE :id")
    fun deleteBook(id: Int)
}
