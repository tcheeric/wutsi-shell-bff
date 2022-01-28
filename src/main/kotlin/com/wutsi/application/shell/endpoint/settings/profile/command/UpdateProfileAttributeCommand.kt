package com.wutsi.application.shell.endpoint.settings.profile.command

import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/update-profile-attribute")
class UpdateProfileAttributeCommand(
    private val accountApi: WutsiAccountApi,
    private val securityContext: SecurityContext,
    private val urlBuilder: URLBuilder,
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestParam name: String, @RequestBody request: UpdateAccountAttributeRequest): ResponseEntity<Action> {
        accountApi.updateAccountAttribute(
            id = securityContext.currentAccountId(),
            name = name,
            request = UpdateAccountAttributeRequest(
                value = request.value
            )
        )

        val headers = HttpHeaders()
        if (name == "language")
            headers["x-language"] = request.value
        return ResponseEntity
            .ok()
            .headers(headers)
            .body(
                Action(
                    type = ActionType.Route,
                    url = if (name == "business") urlBuilder.build("settings/profile") else "route:/..",
                    replacement = name == "business"
                )
            )
    }
}
