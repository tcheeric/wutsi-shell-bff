package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.CityService
import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.SharedUIMapper
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.DropdownButton
import com.wutsi.flutter.sdui.DropdownMenuItem
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.InputType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/profile/city")
class SettingsProfileCityScreen(
    private val urlBuilder: URLBuilder,
    private val securityContext: SecurityContext,
    private val cityService: CityService,
    private val tenantProvider: TenantProvider,
    private val sharedUIMapper: SharedUIMapper,
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val user = securityContext.currentAccount()
        val tenant = tenantProvider.get()
        val items = mutableListOf<DropdownMenuItem>()
        items.add(
            DropdownMenuItem("", "")
        )
        items.addAll(
            cityService.search(null, tenant.countries)
                .sortedBy { sharedUIMapper.toLocationText(it, user.country) }
                .map {
                    DropdownMenuItem(
                        caption = sharedUIMapper.toLocationText(it, user.country),
                        value = it.id.toString()
                    )
                }
        )

        return Screen(
            id = Page.SETTINGS_PROFILE_CITY,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.profile.attribute.city"),
            ),
            child = Form(
                children = listOf(
                    Container(
                        padding = 10.0,
                        child = Text(getText("page.settings.profile.attribute.city.description"))
                    ),
                    Container(padding = 20.0),
                    Container(
                        padding = 10.0,
                        child = DropdownButton(
                            name = "value",
                            value = user.cityId?.toString(),
                            children = items
                        ),
                    ),
                    Container(
                        padding = 10.0,
                        child = Input(
                            name = "submit",
                            type = InputType.Submit,
                            caption = getText("page.settings.profile.attribute.button.submit"),
                            action = Action(
                                type = ActionType.Command,
                                url = urlBuilder.build("commands/update-city")
                            )
                        ),
                    ),
                )
            )
        ).toWidget()
    }
}
