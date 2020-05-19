package id.co.bigtek.defuj

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private var tugas : Double? = null
    private var quiz : Double? = null
    private var uts : Double? = null
    private var uas : Double? = null
    private var total : Double? = null
    private var matkul : String? = null
    private var textToSpeech : TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setFunctions()
    }

    private fun setFunctions() {
        spinnerMK!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long) {
                et_tugas!!.text.clear()
                et_quiz!!.text.clear()
                et_uts!!.text.clear()
                et_uas!!.text.clear()
                et_total!!.text.clear()
                et_nilai!!.text.clear()
                et_textToSpeech!!.text.clear()
                et_speechRecognizer!!.text.clear()

                tugas = 0.0
                quiz = 0.0
                uts = 0.0
                uas = 0.0
                total = 0.0

                matkul = spinnerMK!!.selectedItem.toString()
            }
        }

        btnHitung!!.setOnClickListener {
            if(et_tugas!!.text.isNotEmpty()){
                tugas = if(et_tugas!!.text.toString() == "0"){
                    0.0
                }else{
                    val nilai = et_tugas!!.text.toString().toDouble()
                    nilai*15/100
                }

                if(et_quiz!!.text.isNotEmpty()){
                    quiz = if(et_quiz!!.text.toString() == "0"){
                        0.0
                    }else{
                        val nilai = et_quiz!!.text.toString().toDouble()
                        nilai*15/100
                    }

                    if(et_uts!!.text.isNotEmpty()){
                        uts = if(et_uts!!.text.toString() == "0"){
                            0.0
                        }else{
                            val nilai = et_uts!!.text.toString().toDouble()
                            nilai*30/100
                        }

                        if(et_uas!!.text.isNotEmpty()){
                            uas = if(et_uas!!.text.toString() == "0"){
                                0.0
                            }else{
                                val nilai = et_uas!!.text.toString().toDouble()
                                nilai*40/100
                            }

                            total = tugas!!+quiz!!+uts!!+uas!!
                            et_total!!.setText(aturJumlahAngka(total!!))
                            et_textToSpeech!!.setText(aturJumlahAngka(total!!))

                            when {
                                total!! in 80.0..100.0 -> { //total!! <= 100 && total!! >= 80
                                    et_nilai!!.setText("A")
                                }
                                total!! in 70.0..79.0 -> { //total!! <= 79 && total!! >= 70
                                    et_nilai!!.setText("B")
                                }
                                total!! in 60.0..69.0 -> { //total!! <= 69 && total!! >= 60
                                    et_nilai!!.setText("C")
                                }
                                total!! in 40.0..59.0 -> { //total!! <= 59 && total!! >= 40
                                    et_nilai!!.setText("D")
                                }
                                total!! in 0.0..39.0 -> { //total!! <= 39 && total!! >= 0
                                    et_nilai!!.setText("E")
                                }
                            }
                        }else{
                            showToast("Nilai UAS masih kosong!")
                        }
                    }else{
                        showToast("Nilai UTS masih kosong!")
                    }
                }else{
                    showToast("Nilai Quiz masih kosong!")
                }
            }else{
                showToast("Nilai Tugas masih kosong!")
            }
        }

        textToSpeech = TextToSpeech(this,TextToSpeech.OnInitListener {
            if(it == TextToSpeech.SUCCESS) {
                val ttsLang = textToSpeech!!.setLanguage(Locale.US)

                if(ttsLang == TextToSpeech.LANG_MISSING_DATA || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                    showToast("The Language is not supported!")
                }else {
                    showToast("Language Supported.")
                }
            }else{
                showToast("TTS Initialization failed!")
            }
        })

        btnTextToSpeech!!.setOnClickListener {
            if(et_textToSpeech!!.text.isNotEmpty()){
                textToSpeech!!.speak(et_textToSpeech!!.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
            }
        }

        btnSpeechRecognizer!!.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
            startActivityForResult(intent, 10)
        }
    }

    private fun showToast(title : String){
        Toast.makeText(this,title,Toast.LENGTH_SHORT).show()
    }

    private fun aturJumlahAngka(angka : Double) : String{
        val df = DecimalFormat("#.#")
        return df.format(angka)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null){
            if(requestCode == 10){
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                et_speechRecognizer!!.setText(result!![0])
            }
        }else{
            showToast("Failed to recognize speech!")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(textToSpeech != null){
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }
    }
}
