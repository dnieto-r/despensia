package com.solstix.despensia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

interface MainListener {
    fun onPartialResult(text: String?)
    fun onResult(text: String?)
}

class GoogleAsr (private val context: Context, private val mainListener: MainListener) {

    private var speechRecognizer: SpeechRecognizer? = null
    private var speechRecognizerIntent: Intent? = null

    private fun initAsr() {
        Log.d("JORGETESTO", "Asr-initAsr: ${speechRecognizer == null}")
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            speechRecognizerIntent?.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            speechRecognizerIntent?.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            speechRecognizerIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")

            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Log.d("JORGETESTO", "Asr-listener onReadyForSpeech")
                }

                override fun onBeginningOfSpeech() {}

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {}

                override fun onError(error: Int) {
                    val errorType = when (error) {
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "ERROR_NETWORK_TIMEOUT"
                        SpeechRecognizer.ERROR_NETWORK -> "ERROR_NETWORK"
                        SpeechRecognizer.ERROR_AUDIO -> "ERROR_AUDIO"
                        SpeechRecognizer.ERROR_SERVER -> "ERROR_SERVER"
                        SpeechRecognizer.ERROR_CLIENT -> "ERROR_CLIENT"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "ERROR_SPEECH_TIMEOUT"
                        SpeechRecognizer.ERROR_NO_MATCH -> "ERROR_NO_MATCH"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "ERROR_RECOGNIZER_BUSY"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "ERROR_INSUFFICIENT_PERMISSIONS"
                        SpeechRecognizer.ERROR_TOO_MANY_REQUESTS -> "ERROR_TOO_MANY_REQUESTS"
                        SpeechRecognizer.ERROR_SERVER_DISCONNECTED -> "ERROR_SERVER_DISCONNECTED"
                        SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED -> "ERROR_LANGUAGE_NOT_SUPPORTED"
                        SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE -> "ERROR_LANGUAGE_UNAVAILABLE"
                        SpeechRecognizer.ERROR_CANNOT_CHECK_SUPPORT -> "ERROR_CANNOT_CHECK_SUPPORT"
                        else -> "ERROR_UNKNOWN"
                    }
                    Log.d("JORGETESTO", "Asr-listener onError: $errorType")
                    if(errorType == "ERROR_CLIENT" || errorType == "ERROR_RECOGNIZER_BUSY") {
                        stopAndDestroy()
                    }
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    var text = matches?.get(0) ?: ""
                    Log.d("JORGETESTO", "Asr-listener onResults: $text")
                    if (text.isNotEmpty()) {
                        text = text.replaceFirstChar { it.uppercase() }
                        mainListener.onResult(text)
                    } else {
                        mainListener.onResult(text)
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val matches =
                        partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.get(0) ?: ""
                    Log.d("JORGETESTO", "Asr-listener onPartialResults: $text")
                    if (text.isNotEmpty()) {
                        mainListener.onPartialResult(text)
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    fun startListening() {
        initAsr()
        speechRecognizer?.startListening(speechRecognizerIntent)
    }

    fun stopAndDestroy() {
        Log.d("JORGETESTO", "Asr-stopAndDestroy")
        stopListening()
        destroyAsr()
    }

    private fun destroyAsr() {
        Log.d("JORGETESTO", "Asr-destroyAsr")
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    private fun stopListening() {
        Log.d("JORGETESTO", "Asr-stopListening")
        speechRecognizer?.stopListening()
    }
}
