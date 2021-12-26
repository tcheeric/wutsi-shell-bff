package com.wutsi.application.shell.endpoint.home.screen

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.Theme
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.application.shell.service.UserProvider
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.MainAxisAlignment.spaceAround
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class HomeScreen(
    private val urlBuilder: URLBuilder,
    private val userProvider: UserProvider,

    @Value("\${wutsi.application.cash-url}") private val cashUrl: String,
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val me = userProvider.get()

        return Screen(
            id = Page.HOME,
            appBar = AppBar(
                foregroundColor = Theme.WHITE_COLOR,
                backgroundColor = Theme.PRIMARY_COLOR,
                elevation = 0.0,
                automaticallyImplyLeading = false,
                actions = listOf(
                    IconButton(
                        icon = Theme.ICON_SETTINGS,
                        action = Action(
                            type = Route,
                            url = urlBuilder.build("settings")
                        )
                    )
                )
            ),
            child = Column(
                children = listOf(
                    Container(
                        background = Theme.PRIMARY_COLOR,
                        child = Row(
                            mainAxisAlignment = spaceAround,
                            children = listOf(
                                primaryButton(
                                    caption = getText("page.home.button.scan"),
                                    icon = Theme.ICON_SCAN,
                                    action = Action(
                                        type = Route,
                                        url = ""
                                    ),
                                ),
                                primaryButton(
                                    caption = getText("page.home.button.cashin"),
                                    icon = Theme.ICON_CASHIN,
                                    action = Action(
                                        type = Route,
                                        url = urlBuilder.build(cashUrl, "cashin")
                                    )
                                ),
                                primaryButton(
                                    caption = getText("page.home.button.send"),
                                    icon = Theme.ICON_SEND,
                                    action = Action(
                                        type = Route,
                                        url = urlBuilder.build(cashUrl, "send")
                                    )
                                ),
                                primaryButton(
                                    caption = getText("page.home.button.qr-code"),
                                    icon = Theme.ICON_QR_CODE,
                                    action = Action(
                                        type = Route,
                                        url = urlBuilder.build(cashUrl, "send")
                                    )
                                ),
                            )
                        )
                    ),
                    Container(
                        child = Row(
                            mainAxisAlignment = spaceAround,
                            children = listOf(
                                secondaryButton(
                                    caption = getText("page.home.button.history"),
                                    icon = Theme.ICON_HISTORY,
                                    action = Action(
                                        type = Route,
                                        url = urlBuilder.build(cashUrl, "history")
                                    )
                                ),
                                secondaryButton(
                                    caption = getText("page.home.button.settings"),
                                    icon = Theme.ICON_SETTINGS,
                                    action = Action(
                                        type = Route,
                                        url = urlBuilder.build("settings")
                                    )
                                ),
                            )
                        )
                    )
                )
            ),
        ).toWidget()
    }

    private fun primaryButton(caption: String, icon: String, action: Action) = Button(
        type = ButtonType.Text,
        caption = caption,
        icon = icon,
        stretched = false,
        color = Theme.WHITE_COLOR,
        iconColor = Theme.WHITE_COLOR,
        action = action
    )

    private fun secondaryButton(caption: String, icon: String, action: Action) = Button(
        type = ButtonType.Text,
        caption = caption,
        icon = icon,
        stretched = false,
        color = Theme.BLACK_COLOR,
        iconColor = Theme.PRIMARY_COLOR,
        action = action
    )
}
