package io.datawire.targetpractice

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import io.javalin.Javalin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

val logger: Logger = LoggerFactory.getLogger("io.datawire.targetpractice.TargetPractice")

val mapper: ObjectMapper = ObjectMapper().registerModules(
    Jdk8Module(),
    JavaTimeModule(),
    KotlinModule(),
    ParameterNamesModule())

val random = Random()

data class HttpRequestInfo(
    val method: String,
    val path: String,
    val queryParams: Map<String, Array<String>>,
    val headers: Map<String, String>,
    val remoteAddress: String,
    val remotePort: Int,
    val remoteHost: String?,
    val remoteUser: String?
)

data class WebSocketSessionInfo(
    val id: String,
    val queryParams: Map<String, Array<String>>
)

fun computeSyntheticLatency(standardDeviation: Double, mean: Double): Long {
  val latency = random.nextGaussian() * standardDeviation + mean
  return ((if (latency > 0) latency else 0.0) * 1000).toLong()
}

fun main(args: Array<String>) {
  val api = Javalin
      .create()
      .port(7000)

  api.before { _ ->
    // think of this as simulating a database call or something.
    val latency = computeSyntheticLatency(standardDeviation = .25, mean = .5)
    Thread.sleep(latency)
  }

  api.get("/") { ctx ->
    logger.info("request received: {}", ctx.ip())
    val requestInfo = HttpRequestInfo(
        method        = ctx.method(),
        path          = ctx.path(),
        queryParams   = ctx.queryParamMap(),
        headers       = ctx.headerMap(),
        remoteAddress = ctx.request().remoteAddr,
        remotePort    = ctx.request().remotePort,
        remoteHost    = ctx.request().remoteHost,
        remoteUser    = ctx.request().remoteUser
    )

    ctx.status(200).contentType("application/json").result(jsonify(requestInfo))
  }

  api.ws("/ws") { ws ->
    ws.onConnect { session ->
      logger.info("session started: {}", session.id)

      val sessionInfo = WebSocketSessionInfo(
          id = session.id,
          queryParams = if (session.queryString() != null) session.queryParamMap() else emptyMap()
      )

      session.send(jsonify(sessionInfo))
    }

    ws.onMessage { session, msg ->
      logger.info("session activity: {}", session.id)
      session.send(msg)
    }

    ws.onError { session, throwable ->
      logger.error("session error: {}", session.id, throwable)
    }

    ws.onClose { session, code, reason ->
      logger.info("session ended (code={} reason={}): {}", code, reason ?: code, session.id)
    }
  }

  api.start()
}

fun jsonify(model: Any): String {
  return mapper.writer().withDefaultPrettyPrinter().writeValueAsString(model)
}

inline fun <reified T: Any> unjsonify(payload: String): T {
  return mapper.readValue(payload)
}
