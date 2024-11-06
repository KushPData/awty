package edu.uw.ischool.kushp03.awty

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var messageEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var intervalEditText: EditText
    private lateinit var startStopButton: Button

    private var executorService = Executors.newSingleThreadScheduledExecutor()

    private var serviceStatus: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        messageEditText = findViewById(R.id.message)
        phoneNumberEditText = findViewById(R.id.phoneNumber)
        intervalEditText = findViewById(R.id.interval)
        startStopButton = findViewById(R.id.startStopButton)

        startStopButton.setOnClickListener {
            if (serviceStatus) {
                stopService()
            } else {
                startService()
            }
        }

    }

    private fun startService() {
        val message = messageEditText.text.toString().trim()
        val phoneNumber = phoneNumberEditText.text.toString().trim()
        val interval = intervalEditText.text.toString().trim().toIntOrNull()
        var intervalInMS: Long = 0

        if(message.isNotEmpty() && phoneNumber.isNotEmpty() && interval != null && interval > 0) {
            intervalInMS = (interval * 60 * 1000).toLong()
            startStopButton.text = "Stop"


            executorService.scheduleWithFixedDelay({
                runOnUiThread {
                    Toast.makeText(applicationContext, "$phoneNumber: $message", Toast.LENGTH_SHORT).show()
                }
            },0, intervalInMS, TimeUnit.MILLISECONDS)

            serviceStatus = true
        }

    }

    private fun stopService() {

        executorService.shutdownNow()

        startStopButton.text = "Start"
        serviceStatus = false
    }

    override fun onPause() {
        super.onPause()

        if(serviceStatus) {
            startService()
        }
    }

    override fun onResume() {
        super.onResume()
        if(serviceStatus) {
            startService()
        }
    }
}