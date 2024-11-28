package com.dicoding.picodiploma.loginwithanimation.view.customviewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import com.dicoding.picodiploma.loginwithanimation.R

class nameview : AppCompatEditText {
    private var isNameValid: Boolean = false

    private fun init() {

        addTextChangedListener(onTextChanged = {p0: CharSequence?, p1: Int, p2: Int, p3: Int ->
            val name = text?.trim()
            if (name.isNullOrEmpty()) {
                isNameValid = false
                error = resources.getString(R.string.required_name)
            } else {
                isNameValid = true
            }
        })
    }

    private fun onShowVisibilityIcon(icon: Drawable) {
        setButtonDrawables(startOfTheText = icon)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }
}