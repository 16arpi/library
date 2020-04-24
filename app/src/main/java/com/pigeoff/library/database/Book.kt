package com.pigeoff.library.database

import androidx.room.*

@Entity
data class Book(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var googleId: String? = null,
    var title: String? = null,
    var subtitle: String? = null,
    var authors: MutableList<String>? = null,
    var date: String? = null,
    var publisher: String? = null,
    var categories: MutableList<String>? = null,
    var nbPages: Int? = null,
    var coverImage: String? = null,
    var list: String? = null,
    var location: String? = null,
    var notes: String? = null,
    var mark: Int = 0,
    var isBorrowed: Int = 0,
    var borrowedDeadline: Long? = 0,
    var tag: MutableList<String>? = null
)
