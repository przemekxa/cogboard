package com.cognifide.cogboard.widget.type

import com.cognifide.cogboard.CogboardConstants.Props
import com.cognifide.cogboard.CogboardConstants.ConnectionType
import com.cognifide.cogboard.config.service.BoardsConfigService
import com.cognifide.cogboard.widget.BaseWidget
import com.cognifide.cogboard.widget.Widget
import com.cognifide.cogboard.widget.connectionStrategy.ConnectionStrategy
import com.cognifide.cogboard.widget.connectionStrategy.ConnectionStrategyFactory
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.json.JsonObject
import java.nio.charset.Charset

class LogViewerWidget(
    vertx: Vertx,
    config: JsonObject,
    serv: BoardsConfigService
) : BaseWidget(vertx, config, serv) {
    private val address = config.getString(Props.LOG_SOURCE)
    private val lines = config.getString(Props.LOG_LINES)
    private val connectionType = config.getString(Props.LOG_SOURCE_TYPE)

    private var consumer: MessageConsumer<*>? = null
    var connectionStrategy: ConnectionStrategy = determineConnectionStrategy()

    override fun start(): Widget {
        when (connectionType) {
            ConnectionType.SSH -> {
                consumer = vertx.eventBus()
                        .consumer<Buffer>(eventBusAddress) {
                            handleResponse(it.body())
                        }
            }
            ConnectionType.HTTP -> {
                consumer = vertx.eventBus()
                        .consumer<JsonObject>(eventBusAddress) {
                            handleResponse(it.body())
                        }
            }
            else -> {
                sendConfigurationError("No type of connection chosen")
            }
        }
        return super.start()
    }

    override fun stop(): Widget {
        consumer?.unregister()
        return super.stop()
    }

    override fun updateState() {
        if (address.isNotBlank()) {
            connectionStrategy.connectAndGetResources(address, config)
        } else {
            sendConfigurationError("Endpoint URL is blank")
        }
    }

    private fun handleResponse(responseBody: JsonObject) {
        if (checkAuthorized(responseBody)) {
            val logs = responseBody.getString(Props.LOG_LINES)

            send(prepareLogs(logs))
        }
    }

    private fun handleResponse(responseBody: Buffer) {
        val logs = prepareLogs(
                responseBody.toString(Charset.defaultCharset())
        )

        send(logs)
    }

    private fun prepareLogs(logs: String): String {
        // TODO
        return logs
    }

    private fun determineConnectionStrategy() =
            ConnectionStrategyFactory(vertx, config)
                .addVertxInstance(vertx)
                .build()
}
