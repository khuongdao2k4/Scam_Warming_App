package com.example.scam_warming_app.ai

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoskSpeechToTextService @Inject constructor(
    @ApplicationContext private val context: Context
) : SpeechToTextService, RecognitionListener {

    private var model: Model? = null
    private var speechService: SpeechService? = null

    init {
        initModel()
    }

    private fun initModel() {
        StorageService.unpack(context, "model-vn", "model",
            { model: Model ->
                this.model = model
                Log.d("VoskSTT", "Model loaded successfully")
            },
            { exception: IOException ->
                Log.e("VoskSTT", "Failed to unpack model", exception)
            }
        )
    }

    override fun startListening(): Flow<String> = callbackFlow {
        if (model == null) {
            close(Exception("Model not ready"))
            return@callbackFlow
        }

        try {
            val rec = Recognizer(model, 16000.0f)
            speechService = SpeechService(rec, 16000.0f)
            speechService?.startListening(object : RecognitionListener {
                override fun onPartialResult(hypothesis: String?) {}

                override fun onResult(hypothesis: String?) {
                    hypothesis?.let {
                        val json = JSONObject(it)
                        val text = json.optString("text")
                        if (text.isNotEmpty()) {
                            trySend(text)
                        }
                    }
                }

                override fun onFinalResult(hypothesis: String?) {
                    hypothesis?.let {
                        val json = JSONObject(it)
                        val text = json.optString("text")
                        if (text.isNotEmpty()) {
                            trySend(text)
                        }
                    }
                }

                override fun onError(exception: Exception?) {
                    Log.e("VoskSTT", "Error during recognition", exception)
                }

                override fun onTimeout() {
                    Log.d("VoskSTT", "Recognition timeout")
                }
            })
        } catch (e: Exception) {
            Log.e("VoskSTT", "Failed to start listening", e)
            close(e)
        }

        awaitClose {
            stopListening()
        }
    }

    override fun stopListening() {
        speechService?.let {
            it.stop()
            it.shutdown()
        }
        speechService = null
    }

    override fun isModelReady(): Boolean = model != null

    override fun onPartialResult(hypothesis: String?) {}
    override fun onResult(hypothesis: String?) {}
    override fun onFinalResult(hypothesis: String?) {}
    override fun onError(exception: Exception?) {}
    override fun onTimeout() {}
}
