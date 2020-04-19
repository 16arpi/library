package com.pigeoff.library

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import cesarferreira.faker.loadFromUrl
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText


class BookDetailActivity : AppCompatActivity() {

    var id: Int? = 0
    var notesGlobal: String? = ""
    var locationGlobal: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        var googleid = intent.getIntExtra("id", 0)

        val database = LibraryDatabase(this, null)
        val content = database.getBook(googleid)
        database.close()

        var book = content

        /* Configuration de la Toolbar */
        var toolbar: Toolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.detail_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        id = book?.id


        /* Config des views */
        var toolBarText: TextView = findViewById(R.id.toolBarText)
        var textAuthor: TextView = findViewById(R.id.textViewDetailAuthor)
        var textMore: TextView = findViewById(R.id.textViewDetailMore)
        var imageCover: ImageView = findViewById(R.id.imageViewDetailCover)
        var chipCategories: ChipGroup = findViewById(R.id.chipGroupCategories)
        var editLocation: TextInputEditText = findViewById(R.id.editTextLocation)
        var editNotes: TextInputEditText = findViewById(R.id.editTextNotes)


        editLocation.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                locationGlobal = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {}
        })

        editNotes.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                notesGlobal = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {}
        })

        // Config des texts faciles
        toolBarText.text = book?.title
        textAuthor.text = book?.authors?.replace(",",", ")

        if (book?.publisher != "") {
            if (book?.date != "") {
                var nb_pages = ""

                if (book?.nbPages != 0) {
                    nb_pages = " - "+book?.nbPages.toString()+" "+getString(R.string.pages_nocap)
                }

                textMore.text = book?.publisher+", "+book?.date.toString().substring(0, 4)+nb_pages
            }
            else {
                var nb_pages = ""

                if (book?.nbPages != 0) {
                    nb_pages = " - "+book?.nbPages.toString()+" "+getString(R.string.pages_nocap)
                }

                textMore.text = book?.publisher+nb_pages
            }
        }

        else {
            if (book?.date != "") {
                var nb_pages = ""

                if (book?.nbPages != 0) {
                    nb_pages = " - "+book?.nbPages.toString()+" "+getString(R.string.pages_nocap)
                }

                textMore.text = getString(R.string.published_in)+" "+book?.date.toString().substring(0, 4)+nb_pages
            }
            else {
                var nb_pages = ""

                if (book?.nbPages != 0) {
                    textMore.text = book?.nbPages.toString()+" "+getString(R.string.pages_nocap)
                }

                else {
                    textMore.visibility = View.GONE
                }
            }
        }


        imageCover.loadFromUrl(book?.coverImage!!, R.color.transparent, R.color.transparent)

        editLocation.setText(book?.location)
        editNotes.setText(book?.notes)


        //Config categories
        if (book?.categories != "No category") {
            var categories: Array<String> = book?.categories!!.split(",").toTypedArray()
            for (element in categories) {
                var chip: Chip = Chip(this)
                chip.setText(element)
                chipCategories.addView(chip)
            }
        }



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                Log.i("Finish by saving", locationGlobal+notesGlobal+id.toString())
                var database = LibraryDatabase(this, null)
                database.updateBook(locationGlobal, notesGlobal, id)
                database.close()
                this.finish()
            }
            R.id.action_delete -> {
                var database = LibraryDatabase(this, null)
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.delete_dialog_title)
                    .setMessage(R.string.delete_dialog_question)
                    .setNegativeButton(getString(R.string.delete_dialog_cancel)) { dialog, which ->
                        Log.i("Action", "Negative")
                    }
                    .setPositiveButton(getString(R.string.delete_dialog_submit)) { dialog, which ->
                        Log.i("Action", "Positive")
                        database.deleteBook(id)
                        finish()
                    }
                    .show()
            }
            R.id.action_save -> {
                Log.i("Infos 2", locationGlobal+notesGlobal+id.toString())
                var database = LibraryDatabase(this, null)
                database.updateBook(locationGlobal, notesGlobal, id)
                database.close()
                Snackbar.make(window.findViewById(android.R.id.content), R.string.saved_book, Snackbar.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
