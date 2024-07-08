package com.dicoding.capstone.dermaface.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone.dermaface.R
import com.dicoding.capstone.dermaface.repository.ScanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScanViewModel(private val scanRepository: ScanRepository) : ViewModel() {

    private val _modelReady = MutableLiveData<Boolean>()
    val modelReady: LiveData<Boolean> get() = _modelReady

    private val _analysisResult = MutableLiveData<Pair<String, String>>()
    val analysisResult: LiveData<Pair<String, String>> get() = _analysisResult

    private val _modelStatusMessage = MutableLiveData<String?>()
    val modelStatusMessage: MutableLiveData<String?> get() = _modelStatusMessage

    init {
        downloadModel()
    }

    private fun downloadModel() {
        _modelReady.value = false
        _modelStatusMessage.value = "Downloading model..."
        scanRepository.downloadModel(
            onModelDownloaded = {
                _modelReady.postValue(true)
                _modelStatusMessage.postValue(null)
            },
            onError = { error ->
                _modelReady.postValue(false)
                _modelStatusMessage.postValue("${scanRepository.context.getString(R.string.error_downloading_model)}: $error")
            }
        )
    }

    fun analyzeImage(bitmap: Bitmap) {
        if (_modelReady.value != true) {
            _analysisResult.postValue("Error" to scanRepository.context.getString(R.string.model_not_ready))
            return
        }

        viewModelScope.launch {
            try {
                val results = withContext(Dispatchers.IO) {
                    scanRepository.analyzeImage(bitmap)
                }
                if (results != null) {
                    val (_, diagnosis) = getMaxResult(results)
                    val recommendation = scanRepository.getRecommendation(diagnosis)
                    _analysisResult.postValue(diagnosis to recommendation)
                } else {
                    _analysisResult.postValue("Error" to scanRepository.context.getString(R.string.error_analyzing_image))
                }
            } catch (e: Exception) {
                _analysisResult.postValue("Error" to "${scanRepository.context.getString(R.string.error_analyzing_image)}: ${e.message}")
            }
        }
    }

    private fun getMaxResult(confidences: FloatArray): Pair<Float, String> {
        val classLabels = arrayOf(
            "Actinic Keratosis", "Herpes", "Jerawat",
            "Kerutan", "Kulit Normal", "Mata Panda", "Milia",
            "Panu", "Rosacea", "Vitiligo"
        )

        var maxConfidence = Float.MIN_VALUE
        var maxIndex = -1
        for (i in confidences.indices) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i]
                maxIndex = i
            }
        }
        return maxConfidence to classLabels[maxIndex]
    }
}
