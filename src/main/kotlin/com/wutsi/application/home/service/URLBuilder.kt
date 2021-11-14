package com.wutsi.application.home.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class URLBuilder(
    @Value("\${wutsi.application.server-url}") private val serverUrl: String
) {
    fun build(path: String) = build(serverUrl, path)

    fun build(prefix: String, path: String) = "$prefix/$path"
}
