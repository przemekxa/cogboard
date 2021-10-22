package com.cognifide.cogboard.widget.connectionStrategy

import com.cognifide.cogboard.CogboardConstants
import com.cognifide.cogboard.http.auth.AuthenticationType
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

abstract class ConnectionStrategy(protected val vertx: Vertx) {

    protected fun JsonObject.endpointProp(prop: String): String {
        return this.getJsonObject(CogboardConstants.Props.ENDPOINT_LOADED)?.getString(prop) ?: ""
    }

    protected open fun authenticationTypes(): Set<AuthenticationType> {
        return setOf(AuthenticationType.BASIC)
    }

    abstract fun connectAndGetResources(address: String, arguments: JsonObject)
}
