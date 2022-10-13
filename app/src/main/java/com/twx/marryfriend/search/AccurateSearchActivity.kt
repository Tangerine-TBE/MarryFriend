package com.twx.marryfriend.search

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.KeyboardUtils
import com.gyf.barlibrary.ImmersionBar
import com.twx.marryfriend.R
import kotlinx.android.synthetic.main.activity_accurate_search.*

class AccurateSearchActivity:AppCompatActivity(R.layout.activity_accurate_search) {
    private var currentTextKey=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .statusBarColorInt(Color.TRANSPARENT)
            .init()
        KeyboardUtils.fixAndroidBug5497(this)
        KeyboardUtils.clickBlankArea2HideSoftInput()
        startAccurateSearch.isEnabled=false
        editText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                
            }

            override fun afterTextChanged(s: Editable?) {
                startAccurateSearch.isEnabled=!s.isNullOrBlank()
                currentTextKey=s.toString()
            }
        })
        editText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                startAccurateSearch.performClick()
            }
            return@setOnEditorActionListener false
        }
        initListener()
    }

    private fun initListener(){
        startAccurateSearch.setOnClickListener {
            startActivity(SearchResultActivity.getIntent(this,currentTextKey))
        }
        deleteText.setOnClickListener {
            editText.setText("")
            currentTextKey=""
        }
    }
}