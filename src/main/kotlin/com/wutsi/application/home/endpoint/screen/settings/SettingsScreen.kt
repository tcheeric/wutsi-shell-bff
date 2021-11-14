package com.wutsi.application.home.endpoint.screen.settings

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.application.home.endpoint.AbstractQuery
import com.wutsi.application.home.endpoint.Theme
import com.wutsi.application.home.service.UserProvider
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.Alignment.TopCenter
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings")
class SettingsScreen(
    private val userProvider: UserProvider,
    private val phoneNumberUtil: PhoneNumberUtil
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val user = userProvider.get()
        return Screen(
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.WHITE_COLOR,
                foregroundColor = Theme.BLACK_COLOR,
                title = getText("page.settings.app-bar.title")
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
                                caption = user.displayName ?: "",
                                alignment = TextAlignment.Center,
                                size = Theme.X_LARGE_TEXT_SIZE,
                                bold = true
                            )
                        ),
                        Container(
                            alignment = TopCenter,
                            padding = 10.0,
                            child = Text(
                                caption = formattedPhoneNumber(user) ?: "",
                                alignment = TextAlignment.Center,
                                size = Theme.LARGE_TEXT_SIZE,
                            )
                        ),
                        Container(
                            padding = 20.0
                        ),
                        Divider(color = Theme.DIVIDER_COLOR),
                        ListView(
                            children = listOf(
                                ListItem(
                                    caption = getText("page.settings.option.accounts.title"),
                                    subCaption = getText("page.settings.option.accounts.sub-title"),
                                    iconLeft = Theme.ICON_ACCOUNT,
                                    iconRight = Theme.ICON_CHEVRON_RIGHT
                                ),
                                Divider(color = Theme.DIVIDER_COLOR),
                            )
                        )
                    )
                )
            ),
        ).toWidget()
    }

    private fun formattedPhoneNumber(user: Account): String? {
        val phone = user.phone ?: return null
        val phoneNumber = phoneNumberUtil.parse(phone.number, phone.country)
        return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164)
    }
}
