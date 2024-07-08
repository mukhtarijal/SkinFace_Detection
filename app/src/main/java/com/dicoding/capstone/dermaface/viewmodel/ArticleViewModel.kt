package com.dicoding.capstone.dermaface.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.capstone.dermaface.data.model.OGS8pWLl3rnbNgRzzItem
import com.dicoding.capstone.dermaface.repository.ArticleRepository

class ArticleViewModel(private val articleRepository: ArticleRepository) : ViewModel() {

    private val _articles = MutableLiveData<List<OGS8pWLl3rnbNgRzzItem>>()
    val articles: LiveData<List<OGS8pWLl3rnbNgRzzItem>> get() = _articles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchArticles()
    }

    private fun fetchArticles() {
        _isLoading.value = true
        articleRepository.fetchArticles().observeForever { fetchedArticles ->
            _isLoading.value = false
            _articles.value = fetchedArticles
        }
    }
}

