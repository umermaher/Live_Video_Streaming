package com.example.workwithagora

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.workwithagora.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermission()
        binding.joinChannelBtn.setOnClickListener {
//            if(requirementsDone()) {
                val checked=binding.radioGroup.checkedRadioButtonId
                val userRole=if(checked==binding.audienceBtn.id) 0 else 1
                val intent= Intent(this,VideoStreamingActivity::class.java)
                intent.putExtra(USER_ROlE,userRole)
                intent.putExtra(CHANNEL_NAME,binding.channelNameText.text.toString())
                startActivity(intent)
//            }else
//                Toast.makeText(this,"Fill the requirements",Toast.LENGTH_SHORT)
        }
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                10
            )
        }
    }

    private fun requirementsDone(): Boolean {
        return (binding.channelNameText.text.isNotEmpty()) && (binding.audienceBtn.isChecked || binding.broadcasterBtn.isChecked)
    }
}