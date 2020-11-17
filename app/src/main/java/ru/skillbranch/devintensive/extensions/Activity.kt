package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_main.*

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(et_message.windowToken, 0)
}

fun Activity.isKeyboardClosed(): Boolean {
    val rootView = findViewById<View>(android.R.id.content)
    val visibleDisplayFrame = Rect()
    rootView.getWindowVisibleDisplayFrame(visibleDisplayFrame)

    return  rootView.height < visibleDisplayFrame.height()
}

fun Activity.isKeyboardOpen(): Boolean = !isKeyboardClosed()