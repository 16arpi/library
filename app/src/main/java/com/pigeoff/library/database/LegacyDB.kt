package com.pigeoff.library.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.room.Room
import kotlin.time.milliseconds

class LegacyDB(context: Context, factory: SQLiteDatabase.CursorFactory?)
    : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    var oldVersion = 1
    var newVersion = 2
    var context: Context = context

    class BookEntry {
        var id: Int? = 0
        var googleId: String? = null
        var title: String? = null
        var subtitle: String? = null
        var authors: String? = null
        var date: String? = null
        var publisher: String? = null
        var categories: String? = null
        var nbPages: Int? = null
        var coverImage: String? = null
        var list: String? = null
        var location: String? = null
        var notes: String? = null
        var mark: Int? = 0

        constructor(
            id: Int?,
            googleId: String?,
            title: String?,
            subtitle: String?,
            authors: String?,
            date: String?,
            publisher: String?,
            categories: String?,
            nbPages: Int?,
            coverImage: String?,
            list: String?,
            location: String?,
            notes: String?,
            mark: Int?
        ) {
            this.id = id
            this.googleId = googleId
            this.title = title
            this.subtitle = subtitle
            this.authors = authors
            this.date = date
            this.publisher = publisher
            this.categories = categories
            this.nbPages = nbPages
            this.coverImage = coverImage
            this.list = list
            this.location = location
            this.notes = notes
            this.mark = mark
        }

        constructor(
            googleId: String?,
            title: String?,
            subtitle: String?,
            authors: String?,
            date: String?,
            publisher: String?,
            categories: String?,
            nbPages: Int?,
            coverImage: String?,
            list: String?,
            mark: Int?
        ) {
            this.googleId = googleId
            this.title = title
            this.subtitle = subtitle
            this.authors = authors
            this.date = date
            this.publisher = publisher
            this.categories = categories
            this.nbPages = nbPages
            this.coverImage = coverImage
            this.list = list
            this.mark = mark
        }


    }

    override fun onCreate(db: SQLiteDatabase) {
        onUpgrade(db, 1, 2)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {


    }

    fun migrateDatabse(context: Context) {

        var db = this.writableDatabase

        var newDatabase = Room.databaseBuilder(
            context,
            RmDatabase::class.java, "librarydb"
        ).allowMainThreadQueries().addMigrations(RmMigration().MIGRATION_2_3).build()

        var cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_ID", null)

        if (cursor.count > 0 && cursor != null) {
            cursor.moveToFirst()
            var max = cursor.count
            var i = 0
            while (i < max) {
                val oldBook = BookEntry(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_GOOGLEID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_SUBTITLE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_AUTHORS)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PUBLISHER)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORIES)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_NBPAGES)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_COVER_IMAGE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_LIST)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NOTES)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_MARK))
                )

                var newBook = Book()
                newBook.googleId = oldBook.googleId
                newBook.title = oldBook.title
                newBook.subtitle = oldBook.subtitle
                newBook.date = oldBook.date
                newBook.publisher = oldBook.publisher
                newBook.nbPages = oldBook.nbPages
                newBook.coverImage = oldBook.coverImage
                newBook.list = oldBook.list
                newBook.location = oldBook.location
                newBook.notes = oldBook.notes
                newBook.mark = if (oldBook.mark == null) 0 else oldBook.mark!!

                Log.i("Authors 1", oldBook.authors.isNullOrEmpty().toString())
                Log.i("Author 2", oldBook.authors!!.toString().contains(",").toString())
                Log.i("Author 3", mutableListOf<String>(oldBook.authors!!).toString())
                //For authors, categories
                if (!oldBook.authors.isNullOrEmpty()) {
                        newBook.authors = oldBook.authors?.split(",")?.toMutableList()
                }


                if (!oldBook.categories.isNullOrEmpty()) {
                        newBook.categories = oldBook.categories?.split(",")?.toMutableList()
                }
                Log.i("Author4", newBook.authors.toString())

                newDatabase.bookDao().addBook(newBook)
                cursor.moveToNext()
                i++
            }

            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")

            db.close()
        }
    }


    companion object {
        private val DATABASE_VERSION = 2
        private val DATABASE_NAME = "database.db"

        val TABLE_NAME = "books"
        val COLUMN_ID = "id"
        val COLUMN_GOOGLEID = "googleid"
        val COLUMN_TITLE = "title"
        val COLUMN_SUBTITLE = "subtitle"
        val COLUMN_AUTHORS = "authors"
        val COLUMN_DATE = "published_date"
        val COLUMN_PUBLISHER = "publisher"
        val COLUMN_CATEGORIES = "categories"
        val COLUMN_NBPAGES = "nb_pages"
        val COLUMN_COVER_IMAGE = "cover_image"
        val COLUMN_LOCATION = "emplacement"
        val COLUMN_NOTES = "notes"
        val COLUMN_MARK = "mark"
        val COLUMN_LIST = "list"
        val COLUMN_IS_BORROWED = "isborrowed"
        val COLUMN_BORROWED_DEADLINE = "borroweddeadline"

        val TABLE_LIST = "lists"
    }

}