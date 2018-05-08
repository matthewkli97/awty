package edu.washington.mkl.awty

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    var time:Int = 0
    var text:String = ""
    var phone:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn_start = findViewById(R.id.btn_start) as Button
        val et_time = findViewById(R.id.et_time) as EditText
        val et_phone = findViewById(R.id.et_phone) as EditText
        val et_text = findViewById(R.id.et_text) as EditText

        val intent = Intent(this, MyService::class.java)
        var running = false

        et_phone.addTextChangedListener(object  : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                phone = s.toString()
                updateInputs(running)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        et_text.addTextChangedListener(object  : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                text = s.toString()
                updateInputs(running)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        et_time.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 1 && s.toString().equals("0"))
                    et_time.setText("")

                time = s.toString().toInt()
                updateInputs(running)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        btn_start.setOnClickListener { view ->

            if(running) {
                stopService(intent)
                running = false
                btn_start.text = "Start"
            } else {
                val message = "(" + phone.substring(0, 3) + ")" + phone.substring(3,6) + " - " + phone.substring(6) + " : " + text
                intent.putExtra("text", message)
                intent.putExtra("pause", time)
                startService(intent)
                running = true
                btn_start.text = "Stop"
            }
        }
        updateInputs(running)
    }


    fun updateInputs(running:Boolean) {
        val te_text = findViewById(R.id.et_text) as EditText
        val te_phone = findViewById(R.id.et_phone) as EditText
        val te_time = findViewById(R.id.et_time) as EditText
        val btn_start = findViewById(R.id.btn_start) as Button

        if(running) {
            btn_start.isEnabled = true
            te_text.isEnabled = false
            te_phone.isEnabled = false
            te_time.isEnabled = false
        } else {
            te_text.isEnabled = true
            te_phone.isEnabled = true
            te_time.isEnabled = true

            if(time > 0 && text.length > 0 && phone.length == 10) {
                btn_start.isEnabled = true
            } else {
                btn_start.isEnabled = false
            }
        }
    }

}

class MyService : IntentService("MyService") {

    var stopped:Boolean
    var mHandler: Handler

    init {
        mHandler = Handler();
        stopped = false
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.i("MyService", "Thread Start")
        val toastText = intent!!.getStringExtra("text")
        val minutes = intent!!.getIntExtra("pause", 1)

        while(!stopped) {
            try {
                Log.i("Thread", "hello")
                mHandler.post(DisplayToast(this, toastText))
                Thread.sleep((60000 * minutes).toLong())
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            } catch (e: Exception) {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopped = true
        Log.i("MyService", "Thread Destroy")
    }
}

class DisplayToast(private val mContext: Context, internal var mText: String) : Runnable {

    override fun run() {
        Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show()
    }
}
