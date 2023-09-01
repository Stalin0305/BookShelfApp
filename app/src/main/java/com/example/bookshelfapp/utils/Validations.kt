package com.example.bookshelfapp.utils

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.callbackFlow
import org.w3c.dom.Text
import java.util.concurrent.Flow

const val EMPTY_STRING = ""
fun String.checkIfPasswordIsValid(): String {
    if (this.length < 8) {
        return "Password should have a minimum of 8 characters"
    }

    val digitRegex = ".*\\d.*"
    val lowercaseRegex = ".*[a-z].*"
    val uppercaseRegex = ".*[A-Z].*"
    val specialCharRegex = ".*[!@#\$%&()].*"

    if (!this.matches(Regex(digitRegex))) {
        return "Password must contain at least one digit."
    }

    if (!this.matches(Regex(lowercaseRegex))) {
        return "Password must contain at least one lowercase letter."
    }

    if (!this.matches(Regex(uppercaseRegex))) {
        return "Password must contain at least one uppercase letter."
    }

    if (!this.matches(Regex(specialCharRegex))) {
        return "Password must contain at least one special character [!@#\$%&()]."
    }
    return EMPTY_STRING
}

fun String.checkIfEmailIsValid(): String {
    val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    if (!this.matches(Regex(emailRegex))) {
        return "Please enter a valid email address"
    }
    return EMPTY_STRING
}

fun TextInputLayout.setTextWatcher(editText: TextInputLayout, onTextChanged:(String) -> Unit) {
    this.editText?.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            editText.error = null
            onTextChanged(p0.toString())
        }

        override fun afterTextChanged(p0: Editable?) {

        }

    })

}