package com.wutsi.application.shell.endpoint.scan.command

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.scan.dto.ScanRequest
import com.wutsi.application.shell.exception.toErrorResponse
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.qr.WutsiQrApi
import com.wutsi.platform.qr.dto.DecodeQRCodeRequest
import com.wutsi.platform.qr.error.ErrorURN
import feign.FeignException
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/scan")
class ScanCommand(
    private val qrApi: WutsiQrApi,
    private val urlBuilder: URLBuilder,
    private val mapper: ObjectMapper,

    @Value("\${wutsi.application.cash-url}") private val cashUrl: String,
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestBody request: ScanRequest): Action {
        logger.add("code", request.code)
        logger.add("format", request.format)

        try {
            val entity = qrApi.decode(
                DecodeQRCodeRequest(
                    token = request.code
                )
            ).entity

            return if (entity.type == "payment-request")
                Action(
                    type = ActionType.Route,
                    url = urlBuilder.build(cashUrl, "/pay/confirm?payment-request-id=${entity.id}")
                )
            else
                createErrorAction(null, "prompt.error.unsupported-qr-code")
        } catch (ex: FeignException) {
            val response = ex.toErrorResponse(mapper)
            return if (response?.error?.code == ErrorURN.EXPIRED.urn)
                createErrorAction(ex, "prompt.error.expired-qr-code")
            else if (response?.error?.code == ErrorURN.MALFORMED_TOKEN.urn)
                createErrorAction(ex, "prompt.error.malformed-qr-code")
            else
                createErrorAction(ex, "prompt.error.unexpected-error")
        }
    }
}
