package com.duck.flexilogger

data class LogInfo(
    val tag: String,
    val msg: String? = null,
    val tr: Throwable? = null
)