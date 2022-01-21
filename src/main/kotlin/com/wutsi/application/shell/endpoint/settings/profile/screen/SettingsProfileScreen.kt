package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.StringUtil
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
    private val securityContext: SecurityContext,
    private val tenantProvider: TenantProvider,
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val user = securityContext.currentAccount()
        val tenant = tenantProvider.get()
        val locale = Locale(user.language)
        return Screen(
            id = Page.SETTINGS_PROFILE,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
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
                            hint = getText("page.settings.profile.language.hint"),
                            value = user.language,
                            required = true,
                            children = tenant.languages.map {
                                DropdownMenuItem(
                                    caption = StringUtil.capitalizeFirstLetter(Locale(it).getDisplayLanguage(locale)),
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
                            hint = getText("page.settings.profile.country.hint"),
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
