package com.yomii.gradleoptimize

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val description = "this is version: ${BuildConfig.VERSION_NAME}"
        descriptionView.text = description
//        btnThrow.setOnClickListener {
//            Toast.makeText(applicationContext, R.string.throw_in_main, Toast.LENGTH_SHORT).show()
//        }
        btnThrow?.setOnClickListener {
            Toast.makeText(applicationContext, R.string.bug_fixed_in_main, Toast.LENGTH_SHORT).show()
        }

    }
}
