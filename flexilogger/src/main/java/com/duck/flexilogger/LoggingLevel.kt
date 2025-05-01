package com.duck.flexilogger

enum class LoggingLevel(val level: Int) {
    V(5),
    I(4),
    D(3),
    W(2),
    E(1),
    NONE(0);

    fun canLog(type: LogType): Boolean {
        return when(type) {
            LogType.V -> level >= V.level
            LogType.I -> level >= I.level
            LogType.D -> level >= D.level
            LogType.E -> level >= E.level
            LogType.WTF,
            LogType.W -> level >= W.level
        }
    }
}