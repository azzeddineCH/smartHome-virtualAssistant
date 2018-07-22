package com.cerist.summer.virtualassistant.Utils.BaseComponents

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.support.v7.app.AppCompatActivity
import android.util.Log

open class BaseRecognitionActivity:AppCompatActivity(), RecognitionListener {
    companion object {
        private val TAG = "BaseRecognitionActivity"
    }


    override fun onReadyForSpeech(params: Bundle?) {
        Log.d(TAG,"onReadyForSpeech")
    }

    override fun onRmsChanged(rmsdB: Float) {
        Log.d(TAG,"onRmsChanged")
    }

    override fun onBufferReceived(buffer: ByteArray?) {

    }

    override fun onPartialResults(partialResults: Bundle?) {

    }

    override fun onEvent(eventType: Int, params: Bundle?) {

    }

    override fun onBeginningOfSpeech() {

    }

    override fun onEndOfSpeech() {

    }

    override fun onError(error: Int) {
        Log.d(TAG,"onError: ${getErrorText(error)}")
    }

    override fun onResults(results: Bundle?) {

    }

    private fun getErrorText(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO ->  "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT ->   "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->  "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK ->  "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->  "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH ->  "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->  "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER ->  "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->  "No speech input"
            else ->  "Didn't understand, please try again."
        }

    }
}