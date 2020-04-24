package com.pigeoff.library.database

import android.text.TextUtils
import androidx.room.*
import java.util.*
import kotlin.collections.ArrayList

class RmConverters {
    @TypeConverter
    fun fromListToString(value: MutableList<String>?): String? {
        if (value != null) {
            return TextUtils.join(",", value!!)
        }
        else {
            return null
        }
    }

    @TypeConverter
    fun fromStringToList(value: String?): MutableList<String>? {
        var list: MutableList<String>? = null
        if (value != null) {
            var resultList: MutableList<String> = value?.split(",").toMutableList()
            return resultList
        }
        else {
            return list
        }
    }


}
