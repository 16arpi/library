package com.pigeoff.library.database

import androidx.room.*

@Entity
data class RmList(
    @PrimaryKey var id: Int = 0,
    var title: String? = null,
    var order: String? = null
)
