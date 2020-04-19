package com.pigeoff.library

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class LibraryDatabase(context: Context, factory: SQLiteDatabase.CursorFactory?)
    : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

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

        val CREATE_PRODUCTS_TABLE = ("CREATE TABLE " +
                TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_GOOGLEID + " TEXT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_SUBTITLE + " TEXT,"
                + COLUMN_AUTHORS + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_PUBLISHER + " TEXT,"
                + COLUMN_CATEGORIES + " TEXT,"
                + COLUMN_NBPAGES + " INT,"
                + COLUMN_COVER_IMAGE + " TEXT,"
                + COLUMN_LOCATION + " TEXT,"
                + COLUMN_NOTES + " TEXT,"
                + COLUMN_MARK + " INT,"
                + COLUMN_LIST + " TEXT"
                + ")")
        db.execSQL(CREATE_PRODUCTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addBookToLibrary(book: BookEntry) {
        val values = ContentValues()
        values.put(COLUMN_GOOGLEID, book.googleId)
        values.put(COLUMN_TITLE, book.title)
        values.put(COLUMN_SUBTITLE, book.subtitle)
        values.put(COLUMN_AUTHORS, book.authors)
        values.put(COLUMN_DATE, book.date)
        values.put(COLUMN_PUBLISHER, book.publisher)
        values.put(COLUMN_CATEGORIES, book.categories)
        values.put(COLUMN_NBPAGES, book.nbPages)
        values.put(COLUMN_COVER_IMAGE, book.coverImage)
        values.put(COLUMN_LOCATION, "")
        values.put(COLUMN_MARK, 0)
        values.put(COLUMN_LIST, book.list)
        values.put(COLUMN_NOTES, "")


        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllBooksFromLibrary(): MutableList<BookEntry>? {
        val allBooks: MutableList<BookEntry> = mutableListOf()

        val db = this.readableDatabase
        /* Alphabetic order */
        var cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_LIST != 'wish' ORDER BY $COLUMN_TITLE", null)

        cursor.moveToFirst()

        var max = cursor.count
        var i = 0
        while (i < max) {
            val singleBook = BookEntry(
                cursor.getInt(cursor.getColumnIndex(LibraryDatabase.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_GOOGLEID)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_SUBTITLE)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_AUTHORS)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_DATE)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_PUBLISHER)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_CATEGORIES)),
                cursor.getInt(cursor.getColumnIndex(LibraryDatabase.COLUMN_NBPAGES)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_COVER_IMAGE)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_LIST)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_LOCATION)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_NOTES)),
                cursor.getInt(cursor.getColumnIndex(LibraryDatabase.COLUMN_MARK))
            )
            allBooks.add(singleBook)
            cursor.moveToNext()
            i++
        }

        db.close()

        return allBooks
    }

    fun getAllBooksFromList(list: String, order: String?): MutableList<BookEntry>? {
        val allBooks: MutableList<BookEntry> = mutableListOf()

        val db = this.readableDatabase

        var cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_LIST = '$list' ORDER BY $order", null)

        cursor.moveToFirst()

        var max = cursor.count
        var i = 0
        while (i < max) {
            val singleBook = BookEntry(
                cursor.getInt(cursor.getColumnIndex(LibraryDatabase.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_GOOGLEID)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_SUBTITLE)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_AUTHORS)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_DATE)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_PUBLISHER)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_CATEGORIES)),
                cursor.getInt(cursor.getColumnIndex(LibraryDatabase.COLUMN_NBPAGES)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_COVER_IMAGE)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_LIST)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_LOCATION)),
                cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_NOTES)),
                cursor.getInt(cursor.getColumnIndex(LibraryDatabase.COLUMN_MARK))
            )
            allBooks.add(singleBook)
            cursor.moveToNext()
            i++
        }

        db.close()

        return allBooks
    }

    fun getBook(condition: Int): BookEntry {
        val allBooks: MutableList<BookEntry> = mutableListOf()

        val db = this.readableDatabase

        Log.i("id", condition.toString())

        var cursor: Cursor
        if (condition != 0) {
            /* Alphabetic order */
            cursor = db.query("$TABLE_NAME", null, "$COLUMN_ID = "+condition, null, null, null, null, null)
        }

        else {
            cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_TITLE = null", null)
        }

        cursor.moveToFirst()
        val singleBook = BookEntry(
            cursor.getInt(cursor.getColumnIndex(LibraryDatabase.COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_GOOGLEID)),
            cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_TITLE)),
            cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_SUBTITLE)),
            cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_AUTHORS)),
            cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_DATE)),
            cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_PUBLISHER)),
            cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_CATEGORIES)),
            cursor.getInt(cursor.getColumnIndex(LibraryDatabase.COLUMN_NBPAGES)),
            cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_COVER_IMAGE)),
            cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_LIST)),
            cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_LOCATION)),
            cursor.getString(cursor.getColumnIndex(LibraryDatabase.COLUMN_NOTES)),
            cursor.getInt(cursor.getColumnIndex(LibraryDatabase.COLUMN_MARK))
        )

        db.close()

        return singleBook
    }

    fun deleteBook(condition: Int?) {
        val db = this.readableDatabase
        if (condition != null) {
            db.execSQL("DELETE FROM $TABLE_NAME WHERE $COLUMN_ID = $condition")
        }

    }

    fun updateBook(emplacement: String?, notes: String?, id: Int?) {
        val db = this.readableDatabase

        var values = ContentValues()
        values.put(COLUMN_LOCATION, emplacement)
        values.put(COLUMN_NOTES, notes)
        db.update(TABLE_NAME, values, "$COLUMN_ID = "+id, null)

    }

    companion object {
        private val DATABASE_VERSION = 1
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
    }

}