package com.wutsi.application.shell.endpoint.settings.profile.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.settings.profile.dto.UpdateProfileRequest
import com.wutsi.application.shell.service.AccountService
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/update-profile")
class UpdateProfileCommand(
    private val accountService: AccountService
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestBody request: UpdateProfileRequest): ResponseEntity<Action> {
        accountService.updateProfile(request)
        val headers = HttpHeaders()
        headers["x-language"] = request.language
        return ResponseEntity
            .ok()
            .headers(headers)
            .body(
                Action(
                    type = ActionType.Route,
                    url = "route:/.."
                )
            )
    }
}
