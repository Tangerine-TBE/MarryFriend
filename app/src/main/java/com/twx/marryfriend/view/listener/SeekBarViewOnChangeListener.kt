package com.twx.marryfriend.view.listener

import com.twx.marryfriend.view.event.Event


interface SeekBarViewOnChangeListener {
    fun touch(percent: Float, eventType: Event)
}