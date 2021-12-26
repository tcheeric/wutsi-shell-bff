package com.wutsi.application.shell.endpoint.home.screen

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.Theme
import com.wutsi.application.shell.service.URLBuilder
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

    @Value("\${wutsi.application.cash-url}") private val cashUrl: String,
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
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
                ),
                leading = IconButton(
                    icon = Theme.ICON_HISTORY,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(cashUrl, "history")
                    )
                ),
            ),
            child = Column(
                children = listOf(
                    Container(
                        background = Theme.PRIMARY_COLOR,
                        child = Row(
                            mainAxisAlignment = spaceAround,
                            children = listOf(
                                Button(
                                    type = ButtonType.Text,
                                    caption = getText("page.home.button.scan"),
                                    icon = Theme.ICON_SCAN,
                                    stretched = false,
                                    color = Theme.WHITE_COLOR,
                                    iconColor = Theme.WHITE_COLOR,
                                    action = Action(
                                        type = Route,
                                        url = ""
                                    ),
                                ),
                                Button(
                                    type = ButtonType.Text,
                                    caption = getText("page.home.button.cashin"),
                                    icon = Theme.ICON_CASHIN,
                                    stretched = false,
                                    color = Theme.WHITE_COLOR,
                                    iconColor = Theme.WHITE_COLOR,
                                    action = Action(
                                        type = Route,
                                        url = urlBuilder.build(cashUrl, "cashin")
                                    )
                                ),
                                Button(
                                    type = ButtonType.Text,
                                    caption = getText("page.home.button.send"),
                                    icon = Theme.ICON_SEND,
                                    stretched = false,
                                    color = Theme.WHITE_COLOR,
                                    iconColor = Theme.WHITE_COLOR,
                                    action = Action(
                                        type = Route,
                                        url = urlBuilder.build(cashUrl, "send")
                                    )
                                ),
                                Button(
                                    type = ButtonType.Text,
                                    caption = getText("page.home.button.qr-code"),
                                    icon = Theme.ICON_QR_CODE,
                                    stretched = false,
                                    color = Theme.WHITE_COLOR,
                                    iconColor = Theme.WHITE_COLOR,
                                    action = Action(
                                        type = Route,
                                        url = urlBuilder.build(cashUrl, "send")
                                    )
                                ),
                            )
                        )
                    ),
                )
            ),
        ).toWidget()
    }
}
