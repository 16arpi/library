package com.pigeoff.library

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity() {

    var isLoading: Boolean = false
    var page: Int = 0
    var query: String = ""
    var volumeQuery: VolumeClass = VolumeClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        var listExtra = intent.getStringExtra("list")
        var isBorrowed = intent.getIntExtra("isBorrowed", 0)
        var tagExtra: String? = intent.getStringExtra("tag")

        val searchText: EditText = findViewById(R.id.editText)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.isIndeterminate = true

        searchText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                query = searchText.text.toString()
                if (query.isNotEmpty()) {
                    progressBar.visibility = View.VISIBLE
                    searchText.clearFocus()
                    hideKeyboard(this)
                    Thread {

                        val newHTTPClient = HTTPClient()
                        page = 0
                        volumeQuery = newHTTPClient.searchForVolume(searchText.text.toString(), page)

                        runOnUiThread {
                            val recyclerView: RecyclerView =
                                findViewById<RecyclerView>(R.id.recyclerView)
                            recyclerView.layoutManager = LinearLayoutManager(this)
                            recyclerView.adapter = SearchAdapter(this, volumeQuery, listExtra, isBorrowed, tagExtra)
                            progressBar.visibility = View.GONE


                        }
                    }.start()


                    return@OnKeyListener true
                }

            }
            false
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == recyclerView.getAdapter()!!.getItemCount() - 1) { //bottom of list!

                        progressBar.visibility = View.VISIBLE
                        isLoading = true
                        Thread {
                            page += 10
                            val newHTTPClient = HTTPClient()
                            val newVolumeQuery: VolumeClass = newHTTPClient.searchForVolume(query, page)

                            runOnUiThread {

                                newVolumeQuery.items!!.forEach {
                                    volumeQuery.items!!.add(it)
                                }

                                recyclerView.adapter!!.notifyItemInserted(recyclerView.getAdapter()!!.getItemCount())
                                recyclerView.scrollToPosition(recyclerView.getAdapter()!!.getItemCount())
                                isLoading = false
                                progressBar.visibility = View.GONE
                            }
                        }.start()
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

        }
        return super.onOptionsItemSelected(item)
    }

    fun doMySearch(query: String) {

    }

    fun hideKeyboard(activity: Activity) {
        val view =
            activity.findViewById<View>(android.R.id.content)
        if (view != null) {
            val imm: InputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}