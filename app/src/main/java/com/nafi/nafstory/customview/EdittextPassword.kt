package com.nafi.nafstory.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.nafi.nafstory.R

class EdittextPassword : AppCompatEditText {
    private lateinit var passwordIconDrawable: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        transformationMethod = PasswordTransformationMethod.getInstance()
    }

    private fun init() {
        passwordIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_lock_24)!!
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        compoundDrawablePadding = 16

        setHint(R.string.hint_password)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(AUTOFILL_HINT_PASSWORD)
        }

        setDrawable(start = passwordIconDrawable)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.length < 8) {
                    error = context.getString(R.string.error_password)
                }
            }
        })
    }

    private fun setDrawable(
        start: Drawable? = null,
        top: Drawable? = null,
        end: Drawable? = null,
        bottom: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom)
    }
}
