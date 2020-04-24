package com.pigeoff.library

import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import cesarferreira.faker.loadFromUrl
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pigeoff.library.database.Book
import com.pigeoff.library.database.RmDatabase
import com.pigeoff.library.database.RmMigration
import kotlinx.android.synthetic.main.main_list.view.*
import java.text.SimpleDateFormat
import java.util.*


class SearchAdapter(private val context: SearchActivity, private val volumesList: VolumeClass, private val listExtra: String, private val isBorrowed: Int, private val tag: String?) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {


    /* CE QUI SE LANCE EN PREMIER : initilisise la classe ViewHolder qui sert de classe principal */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.main_list, parent, false))
    }

    override fun getItemCount(): Int {
        return volumesList.items!!.count()
    }


    /* Action quand un holder est cliqué */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val volume: VolumeClass.Items = volumesList.items!![position]
        val title: String? = volume.volumeInfo!!.title
        if (volume.volumeInfo!!.subtitle != null) {
            title.plus(", "+volume.volumeInfo!!.subtitle)
        }
        holder.textViewTitle.text = title

        Log.i("Extra", listExtra)

        val authorsArray = volume.volumeInfo!!.authors
        val authorsString: String
        if (authorsArray != null) {
            authorsString = authorsArray!!.joinToString()
        }

        else {
            authorsString = "Unknow author"
        }
        holder.textViewAuthor.text = authorsString

        val categoriesArray = volume.volumeInfo!!.categories
        val categories: String?

        if (categoriesArray?.get(0) != null) {
            categories = categoriesArray?.joinToString(" ")
            holder.textViewCategories.text = categories
        }

        else  {
            categories = "No category"
            holder.textViewCategories.text = categories
        }

        var imageCover = if (volume.volumeInfo?.imageLinks?.thumbnail != null) volume.volumeInfo?.imageLinks?.thumbnail else "No COVER"
        if (imageCover!!.contains("http")) {
            holder.imageViewCover.loadFromUrl(imageCover!!, R.color.background, R.color.background)
        }

        else {
            holder.imageViewCover.visibility = View.GONE
        }


        var list: String? = ""

        holder.cardView.setOnClickListener {

            var textTitle = TextView(context)
            textTitle.setText(context.getString(R.string.borrowed_deadline_indicator_message))
            var px = 32
            textTitle.setPadding(
                px*context.getResources().getDisplayMetrics().density.toInt(),
                24*context.getResources().getDisplayMetrics().density.toInt(),
                px*context.getResources().getDisplayMetrics().density.toInt(),
                24*context.getResources().getDisplayMetrics().density.toInt())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textTitle.setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline6)
            }
            var datePicker = DatePicker(context)
            var view = LinearLayout(context)
            view.orientation = LinearLayout.VERTICAL
            view.addView(textTitle)
            view.addView(datePicker)

            if (isBorrowed == 1) {
                MaterialAlertDialogBuilder(context)
                    .setMessage(R.string.action_add_question)
                    .setNegativeButton(context.getString(R.string.delete_dialog_cancel)) { dialog, which ->
                        //DO Nothing
                    }
                    .setPositiveButton(context.getString(R.string.dialog_confirm)) { dialog, which ->
                        //Date picker
                        MaterialAlertDialogBuilder(context)
                            .setView(view)
                            .setNegativeButton(context.getString(R.string.delete_dialog_cancel)) { dialog, which ->
                                //DO Nothing
                            }
                            .setPositiveButton(context.getString(R.string.dialog_confirm)) { dialog, which ->
                                val month = datePicker.month+1
                                val dateComposer = datePicker.year.toString()+"/"+month.toString()+"/"+datePicker.dayOfMonth.toString()
                                Log.i("Date avant", dateComposer)
                                val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                                val timestamp = dateFormat.parse(dateComposer).time
                                Log.i("Date après", timestamp.toString())


                                val newBook = Book()
                                newBook.googleId = volume.id
                                newBook.title = volume.volumeInfo?.title
                                newBook.subtitle = volume.volumeInfo?.subtitle
                                newBook.authors = volume.volumeInfo?.authors
                                newBook.date = volume.volumeInfo?.publishedDate
                                newBook.publisher = volume.volumeInfo?.publisher
                                newBook.categories = volume.volumeInfo?.categories
                                newBook.nbPages = volume.volumeInfo?.pageCount
                                newBook.coverImage = volume.volumeInfo?.imageLinks?.thumbnail
                                newBook.list = listExtra
                                newBook.isBorrowed = 1
                                newBook.borrowedDeadline = timestamp
                                if (tag != null && tag.isNotEmpty()) newBook.tag = mutableListOf(tag)


                                var database = Room.databaseBuilder(
                                    context,
                                    RmDatabase::class.java, "librarydb"
                                ).allowMainThreadQueries().addMigrations(RmMigration().MIGRATION_2_3).build()

                                database.close()

                                database.bookDao().addBook(newBook)

                                context.setResult(AppCompatActivity.RESULT_OK)

                                context.finish()
                            }
                            .show()
                    }
                    .show()


            }


            else {
                //Date Picker
                MaterialAlertDialogBuilder(context)
                    .setMessage(R.string.action_add_question)
                    .setNegativeButton(context.getString(R.string.delete_dialog_cancel)) { dialog, which ->
                        //DO Nothing
                    }
                    .setPositiveButton(context.getString(R.string.dialog_confirm)) { dialog, which ->
                        val newBook = Book()
                        newBook.googleId = volume.id
                        newBook.title = volume.volumeInfo?.title
                        newBook.subtitle = volume.volumeInfo?.subtitle
                        newBook.authors = volume.volumeInfo?.authors
                        newBook.date = volume.volumeInfo?.publishedDate
                        newBook.publisher = volume.volumeInfo?.publisher
                        newBook.categories = volume.volumeInfo?.categories
                        newBook.nbPages = volume.volumeInfo?.pageCount
                        newBook.coverImage = volume.volumeInfo?.imageLinks?.thumbnail
                        newBook.list = listExtra
                        if (tag != null && tag.isNotEmpty()) newBook.tag = mutableListOf(tag)


                        var database = Room.databaseBuilder(
                            context,
                            RmDatabase::class.java, "librarydb"
                        ).allowMainThreadQueries().build()

                        database.bookDao().addBook(newBook)

                        context.setResult(AppCompatActivity.RESULT_OK)
                        context.finish()
                    }
                    .show()
            }
        }



    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView = view.cardView
        val textViewCategories = view.textViewCategories
        val textViewTitle = view.textViewTitle
        val textViewAuthor = view.textViewAuthor
        val textViewOther = view.textViewOther
        val imageViewCover = view.imageViewCover

    }

    fun dp(px: Int): Int {
        val density = Resources.getSystem()
            .displayMetrics.density
        return (px * density).toInt()
    }
}