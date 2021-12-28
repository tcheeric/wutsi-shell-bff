package com.wutsi.application.shell.endpoint.pay.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.pay.dto.ScanRequest
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/scan")
class ScanCommand(
    private val logger: KVLogger
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestBody request: ScanRequest) {
        logger.add("qr_format", request.format)
        logger.add("qr_code", request.code)
    }
}
