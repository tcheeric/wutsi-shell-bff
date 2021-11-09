package com.wutsi.application.home.endpoint.home

import com.wutsi.application.home.endpoint.AbstractQuery
import com.wutsi.application.home.endpoint.Theme
import com.wutsi.application.home.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType.Prompt
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.flutter.sdui.enums.Alignment.BottomCenter
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment.spaceEvenly
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class HomeScreen(
    private val urlBuilder: URLBuilder
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        return Screen(
            appBar = AppBar(
                foregroundColor = Theme.HOME_TEXT_COLOR,
                backgroundColor = Theme.HOME_BACKGROUND_COLOR,
                elevation = 0.0,
                actions = listOf(
                    IconButton(
                        icon = Theme.ICON_SETTINGS,
                        action = Action(
                            type = Prompt,
                            prompt = Dialog(message = "Not implemented yet")
                        )
                    )
                )
            ),
            backgroundColor = Theme.HOME_BACKGROUND_COLOR,
            child = Column(
                children = listOf(
                    Container(
                        padding = 10.0,
                        alignment = Center,
                        child = Column(
                            children = listOf(
                                Container(
                                    alignment = Center,
                                    background = Theme.HOME_BACKGROUND_COLOR,
                                    child = Text(
                                        caption = getText("page.home.your-balance"),
                                        color = Theme.HOME_TEXT_COLOR,
                                        alignment = TextAlignment.Center
                                    )
                                ),
                                Container(
                                    alignment = Center,
                                    background = Theme.HOME_BACKGROUND_COLOR,
                                    padding = 10.0,
                                    child = MoneyText(value = 0.0, currency = "XAF", color = Theme.HOME_TEXT_COLOR)
                                ),
                            )
                        ),
                        borderColor = "#ff0000"
                    ),
                    Flexible(
                        child = Container(
                            margin = 30.0,
                            background = Theme.HOME_TEXT_COLOR,
                            borderColor = Theme.DIVIDER_COLOR,
                            child = accounts(),
                            alignment = BottomCenter
                        )
                    )
                ),
                crossAxisAlignment = CrossAxisAlignment.center,
                mainAxisAlignment = spaceEvenly
            ),
        ).toWidget()
    }

    private fun accounts() = Column(
        children = listOf(
            Container(
                padding = 20.0,
                alignment = Center,
                child = Text(caption = getText("page.home.your-accounts"), bold = true)
            ),
            Divider(color = Theme.DIVIDER_COLOR),
            Container(
                padding = 20.0,
                alignment = Center,
                child = Button(
                    caption = getText("page.home.button.add-account"),
                    action = Action(
                        type = Route,
                        url = urlBuilder.build("settings/accounts/link/mobile")
                    )
                )
            ),
        )
    )
}
