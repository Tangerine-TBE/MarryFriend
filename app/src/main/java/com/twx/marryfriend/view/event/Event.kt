package com.twx.marryfriend.view.event

sealed class Event

object Down : Event()
object Move : Event()
object Up : Event()

fun Any.and(block: () -> Unit = {}) = block.invoke()