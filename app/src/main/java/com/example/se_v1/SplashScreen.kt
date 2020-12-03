package com.example.se_v1

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView

class SplashScreen : AppCompatActivity() {
    lateinit var i : Button
    lateinit var i1 : TextView
    val time :Long = 1100
    val time1 : Long =1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        i = findViewById<Button>(R.id.i)
        i1 = findViewById<TextView>(R.id.tv)



        Handler().postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, 1200)

        var animatorY = ObjectAnimator.ofFloat(i,"y",550f)
        animatorY.setDuration(time)
        var animatorSet = AnimatorSet()
        animatorSet.playTogether(animatorY)
        animatorSet.start()

        var animatorX = ObjectAnimator.ofFloat(i1,"x",200f)
        animatorX.setDuration(time1)
        var animatorSet1 = AnimatorSet()
        animatorSet1.playTogether(animatorX)
        animatorSet1.start()



    }
}