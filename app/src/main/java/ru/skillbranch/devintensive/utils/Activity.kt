package ru.skillbranch.devintensive.utils

import android.app.Activity
import android.graphics.Rect
import android.view.View

fun Activity.hideKeyboard() {
//    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//    imm.hideSoftInputFromWindow(et_message.windowToken, 0)
}

fun Activity.isKeyboardClosed(): Boolean {
    val rootView = findViewById<View>(android.R.id.content)
    val visibleDisplayFrame = Rect()
    rootView.getWindowVisibleDisplayFrame(visibleDisplayFrame)

    return  rootView.height < visibleDisplayFrame.height()
}

fun Activity.isKeyboardOpen(): Boolean = !isKeyboardClosed()