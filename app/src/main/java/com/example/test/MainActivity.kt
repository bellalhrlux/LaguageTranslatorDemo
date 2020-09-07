package com.example.test

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.google.cloud.translate.Translation
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    private var originalText: String? = null
    private var translatedText: String? = null
    private val connected = false
    var translate: Translate? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

        button.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.CHINESE)

            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, 10)
            } else {
                Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        //Translate languages
        getTranslateService()
        translate("How are you?")


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            10 -> if (resultCode == RESULT_OK && data != null) {
                val result =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                textView.text = result!![0]
            }
        }
    }


    fun getTranslateService() {
        val policy =
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            resources.openRawResource(R.raw.lan_translator).use { `is` ->

                //Get credentials:
                val myCredentials = GoogleCredentials.fromStream(`is`)

                //Set credentials and get translate service:
                val translateOptions =
                    TranslateOptions.newBuilder().setCredentials(myCredentials).build()
                translate = translateOptions.service
            }
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }
    }

    fun translate(sourceTxt:String) {

        //Get input text to be translated:
        originalText = sourceTxt
        val translation: Translation = translate!!.translate(
            originalText,
            Translate.TranslateOption.targetLanguage("bn"),
            Translate.TranslateOption.model("base")
        )
        translatedText = translation.translatedText

        //Translated text and original text are set to TextViews:
        textView2.text = translatedText
    }
}