package com.wutsi.application.home.endpoint.screen.settings.account

import com.wutsi.application.home.endpoint.AbstractQuery
import com.wutsi.application.home.endpoint.Page
import com.wutsi.application.home.endpoint.Theme
import com.wutsi.application.home.service.AccountService
import com.wutsi.application.home.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.PinWithKeyboard
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.Alignment.TopCenter
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/accounts/verify/mobile")
class VerifyAccountMobileScreen(
    private val urlBuilder: URLBuilder,
    private val service: AccountService
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val state = service.getSmsCodeEntity()
        return Screen(
            id = Page.SETTINGS_ACCOUNT_LINK_VERIFY,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.WHITE_COLOR,
                foregroundColor = Theme.BLACK_COLOR,
                title = getText("page.verify-account-mobile.app-bar.title")
            ),
            child = Container(
                padding = 20.0,
                alignment = Center,
                child = Column(
                    children = listOf(
                        Container(
                            alignment = Center,
                            padding = 10.0,
                            child = Text(
                                caption = getText("page.verify-account-mobile.title"),
                                alignment = TextAlignment.Center,
                                size = Theme.X_LARGE_TEXT_SIZE,
                                bold = true
                            )
                        ),
                        Container(
                            alignment = TopCenter,
                            padding = 10.0,
                            child = Text(
                                caption = getText("page.verify-account-mobile.sub-title", arrayOf(state.phoneNumber)),
                                alignment = TextAlignment.Center,
                                size = Theme.LARGE_TEXT_SIZE,
                            )
                        ),
                        Container(
                            alignment = TopCenter,
                            child = Button(
                                caption = getText("page.verify-account-mobile.button.resend"),
                                type = ButtonType.Text,
                                action = Action(
                                    type = ActionType.Command,
                                    url = urlBuilder.build("commands/resend-sms-code")
                                )
                            )
                        ),
                        Container(
                            child = PinWithKeyboard(
                                name = "code",
                                hideText = true,
                                pinSize = 20.0,
                                keyboardButtonSize = 90.0,
                                action = Action(
                                    type = ActionType.Command,
                                    url = urlBuilder.build("commands/verify-sms-code")
                                )
                            ),
                        )
                    )
                )
            ),
        ).toWidget()
    }
}
