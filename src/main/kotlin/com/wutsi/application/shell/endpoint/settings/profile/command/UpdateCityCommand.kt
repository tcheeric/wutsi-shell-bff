package com.wutsi.application.shell.endpoint.settings.profile.command

import com.wutsi.application.shared.service.CityService
import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/update-city")
class UpdateCityCommand(
    private val accountApi: WutsiAccountApi,
    private val securityContext: SecurityContext,
    private val cityService: CityService
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestBody request: UpdateAccountAttributeRequest): Action {
        // Update the city-id
        accountApi.updateAccountAttribute(
            id = securityContext.currentAccountId(),
            name = "city-id",
            request = UpdateAccountAttributeRequest(
                value = request.value
            )
        )

        // Update the country
        val city = cityService.get(request.value?.toLong() ?: -1)
        if (city != null)
            accountApi.updateAccountAttribute(
                id = securityContext.currentAccountId(),
                name = "country",
                request = UpdateAccountAttributeRequest(
                    value = city.country
                )
            )

        return Action(
            type = ActionType.Route,
            url = "route:/..",
        )
    }
}
