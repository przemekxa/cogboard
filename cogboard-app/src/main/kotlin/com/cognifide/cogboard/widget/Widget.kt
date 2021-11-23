package com.cognifide.cogboard.widget

import io.vertx.core.json.JsonObject

interface Widget {

    val id: String
    val type: String

    fun start(): Widget

    fun stop(): Widget

    fun updateState()

    fun send(state: Any, dontWrap: Boolean = false)

    fun config(): JsonObject

    enum class Status {
        UNKNOWN,
        OK,
        FAIL,
        UNSTABLE,
        IN_PROGRESS,
        ERROR,
        ERROR_CONNECTION,
        ERROR_CONFIGURATION,
        NONE;

        companion object {
            fun from(text: String): Status = when (text.toUpperCase()) {
                "SUCCESS", "SUCCESSFUL", "OK" -> OK
                "WARN" -> UNSTABLE
                "FAILURE", "FAILED", "CRITICAL", "HEALTH_CHECK_ERROR" -> FAIL
                "NONE" -> NONE
                else -> values().firstOrNull { it.name.equals(text, ignoreCase = true) } ?: UNKNOWN
            }

            fun compare(expected: Any, actual: Any): Status = if (expected == actual) OK
            else ERROR

            fun random(): Status = values()[(values().indices).random()]
        }
    }
}
