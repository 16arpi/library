package com.pigeoff.library

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import cesarferreira.faker.loadFromUrl
import kotlinx.android.synthetic.main.main_list.view.*


class SearchAdapter(private val context: SearchActivity, private val volumesList: VolumeClass, private val listExtra: String) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    var RESULT_SEARCH_CODE = 1871


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

        /*val colorGenerator: ColorGenerator = ColorGenerator.MATERIAL
        val color: Int = colorGenerator.getColor(volume.volumeInfo!!.title)
        val firstletter: String = authorsString!!.substring(0, 1)
        val textDrawable: TextDrawable = TextDrawable.builder().buildRound(firstletter, color)
        holder.imageView.setImageDrawable(textDrawable)*/

        var list: String? = ""

        holder.cardView.setOnClickListener {


            val newBook = LibraryDatabase.BookEntry(
                if (volume.id != null) volume.id else "",
                if (volume.volumeInfo!!.title != null) volume.volumeInfo!!.title else "",
                if (volume.volumeInfo!!.subtitle != null) volume.volumeInfo!!.subtitle else "",
                authorsString,
                if (volume.volumeInfo!!.publishedDate != null) volume.volumeInfo!!.publishedDate else "",
                if (volume.volumeInfo!!.publisher != null) volume!!.volumeInfo!!.publisher else "",
                categories,
                if (volume.volumeInfo!!.pageCount != null) volume.volumeInfo!!.pageCount else 0,
                if (volume.volumeInfo!!.imageLinks?.thumbnail != null) volume.volumeInfo!!.imageLinks?.thumbnail else "",
                listExtra,
                0

            )

                val database = LibraryDatabase(context, null)
                database.addBookToLibrary(newBook)
                database.close()

                context.setResult(AppCompatActivity.RESULT_OK)
                context.finish()
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
}