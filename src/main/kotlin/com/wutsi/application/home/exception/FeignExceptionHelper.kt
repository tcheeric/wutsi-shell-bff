package com.wutsi.application.home.exception

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.core.error.ErrorResponse
import feign.FeignException

fun FeignException.toErrorResponse(mapper: ObjectMapper): ErrorResponse? =
    try {
        mapper.readValue(this.contentUTF8(), ErrorResponse::class.java)
    } catch (ex: Exception) {
        null
    }
