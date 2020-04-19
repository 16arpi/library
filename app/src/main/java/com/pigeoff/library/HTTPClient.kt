package com.pigeoff.library

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.reflect.Type

class HTTPClient {

    var client: OkHttpClient = OkHttpClient()

    @Throws(IOException::class)
    fun GET(url: String): String {
        val request: Request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).execute().use({ response -> return response.body!!.string() })
    }

    @Throws(IOException::class)
    fun POST(url: String, parameters: HashMap<String, String>) {
        val builder = FormBody.Builder()
        val it = parameters.entries.iterator()
        while (it.hasNext()) {
            val pair = it.next() as Map.Entry<*, *>
            builder.add(pair.key.toString(), pair.value.toString())
        }

        val formBody = builder.build()
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).execute()
    }

    fun searchForVolume(query: String, page: Int) : VolumeClass {
        //val queryCorrige = query.replace(" ", "+")
        val queryCorrige = query
        val url ="https://www.googleapis.com/books/v1/volumes?q="+queryCorrige+"&startIndex="+page
        val content = GET(url)

        //Log.i("URL", url)
        //Log.i("JSON", content)

        val gson = Gson()
        val listType: Type = object : TypeToken<VolumeClass>() {}.type
        val dataFull: VolumeClass = gson.fromJson(content, listType)

        return dataFull
    }

}