package com.pigeoff.library

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    var navigationView: NavigationView? = null
    var drawerLayout: DrawerLayout? = null
    var listGlobal: String? = ""

    var RESULT_SEARCH_CODE = 1871

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_action_menu)
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setTitle(R.string.title_main)


        val database = LibraryDatabase(this, null)
        val content = database.getAllBooksFromLibrary()
        database.close()

        var recyclerView: RecyclerView = findViewById(R.id.recyclerViewMain)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MainAdapter(this, content, listGlobal!!)

        var floating: FloatingActionButton = findViewById(R.id.floatinButton)
        floating.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra("list", listGlobal)
            startActivityForResult(intent, RESULT_SEARCH_CODE)
        }

        /* HANDLE NAVIGATION */
        navigationView = findViewById(R.id.navigationView)
        drawerLayout = findViewById(R.id.drawerLayout)

        navigationView!!.bringToFront();
        var homeItem = navigationView!!.getMenu().findItem(R.id.nav_item_home)
        homeItem.isChecked = true

        navigationView!!.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_item_home -> {
                    if (it.isChecked) {
                        Log.i("Info", "HOME CLICK FALSE")
                        false
                    }

                    else {
                        Log.i("Info", "HOME CLICK TRUE")
                        listGlobal = ""
                        val newdatabase = LibraryDatabase(this, null)
                        val newcontent = newdatabase.getAllBooksFromLibrary()
                        newdatabase.close()

                        var newrecyclerView: RecyclerView = findViewById(R.id.recyclerViewMain)
                        newrecyclerView.layoutManager = LinearLayoutManager(this)
                        newrecyclerView.adapter = MainAdapter(this, newcontent, listGlobal!!)
                        it.isChecked = true

                        var collapseToolbar: CollapsingToolbarLayout = findViewById(R.id.collapseToolbar)
                        collapseToolbar.setTitle(getString(R.string.title_main))

                        drawerLayout!!.closeDrawer(GravityCompat.START)
                    }
                }

                R.id.nav_item_wishes -> {
                    if (it.isChecked) {
                        Log.i("Info", "WISH CLICK FALSE")
                        false
                    }

                    else {
                        Log.i("Info", "WISH CLICK TRUE")
                        var list = "wish"
                        listGlobal = "wish"
                        var order = LibraryDatabase.COLUMN_ID+" DESC"
                        val newdatabase = LibraryDatabase(this, null)
                        val newcontent = newdatabase.getAllBooksFromList(list, order)
                        newdatabase.close()

                        var newrecyclerView: RecyclerView = findViewById(R.id.recyclerViewMain)
                        newrecyclerView.layoutManager = LinearLayoutManager(this)
                        newrecyclerView.adapter = MainAdapter(this, newcontent, listGlobal!!)
                        it.isChecked = true

                        var collapseToolbar: CollapsingToolbarLayout = findViewById(R.id.collapseToolbar)
                        collapseToolbar.setTitle(getString(R.string.title_wish))

                        drawerLayout!!.closeDrawer(GravityCompat.START)
                    }
                }
            }
            true
        }

        navigationView!!.bringToFront();

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == RESULT_SEARCH_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Snackbar.make(this.findViewById(android.R.id.content), R.string.add_book_alert, Snackbar.LENGTH_SHORT).show()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()

        var item = navigationView!!.checkedItem

        when(item?.itemId) {
            R.id.nav_item_home -> {
                listGlobal = ""
                val database = LibraryDatabase(this, null)
                val content = database.getAllBooksFromLibrary()
                database.close()

                var recyclerView: RecyclerView = findViewById(R.id.recyclerViewMain)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = MainAdapter(this, content, listGlobal!!)
            }

            R.id.nav_item_wishes -> {
                listGlobal = "wish"
                val database = LibraryDatabase(this, null)
                val list = "wish"
                val order = LibraryDatabase.COLUMN_TITLE
                val content = database.getAllBooksFromList(list, order)
                database.close()

                var recyclerView: RecyclerView = findViewById(R.id.recyclerViewMain)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = MainAdapter(this, content, listGlobal!!)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout?.openDrawer(GravityCompat.START)
            }
            /*R.id.action_search -> {
                true
            }*/
            R.id.action_scan -> {
                Snackbar.make(this.findViewById(android.R.id.content), R.string.action_scan, Snackbar.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
