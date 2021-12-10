package com.wutsi.application.shell.endpoint.settings.security.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.settings.security.dto.UpdateAccountAttributeRequest
import com.wutsi.application.shell.service.AccountService
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/transfer-secured")
class TransferSecuredCommand(
    private val service: AccountService,
    private val urlBuilder: URLBuilder,
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestBody request: UpdateAccountAttributeRequest): Action {
        service.setTransferSecured(request)
        return Action(
            type = ActionType.Route,
            url = urlBuilder.build("settings/security")
        )
    }
}
