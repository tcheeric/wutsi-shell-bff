package com.wutsi.application.shell.endpoint.settings.security.screen

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.Theme
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.PinWithKeyboard
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/security/pin")
class SettingsPINScreen(
    private val urlBuilder: URLBuilder
) : AbstractQuery() {
    @PostMapping
    fun index() = Screen(
        id = Page.SETTINGS_SECURITY_PIN,
        backgroundColor = Theme.PRIMARY_COLOR,
        appBar = AppBar(
            elevation = 0.0,
            backgroundColor = Theme.PRIMARY_COLOR,
            foregroundColor = Theme.WHITE_COLOR,
            title = getText("page.settings.pin.app-bar.title")
        ),
        child = Container(
            alignment = Alignment.Center,
            child = Column(
                children = listOf(
                    Container(
                        alignment = Alignment.TopCenter,
                        padding = 10.0,
                        child = Text(
                            caption = getText("page.settings.pin.sub-title"),
                            alignment = TextAlignment.Center,
                            size = Theme.LARGE_TEXT_SIZE,
                            color = Theme.WHITE_COLOR,
                        )
                    ),
                    PinWithKeyboard(
                        name = "pin",
                        hideText = true,
                        maxLength = 6,
                        color = Theme.WHITE_COLOR,
                        action = Action(
                            type = ActionType.Command,
                            url = urlBuilder.build("commands/change-pin")
                        ),
                    ),
                )
            )
        )
    ).toWidget()
}
