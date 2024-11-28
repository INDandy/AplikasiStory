package com.dicoding.picodiploma.loginwithanimation.view.customviewmodel

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

class emailview : AppCompatEditText {


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Masukkan email"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                val text = s.toString()
                if (!text.matches(emailPattern.toRegex())) {
                    setError("Must Include @", null)
                } else {
                    error = null
                }
            }
            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }
}