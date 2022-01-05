package com.wutsi.application.shell.endpoint.settings.business.screen

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.Theme
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.application.shell.service.UserProvider
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.InputType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/business/biography")
class SettingsBusinessBiographyScreen(
    private val urlBuilder: URLBuilder,
    private val userProvider: UserProvider
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val user = userProvider.get()
        return Screen(
            id = Page.SETTINGS_BUSINESS_BIOGRAPHY,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.business-biography.app-bar.title"),
            ),
            child = Form(
                children = listOf(
                    Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = Text(getText("page.settings.business-biography.sub-title"))
                    ),
                    Container(
                        padding = 20.0
                    ),
                    Container(
                        padding = 10.0,
                        child = Input(
                            name = "value",
                            value = user.biography,
                            maxLength = 160
                        ),
                    ),
                    Container(
                        padding = 20.0,
                        child = Input(
                            name = "submit",
                            type = InputType.Submit,
                            caption = getText("page.settings.business-biography.submit"),
                            action = Action(
                                type = ActionType.Command,
                                url = urlBuilder.build("commands/update-business-attribute?name=biography")
                            )
                        ),
                    ),
                )
            )
        ).toWidget()
    }
}
