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
import com.google.android.material.snackbar.Snackbar
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

                    Thread {
                        val newHTTPClient = HTTPClient()
                        page = 0
                        try {
                            volumeQuery = newHTTPClient.searchForVolume(searchText.text.toString(), page)
                        }
                        catch (e: Exception) {
                            volumeQuery = VolumeClass()
                        }

                        if (volumeQuery.items?.count() == null) {
                            /*MaterialAlertDialogBuilder(this)
                                .setMessage(R.string.indicator_no_internet)
                                .setPositiveButton(R.string.dialog_confirm, DialogInterface.OnClickListener { dialog, which ->

                                })
                                .show()*/
                            runOnUiThread {
                                searchText.clearFocus()
                                progressBar.visibility = View.GONE
                                Snackbar.make(findViewById(android.R.id.content), getString(R.string.indicator_no_internet), Snackbar.LENGTH_SHORT).show()
                            }
                            hideKeyboard(this)
                        }
                        else {
                            runOnUiThread {
                                val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
                                recyclerView.layoutManager = LinearLayoutManager(this)
                                recyclerView.adapter = SearchAdapter(this, volumeQuery, listExtra, isBorrowed, tagExtra)
                                progressBar.visibility = View.GONE
                                searchText.clearFocus()
                            }
                            hideKeyboard(this)
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
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == recyclerView.getAdapter()!!.getItemCount() - 1) { //bottom of list!

                        progressBar.visibility = View.VISIBLE
                        isLoading = true
                        Thread {
                            page += 10
                            val newHTTPClient = HTTPClient()
                            var newVolumeQuery = VolumeClass()
                            try {
                                newVolumeQuery = newHTTPClient.searchForVolume(query, page)
                            }
                            catch (e: Exception) {
                                newVolumeQuery = VolumeClass()
                            }

                            if (newVolumeQuery.items?.count() != null) {
                                runOnUiThread {

                                    newVolumeQuery.items!!.forEach {
                                        volumeQuery.items!!.add(it)
                                    }

                                    recyclerView.adapter!!.notifyItemInserted(recyclerView.getAdapter()!!.getItemCount())
                                    recyclerView.scrollToPosition(recyclerView.getAdapter()!!.getItemCount())
                                    isLoading = false
                                    progressBar.visibility = View.GONE
                                }
                            }
                            else {
                                runOnUiThread {
                                    page -= 10
                                    isLoading = false
                                    progressBar.visibility = View.GONE

                                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.indicator_no_internet), Snackbar.LENGTH_SHORT).show()
                                }
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