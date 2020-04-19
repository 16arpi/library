package com.pigeoff.library

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cesarferreira.faker.loadFromUrl
import kotlinx.android.synthetic.main.main_list.view.*
import kotlinx.android.synthetic.main.recycler_header.view.*


class MainAdapter(private val context: MainActivity, private val volumesList: MutableList<LibraryDatabase.BookEntry>?, private var listGlobal: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var TYPE_HEADER: Int = 0
    var TYPE_ITEM: Int = 1
    var LIST_GLOBAL: String = ""
    var RESULT_SEARCH_CODE: Int = 1871

    /* CE QUI SE LANCE EN PREMIER : initilisise la classe ViewHolder qui sert de classe principal */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        this.LIST_GLOBAL = listGlobal

        if (viewType == TYPE_HEADER) {
            return HeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_header, parent, false))
        }

        else if (viewType == TYPE_ITEM) {
            return ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.main_list, parent, false))
        }
        else return ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.main_list, parent, false))
    }

    override fun getItemCount(): Int {
        return volumesList!!.count()+1
    }


    /* Action quand un holder est cliqué */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is HeaderViewHolder) {
            var headHolder = holder as HeaderViewHolder
            var nbBooks = itemCount-1

            if (itemCount == 1) {
                headHolder.noBookView.visibility = View.VISIBLE
                headHolder.textHeader.visibility = View.GONE

                headHolder.addBook.setOnClickListener {
                    val intent = Intent(context, SearchActivity::class.java)
                    intent.putExtra("list", LIST_GLOBAL)
                    context.startActivityForResult(intent, RESULT_SEARCH_CODE)
                }
            }

            else if (itemCount == 2) {
                headHolder.textHeader.text = context.getString(R.string.nb_books_single)
            }

            else {
                headHolder.textHeader.text = nbBooks.toString()+" "+context.getString(R.string.nb_books)
            }
        }

        else if (holder is ItemViewHolder) {
            var itemHolder = holder as ItemViewHolder
            Log.i("POSITION : ", position.toString())

            var book = volumesList?.get(position - 1)

            itemHolder.textViewTitle.text = book?.title
            itemHolder.textViewAuthor.text = book?.authors?.replace(",", ", ")
            itemHolder.textViewCategories.text = book?.categories?.replace(",", "  ")?.capitalize()
            itemHolder.textViewOther.text = book?.publisher+", "+book?.date

            var imageCover = if (book?.coverImage != null) book.coverImage else "No COVER"
            if (imageCover!!.contains("http")) {
                itemHolder.imageViewCover.loadFromUrl(imageCover!!, R.color.background, R.color.background)
            }

            itemHolder.cardView.setOnClickListener {
                val intent = Intent(context, BookDetailActivity::class.java)
                intent.putExtra("id", book?.id)
                context.startActivity(intent)
            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_HEADER
        } else TYPE_ITEM
    }


    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textHeader = view.textRecyclerHeader
        val noBookView = view.linearLayoutHeaderList
        val addBook = view.buttonHeaderList
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView = view.cardView
        val textViewCategories = view.textViewCategories
        val textViewTitle = view.textViewTitle
        val textViewAuthor = view.textViewAuthor
        val textViewOther = view.textViewOther
        val imageViewCover = view.imageViewCover

    }

}