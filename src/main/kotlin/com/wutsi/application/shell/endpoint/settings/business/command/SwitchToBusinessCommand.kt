package com.wutsi.application.shell.endpoint.settings.business.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.application.shell.service.UserProvider
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/switch-to-business")
class SwitchToBusinessCommand(
    private val accountApi: WutsiAccountApi,
    private val userProvider: UserProvider,
    private val urlBuilder: URLBuilder
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestParam(required = false) value: Boolean? = null): Action {
        accountApi.updateAccountAttribute(
            id = userProvider.id(),
            name = "business",
            request = UpdateAccountAttributeRequest(
                value = value?.toString() ?: "true"
            )
        )
        return Action(
            type = ActionType.Route,
            url = if (value == false)
                urlBuilder.build("settings")
            else
                urlBuilder.build("settings/business")
        )
    }
}
