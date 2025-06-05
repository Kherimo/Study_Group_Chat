package com.example.studygroupchat

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

 open class BaseActivity : AppCompatActivity() {
     override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
         if (ev.action == MotionEvent.ACTION_DOWN) {
             val v = currentFocus
             if (v is EditText) {
                 val outRect = Rect()
                 v.getGlobalVisibleRect(outRect)
                 if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                     v.clearFocus()
                     val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                     imm.hideSoftInputFromWindow(v.windowToken, 0)
                 }
             }
         }
         return super.dispatchTouchEvent(ev)
     }
}