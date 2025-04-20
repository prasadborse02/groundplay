package com.theprasadtech.groundplay.utils

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.util.ContentCachingRequestWrapper
import java.lang.Exception

@Component
class LoggingInterceptor : HandlerInterceptor {
    private val logger = logger()
    private val requestTimeThreadLocal = ThreadLocal<Long>()

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        // TODO: log received request
        requestTimeThreadLocal.set(System.currentTimeMillis())

        val requestBody =
            if (request.contentLength > 0) {
                when (request) {
                    is ContentCachingRequestWrapper -> String(request.contentAsByteArray)
                    else -> {
                        val wrapper = ContentCachingRequestWrapper(request)
                        String(wrapper.contentAsByteArray)
                    }
                }
            } else {
                ""
            }

        logger.info(
            "Incoming Request - URI: ${request.requestURI}, Method: ${request.method}${if (requestBody.isNotBlank()) ", Body: $requestBody" else ""}",
        )
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
        // TODO: log response
        val executionTime = System.currentTimeMillis() - (requestTimeThreadLocal.get() ?: 0)
        requestTimeThreadLocal.remove()
        logger.info("Request Completed - Status: ${response.status}, Time: ${executionTime}ms${ex?.let { ", Error: ${it.message}" } ?: ""}")
    }
}
