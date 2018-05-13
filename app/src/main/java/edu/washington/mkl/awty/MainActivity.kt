package edu.washington.mkl.awty

import android.Manifest
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
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.telephony.SmsManager


class MainActivity : AppCompatActivity() {


    private val MY_PERMISSIONS_REQUEST_SEND_SMS = 0
    var time:Int = 0
    var text:String = ""
    var phone:String = ""
    var running = false
    lateinit var textIntent:Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textIntent = Intent(this, MyService::class.java)
        val btn_start = findViewById(R.id.btn_start) as Button
        val et_time = findViewById(R.id.et_time) as EditText
        val et_phone = findViewById(R.id.et_phone) as EditText
        val et_text = findViewById(R.id.et_text) as EditText

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
                stopService(textIntent)
                running = false
                btn_start.text = "Start"
            } else {
                sendSMSMessage()
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

            btn_start.isEnabled = true
            if(time > 0 && text.length > 0 && phone.length == 10) {
                btn_start.isEnabled = true
            } else {
                btn_start.isEnabled = false
            }
        }
    }

    protected fun sendSMSMessage() {
        val btn_start = findViewById(R.id.btn_start) as Button
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.SEND_SMS)) {
                textIntent.putExtra("text", text)
                textIntent.putExtra("text", text)
                        .putExtra("pause", time)
                textIntent.putExtra("text", text)
                        .putExtra("phone", phone)
                startService(textIntent)
                running = true
                btn_start.text = "Stop"
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.SEND_SMS),
                        MY_PERMISSIONS_REQUEST_SEND_SMS)
            }
        } else {
            textIntent.putExtra("text", text)
            textIntent.putExtra("text", text)
                    .putExtra("pause", time)
            textIntent.putExtra("text", text)
                    .putExtra("phone", phone)
            startService(textIntent)
            running = true
            btn_start.text = "Stop"
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_SEND_SMS -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(applicationContext,
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show()
                    return
                }
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
        val text = intent!!.getStringExtra("text")
        val phoneNumber = intent!!.getStringExtra("phone")
        val minutes = intent!!.getIntExtra("pause", 1)

        while(!stopped) {
            try {
                Log.i("Thread", "hello")
                mHandler.post(DisplayToast(this, text))

                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phoneNumber, null, text, null, null)

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
