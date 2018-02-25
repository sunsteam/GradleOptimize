package com.yomii.gradleoptimize

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val descriptionTextView = findViewById<TextView?>(R.id.description)
        val btnThrow = findViewById<Button?>(R.id.btn_throw)
        val description = "this is version: ${BuildConfig.VERSION_NAME}"
        descriptionTextView?.text = description
        btnThrow?.setOnClickListener({ throw RuntimeException(getString(R.string.throw_in_main)) })

    }
}
