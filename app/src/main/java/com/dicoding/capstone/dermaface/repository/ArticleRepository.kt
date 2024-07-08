package com.dicoding.capstone.dermaface.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.capstone.dermaface.BuildConfig
import com.dicoding.capstone.dermaface.data.model.NewsResponse
import com.dicoding.capstone.dermaface.data.model.OGS8pWLl3rnbNgRzzItem
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.*
import java.io.IOException

class ArticleRepository {
    private val databaseUrl = BuildConfig.BASE_URL

    fun fetchArticles(): LiveData<List<OGS8pWLl3rnbNgRzzItem>> {
        val articlesLiveData = MutableLiveData<List<OGS8pWLl3rnbNgRzzItem>>()
        val client = OkHttpClient()
        val request = Request.Builder().url(databaseUrl).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                articlesLiveData.postValue(emptyList())
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    try {
                        val newsResponse: NewsResponse = Gson().fromJson(responseBody.string(), NewsResponse::class.java)
                        val articles = newsResponse.oGS8pWLl3rnbNgRzz?.filterNotNull() ?: emptyList()
                        articlesLiveData.postValue(articles)
                    } catch (e: JsonSyntaxException) {
                        articlesLiveData.postValue(emptyList())
                    }
                } ?: run {
                    articlesLiveData.postValue(emptyList())
                }
            }
        })

        return articlesLiveData
    }
}

