package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.Theme
import com.wutsi.application.shell.service.TenantProvider
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.application.shell.service.UserProvider
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.DropdownButton
import com.wutsi.flutter.sdui.DropdownMenuItem
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.InputType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Locale

@RestController
@RequestMapping("/settings/profile")
class SettingsProfileScreen(
    private val urlBuilder: URLBuilder,
    private val userProvider: UserProvider,
    private val tenantProvider: TenantProvider,
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val user = userProvider.get()
        val tenant = tenantProvider.get()
        val locale = Locale(user.language)
        return Screen(
            id = Page.SETTINGS_PROFILE,
            backgroundColor = Theme.WHITE_COLOR,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.WHITE_COLOR,
                foregroundColor = Theme.BLACK_COLOR,
                title = getText("page.settings.profile.app-bar.title"),
            ),
            child = Form(
                children = listOf(
                    Container(
                        padding = 10.0,
                        child = Input(
                            name = "displayName",
                            maxLength = 100,
                            caption = getText("page.settings.profile.display-name.caption"),
                            hint = user.displayName,
                            value = user.displayName,
                            required = true
                        )
                    ),
                    Container(
                        padding = 10.0,
                        child = DropdownButton(
                            name = "language",
                            hint = getText("page.settings.profile.country.hint"),
                            value = user.language,
                            required = true,
                            children = tenant.languages.map {
                                DropdownMenuItem(
                                    caption = Locale(it).getDisplayLanguage(locale),
                                    value = it
                                )
                            }
                        )
                    ),
                    Container(
                        padding = 10.0,
                        child = DropdownButton(
                            name = "country",
                            value = user.country,
                            hint = getText("page.settings.profile.language.hint"),
                            required = true,
                            children = tenant.countries.map {
                                DropdownMenuItem(
                                    icon = "https://flagcdn.com/w20/${it.lowercase()}.png",
                                    caption = Locale(user.language, it).getDisplayCountry(locale),
                                    value = it
                                )
                            }.sortedBy { it.caption }
                        )
                    ),
                    Container(
                        padding = 10.0,
                        child = Input(
                            name = "submit",
                            type = InputType.Submit,
                            caption = getText("page.settings.profile.button.submit"),
                            action = Action(
                                type = ActionType.Command,
                                url = urlBuilder.build("commands/update-profile")
                            ),
                        ),
                    ),
                )
            ),
        ).toWidget()
    }
}
