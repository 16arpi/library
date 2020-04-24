package com.pigeoff.library

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.room.Room
import cesarferreira.faker.loadFromUrl
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pigeoff.library.database.Book
import com.pigeoff.library.database.RmDatabase
import com.pigeoff.library.database.RmMigration
import com.tylersuehr.chips.ChipDataSource
import com.tylersuehr.chips.ChipsInputLayout
import kotlinx.android.synthetic.main.activity_book_detail.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class BookDetailActivity : AppCompatActivity() {

    var database: RmDatabase? = null
    var book = Book()
    var borrowedDateGlobal: String? = null
    var allTags: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        var bookId = intent.getIntExtra("id", 0)
        allTags = intent.getStringArrayListExtra("allTags")
        database = Room.databaseBuilder(
            applicationContext,
            RmDatabase::class.java, "librarydb"
        ).allowMainThreadQueries().addMigrations(RmMigration().MIGRATION_2_3).build()
        book = database!!.bookDao().getBook(bookId)

        /* Configuration de la Toolbar */
        var toolbar: Toolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.detail_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        book.id = book?.id


        /* Config des views */
        var toolBarText: TextView = findViewById(R.id.toolBarText)
        var textAuthor: TextView = findViewById(R.id.textViewDetailAuthor)
        var textMore: TextView = findViewById(R.id.textViewDetailMore)
        var imageCover: ImageView = findViewById(R.id.imageViewDetailCover)
        var chipCategories: ChipGroup = findViewById(R.id.chipGroupCategories)
        var editLocation: TextInputEditText = findViewById(R.id.editTextLocation)
        var ratingBar: RatingBar = findViewById(R.id.ratingBar)
        var editNotes: TextInputEditText = findViewById(R.id.editTextNotes)
        var editBorrowedDeadline: TextInputEditText = findViewById(R.id.editTextBorrowedDeadline)


        editLocation.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                book.location = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {}
        })

        editNotes.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                book.notes = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {}
        })

        // Config des texts faciles
        toolBarText.text = book?.title
        if (book?.authors != null) textAuthor.text = TextUtils.join(", ", book?.authors!!)
        else textAuthor.visibility = View.GONE

        //Config de la notation
        ratingBar.rating = book?.mark!!.toFloat()

        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            ratingBar.rating = rating
            book.mark = rating.toInt()
        }

        if (book?.publisher != "") {
            if (book?.date != "") {
                var nb_pages = ""
                if (book?.nbPages != 0) nb_pages = " - "+book?.nbPages.toString()+" "+getString(R.string.pages_nocap)
                textMore.text = book?.publisher+", "+book?.date.toString().substring(0, 4)+nb_pages
            }
            else {
                var nb_pages = ""
                if (book?.nbPages != 0) nb_pages = " - "+book?.nbPages.toString()+" "+getString(R.string.pages_nocap)
                textMore.text = book?.publisher+nb_pages
            }
        }

        else {
            if (book?.date != "") {
                var nb_pages = ""
                if (book?.nbPages != 0) nb_pages = " - "+book?.nbPages.toString()+" "+getString(R.string.pages_nocap)
                textMore.text = getString(R.string.published_in)+" "+book?.date.toString().substring(0, 4)+nb_pages
            }
            else {
                var nb_pages = ""
                if (book?.nbPages != 0) textMore.text = book?.nbPages.toString()+" "+getString(R.string.pages_nocap)
                else textMore.visibility = View.GONE
            }
        }


        if (book?.coverImage == null) book?.coverImage = ""
        if (imageCover != null) imageCover.loadFromUrl(book?.coverImage!!, R.color.transparent, R.color.transparent)

        editLocation.setText(book?.location)
        editNotes.setText(book?.notes)

        //Config borrowed edittext
        if (book?.isBorrowed == 1) {
            var editLayout: TextInputLayout = findViewById(R.id.textInputLayoutBorrowed)
            editLayout.visibility = View.VISIBLE
            var dateText = Date(book?.borrowedDeadline!!)
            var format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            editBorrowedDeadline.setText(format.format(dateText).toString())
            borrowedDateGlobal = editBorrowedDeadline.text.toString()
        }


        editBorrowedDeadline.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                borrowedDateGlobal = editBorrowedDeadline.text.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}
        })


        //Config categories
        if (book?.categories != null) {
            var categories = book?.categories!!
            for (element in categories) {
                var chip = Chip(this)
                chip.setText(element)
                chipCategories.addView(chip)
            }
        }

        //Config Tags
        var chipGroupTag: ChipsInputLayout = findViewById(R.id.chipGroupTags)
        var preListChip: MutableList<CustomChip>? = mutableListOf()
        chipGroupTag.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

        if (allTags != null) {
            if (allTags!!.isNotEmpty()) {
                var filterChips = mutableListOf<CustomChip>()
                for (element in allTags!!) {
                    filterChips.add(CustomChip(element))
                }
                chipGroupTag.setFilterableChipList(filterChips)
            }
        }

        if (book?.tag != null && book?.tag?.size != 0) {
            for (element in book?.tag!!) {
                if (element != null && element != "") {
                    var chip = CustomChip(element)
                    preListChip?.add(chip)
                }
            }
            chipGroupTag.setSelectedChipList(preListChip)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                if (book.isBorrowed == 1) {
                    val dateComposer = borrowedDateGlobal.toString()
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val timestamp = dateFormat.parse(dateComposer).time

                    book.borrowedDeadline = timestamp
                }

                var chipSource: ChipDataSource = chipGroupTags.chipDataSource
                var allChips = chipSource.selectedChips

                book?.tag = mutableListOf()
                for (element in allChips) {
                    if (element.title != null && element.title != "")
                        if (book.tag != null) book.tag!!.add(element.title)
                }

                database!!.bookDao().updateBook(book)
                this.finish()
            }
            R.id.action_delete -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.delete_dialog_title)
                    .setMessage(R.string.delete_dialog_question)
                    .setNegativeButton(getString(R.string.delete_dialog_cancel)) { dialog, which ->
                        Log.i("Action", "Negative")
                    }
                    .setPositiveButton(getString(R.string.delete_dialog_submit)) { dialog, which ->
                        Log.i("Action", "Positive")
                        database!!.bookDao().deleteBook(book.id)
                        finish()
                    }
                    .show()
            }
            R.id.action_save -> {
                if (book.isBorrowed == 1) {
                    val dateComposer = borrowedDateGlobal.toString()
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val timestamp = dateFormat.parse(dateComposer).time

                    book.borrowedDeadline = timestamp
                }

                var chipSource: ChipDataSource = chipGroupTags.chipDataSource
                var allChips = chipSource.selectedChips

                book?.tag = mutableListOf()
                for (element in allChips) {
                    if (element.title != null && element.title != "")
                        if (book.tag != null) book.tag!!.add(element.title)
                }

                database!!.bookDao().updateBook(book)
                Snackbar.make(window.findViewById(android.R.id.content), R.string.saved_book, Snackbar.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (book.isBorrowed == 1) {
                val dateComposer = borrowedDateGlobal.toString()
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val timestamp = dateFormat.parse(dateComposer).time

                book.borrowedDeadline = timestamp
            }

            var chipSource: ChipDataSource = chipGroupTags.chipDataSource
            var allChips = chipSource.selectedChips

            book?.tag = mutableListOf()
            for (element in allChips) {
                if (element.title != null && element.title != "")
                if (book.tag != null) book.tag!!.add(element.title)
            }

            database!!.bookDao().updateBook(book)
            this.finish()
        }
        return super.onKeyUp(keyCode, event)
    }

    private class CustomChip : com.tylersuehr.chips.Chip {

        var customTitle: String? = null
        var customAvatar: Drawable? = null
        var customAvatarUi: Uri? = null
        var customSubtitle: String? = null
        var customId: Any? = null

        constructor(customTitle: String) {
            this.customTitle = customTitle
        }

        override fun getTitle() : String {
            return customTitle!!
        }

        fun setTitle(title: String?) {
            this.customTitle = title
        }

        override fun getSubtitle(): String? {
            return customSubtitle
        }

        fun setSubtitle(sub: String?) {
            this.customSubtitle = sub
        }

        override fun getAvatarDrawable(): Drawable? {
            return customAvatar
        }

        fun setAvatarDrawable(dr: Drawable?) {
            this.customAvatar = dr
        }

        override fun getAvatarUri(): Uri? {
            return customAvatarUi
        }

        fun setAvatarUri(uri: Uri?) {
            this.customAvatarUi = uri
        }

        override fun getId(): Any? {
            return customId
        }

        fun setId(id: Int?) {
            this.customId = id
        }
    }

}
