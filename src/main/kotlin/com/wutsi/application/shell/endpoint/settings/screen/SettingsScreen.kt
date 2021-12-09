package com.wutsi.application.shell.endpoint.settings.screen

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.Theme
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.application.shell.service.UserProvider
import com.wutsi.application.shell.util.StringUtil
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.CircleAvatar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.MainAxisSize
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings")
class SettingsScreen(
    private val urlBuilder: URLBuilder,
    private val userProvider: UserProvider,
    private val phoneNumberUtil: PhoneNumberUtil
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val user = userProvider.get()
        return Screen(
            id = Page.SETTINGS,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.WHITE_COLOR,
                foregroundColor = Theme.BLACK_COLOR,
                title = getText("page.settings.app-bar.title")
            ),
            child = Container(
                padding = 5.0,
                alignment = Center,
                child = Column(
                    children = listOf(
                        Container(
                            child = icon(user.pictureUrl, user),
                        ),
                        Container(
                            alignment = Center,
                            padding = 5.0,
                            child = Text(
                                caption = user.displayName ?: "",
                                alignment = TextAlignment.Center,
                                size = Theme.X_LARGE_TEXT_SIZE,
                                bold = true
                            )
                        ),
                        Container(
                            alignment = Center,
                            child = Text(
                                caption = formattedPhoneNumber(user) ?: "",
                                alignment = TextAlignment.Center,
                                size = Theme.LARGE_TEXT_SIZE,
                            )
                        ),
                        Flexible(
                            flex = 2,
                            child = ListView(
                                separator = true,
                                children = listOf(
                                    ListItem(
                                        padding = 5.0,
                                        caption = getText("page.settings.listitem.personal.caption"),
                                        subCaption = getText("page.settings.listitem.personal.subcaption"),
                                        leading = Icon(
                                            code = Theme.ICON_VERIFIED_USER,
                                            size = 32.0,
                                            color = Theme.PRIMARY_COLOR
                                        ),
                                        trailing = Icon(
                                            code = Theme.ICON_CHEVRON_RIGHT,
                                            size = 24.0,
                                            color = Theme.BLACK_COLOR
                                        ),
                                        action = Action(
                                            type = Route,
                                            url = urlBuilder.build("settings/account")
                                        )
                                    ),
                                    ListItem(
                                        padding = 5.0,
                                        caption = getText("page.settings.listitem.account.caption"),
                                        subCaption = getText("page.settings.listitem.account.subcaption"),
                                        leading = Icon(
                                            code = Theme.ICON_ADD_CASH,
                                            size = 32.0,
                                            color = Theme.SUCCESS_COLOR
                                        ),
                                        trailing = Icon(
                                            code = Theme.ICON_CHEVRON_RIGHT,
                                            size = 24.0,
                                            color = Theme.BLACK_COLOR
                                        ),
                                        action = Action(
                                            type = Route,
                                            url = urlBuilder.build("settings/account")
                                        )
                                    ),
                                    ListItem(
                                        padding = 5.0,
                                        caption = getText("page.settings.listitem.security.caption"),
                                        subCaption = getText("page.settings.listitem.security.subcaption"),
                                        leading = Icon(
                                            code = Theme.ICON_LOCK,
                                            size = 32.0,
                                            color = Theme.DANGER_COLOR
                                        ),
                                        trailing = Icon(
                                            code = Theme.ICON_CHEVRON_RIGHT,
                                            size = 24.0,
                                            color = Theme.BLACK_COLOR
                                        ),
                                        action = Action(
                                            type = Route,
                                            url = urlBuilder.build("settings/security")
                                        )
                                    ),
                                )
                            ),
                        )
                    )
                )
            )
        ).toWidget()
    }

    private fun formattedPhoneNumber(user: Account): String? {
        val phone = user.phone ?: return null
        return formattedPhoneNumber(phone.number, phone.country)
    }

    private fun icon(pictureUrl: String?, user: Account): WidgetAware {
        val picture = if (pictureUrl != null)
            CircleAvatar(
                radius = 64.0,
                child = Image(
                    width = 128.0,
                    height = 128.0,
                    url = pictureUrl
                )
            )
        else
            CircleAvatar(
                radius = 64.0,
                child = Text(
                    caption = StringUtil.initials(user.displayName),
                    size = 2.0 * Theme.LARGE_TEXT_SIZE,
                    bold = true
                )
            )

        return Column(
            mainAxisSize = MainAxisSize.min,
            children = listOf(
                picture,
                Button(
                    type = ButtonType.Text,
                    caption = getText("page.settings.button.change-picture"),
                    action = Action(
                        type = ActionType.Route,
                        url = urlBuilder.build("/settings/picture")
                    )
                )
            )
        )
    }
}
