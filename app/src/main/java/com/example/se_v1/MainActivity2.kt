package com.example.se_v1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


        d.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,MainActivity3::class.java)
            startActivity(intent)
        })

    }
}