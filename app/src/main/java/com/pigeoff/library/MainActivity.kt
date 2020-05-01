package com.pigeoff.library

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.pigeoff.library.database.LegacyDB
import com.pigeoff.library.database.RmDatabase
import com.pigeoff.library.database.RmMigration


class MainActivity : AppCompatActivity() {

    var navigationView: NavigationView? = null
    var drawerLayout: DrawerLayout? = null
    var recyclerView: RecyclerView? = null
    var recyclerAdapter: MainAdapter? = null
    var database: RmDatabase? = null

    var LIST_GLOBAL: String? = ""
    var TAG_GLOBAL: String = ""
    var RESULT_SEARCH_CODE = 1871
    var IS_BORROWED = 0
    var LIST_TAGS = arrayListOf<String>()

    var NAV_ITEM_HOME = 1
    var NAV_ITEM_WISH = 2
    var NAV_ITEM_BORROWED = 3


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            var oldDatabase = LegacyDB(this, null)
            oldDatabase.migrateDatabse(this)
        }
        catch (e: Exception) {
            Log.i("Error", e.toString())
        }
        finally {
            database = Room.databaseBuilder(
                this,
                RmDatabase::class.java, "librarydb"
            ).allowMainThreadQueries().addMigrations(RmMigration().MIGRATION_2_3).build()
        }


        var toolbar: Toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigationView)
        setTagsMenu(navigationView)
        drawerLayout = findViewById(R.id.drawerLayout)
        recyclerView = findViewById(R.id.recyclerViewMain)
        recyclerView?.layoutManager = LinearLayoutManager(this)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_action_menu)
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setTitle(R.string.title_main)

        navigationView?.bringToFront()
        navigationView?.setNavigationItemSelectedListener {
            Log.i("Item selected", it.itemId.toString()+it.title)

            when(it.itemId) {
                R.id.nav_item_home -> {
                    navigationUpdateUI(it, NAV_ITEM_HOME)
                }

                R.id.nav_item_wishes -> {
                    navigationUpdateUI(it, NAV_ITEM_WISH)
                }
                R.id.nav_item_borrowed -> {
                    navigationUpdateUI(it, NAV_ITEM_BORROWED)
                }
                else -> {
                    navigationUpdateUI(it, it.itemId)
                }
            }
            true
        }

        navigationView?.menu?.getItem(0)?.setChecked(true)
        navigationUpdateUI(navigationView?.menu!!.getItem(0), NAV_ITEM_HOME)

        var floating: FloatingActionButton = findViewById(R.id.floatinButton)
        floating.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra("list", LIST_GLOBAL)
            intent.putExtra("isBorrowed", IS_BORROWED)
            intent.putExtra("tag", TAG_GLOBAL)
            startActivityForResult(intent, RESULT_SEARCH_CODE)
        }

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

        val oldId = navigationView?.checkedItem?.itemId
        navigationView?.menu?.removeGroup(R.id.main_group)
        navigationView?.inflateMenu(R.menu.nav_menu)
        setTagsMenu(navigationView)
        if (navigationView?.menu?.findItem(oldId!!)?.title != null && navigationView?.menu?.findItem(oldId!!)?.title!!.isNotEmpty()) {
            navigationView?.menu?.findItem(oldId!!)?.setChecked(true)
        }

        var item = navigationView!!.checkedItem

        when(item?.itemId) {
            R.id.nav_item_home -> {
                LIST_GLOBAL = ""
                TAG_GLOBAL = ""
                IS_BORROWED = 0
                val content = database!!.bookDao().getAllBooksFromLibrary()
                recyclerView?.adapter = MainAdapter(this, content, LIST_GLOBAL!!, IS_BORROWED, TAG_GLOBAL, LIST_TAGS)
            }

            R.id.nav_item_wishes -> {
                LIST_GLOBAL = "wish"
                TAG_GLOBAL = ""
                IS_BORROWED = 0
                val list = "wish"
                val order = "id DESC"
                val content = database!!.bookDao().getAllBooksFromList(list, order)
                recyclerView?.adapter = MainAdapter(this, content, LIST_GLOBAL!!, IS_BORROWED, TAG_GLOBAL, LIST_TAGS)
            }

            R.id.nav_item_borrowed -> {
                LIST_GLOBAL = ""
                IS_BORROWED = 1
                TAG_GLOBAL = ""
                val content = database!!.bookDao().getAllBorrowed()
                recyclerView?.adapter = MainAdapter(this, content, LIST_GLOBAL!!, IS_BORROWED, TAG_GLOBAL, LIST_TAGS)

            }

            else -> {
                IS_BORROWED = 0
                var tag = item?.title.toString()
                TAG_GLOBAL = tag
                val content = database!!.bookDao().getAllFromTag(tag)
                recyclerView?.adapter = MainAdapter(this, content, LIST_GLOBAL!!, IS_BORROWED, TAG_GLOBAL, LIST_TAGS)
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
                var snackbar = Snackbar.make(this.findViewById(android.R.id.content), R.string.action_scan, Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun navigationUpdateUI(it: MenuItem, param: Int) {
        when(it.itemId) {
            R.id.nav_item_home -> {
                if (it.isChecked) {
                    Log.i("Info", "HOME CLICK FALSE")
                    false
                }

                else {
                    Log.i("Info", "HOME CLICK TRUE")
                    LIST_GLOBAL = ""
                    IS_BORROWED = 0
                    TAG_GLOBAL = ""
                    val content = database!!.bookDao().getAllBooksFromLibrary()

                    recyclerAdapter = MainAdapter(this, content, LIST_GLOBAL!!, IS_BORROWED, TAG_GLOBAL, LIST_TAGS)
                    recyclerView?.adapter = recyclerAdapter
                    it.isChecked = true

                    var collapseToolbar: CollapsingToolbarLayout = findViewById(R.id.collapseToolbar)
                    supportActionBar!!.setTitle(getString(R.string.title_main))
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
                    LIST_GLOBAL = "wish"
                    IS_BORROWED = 0
                    TAG_GLOBAL = ""
                    var order = "id DESC"
                    val content = database!!.bookDao().getAllBooksFromList(list, order)

                    recyclerAdapter = MainAdapter(this, content, LIST_GLOBAL!!, IS_BORROWED, TAG_GLOBAL, LIST_TAGS)
                    recyclerView?.adapter = recyclerAdapter
                    it.isChecked = true

                    var collapseToolbar: CollapsingToolbarLayout = findViewById(R.id.collapseToolbar)
                    supportActionBar!!.setTitle(getString(R.string.title_wish))
                    collapseToolbar.setTitle(getString(R.string.title_wish))

                    drawerLayout!!.closeDrawer(GravityCompat.START)
                }
            }

            R.id.nav_item_borrowed -> {
                if (it.isChecked) {
                    Log.i("Info", "HOME CLICK FALSE")
                    false
                }

                else {
                    Log.i("Info", "HOME CLICK TRUE")
                    IS_BORROWED = 1
                    LIST_GLOBAL = ""
                    TAG_GLOBAL = ""
                    val content = database!!.bookDao().getAllBorrowed()

                    recyclerAdapter = MainAdapter(this, content, LIST_GLOBAL!!, IS_BORROWED, TAG_GLOBAL, LIST_TAGS)
                    recyclerView?.adapter = recyclerAdapter
                    it.isChecked = true

                    var collapseToolbar: CollapsingToolbarLayout = findViewById(R.id.collapseToolbar)
                    supportActionBar!!.setTitle(getString(R.string.title_borrowed))
                    collapseToolbar.setTitle(getString(R.string.title_borrowed))

                    drawerLayout!!.closeDrawer(GravityCompat.START)
                }
            }

            else -> {
                if (it.isChecked) {
                    false
                }
                else {
                    IS_BORROWED = 0
                    LIST_GLOBAL = ""
                    val tag = it.title.toString()
                    TAG_GLOBAL = tag
                    val content = database!!.bookDao().getAllFromTag(tag)

                    recyclerAdapter = MainAdapter(this, content, LIST_GLOBAL!!, IS_BORROWED, TAG_GLOBAL, LIST_TAGS)
                    recyclerView?.adapter = recyclerAdapter
                    it.isChecked = true

                    var collapseToolbar: CollapsingToolbarLayout = findViewById(R.id.collapseToolbar)
                    supportActionBar!!.setTitle(it.title)
                    collapseToolbar.setTitle(it.title)

                    drawerLayout!!.closeDrawer(GravityCompat.START)
                }
            }


        }
    }

    fun setTagsMenu(nav: NavigationView?) {
        LIST_TAGS = getAllTags(database!!)
        LIST_TAGS.sort()
        var navMenu = nav?.menu
        var id = 34
        if (LIST_TAGS != null && LIST_TAGS.isNotEmpty()) {
            for (element in LIST_TAGS!!) {
                navMenu?.add(R.id.main_group, id, Menu.NONE, element)
                navMenu?.findItem(id)?.setIcon(R.drawable.ic_bookmark_border_black_24dp)?.setCheckable(true)
                id++
            }
        }
        else navMenu?.add(R.id.main_group, Menu.NONE, Menu.NONE, R.string.tags_indicator_empty)?.setCheckable(false)?.setEnabled(false)?.setIcon(R.drawable.ic_bookmark_border_black_24dp)
    }


    fun getAllTags(database: RmDatabase) : ArrayList<String>{
        var allBooks = database.bookDao().getAllBooksFromLibrary()
        var allTags: ArrayList<String> = arrayListOf()
        for (element in allBooks) {
            var tags = element.tag
            if (tags != null && tags.isNotEmpty()) {
                for (item in tags) {
                    Log.i("Tag existing", item)
                    if (!allTags.contains(item) && item.isNotEmpty()) allTags.add(item)
                }
            }
        }
        allTags.sort()
        return allTags
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout?.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }
}
