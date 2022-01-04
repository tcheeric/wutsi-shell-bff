package com.wutsi.application.shell.endpoint.settings.screen

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.Theme
import com.wutsi.application.shell.service.TogglesProvider
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.application.shell.service.UserProvider
import com.wutsi.application.shell.util.StringUtil
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.CircleAvatar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
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
    private val togglesProvider: TogglesProvider,
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget = Screen(
        id = Page.SETTINGS,
        appBar = AppBar(
            elevation = 0.0,
            backgroundColor = Theme.COLOR_WHITE,
            foregroundColor = Theme.COLOR_BLACK,
            title = getText("page.settings.app-bar.title"),
            actions = listOf(
                IconButton(
                    icon = Theme.ICON_QR_CODE,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build("qr-code")
                    )
                )
            )
        ),
        child = Container(
            child = ListView(
                separator = true,
                separatorColor = Theme.COLOR_DIVIDER,
                children = listItems()
            ),
        )
    ).toWidget()

    private fun listItems(): List<WidgetAware> {
        val user = userProvider.get()
        val children = mutableListOf<WidgetAware>(
            Container(
                padding = 5.0,
                child = Column(
                    mainAxisSize = MainAxisSize.min,
                    crossAxisAlignment = CrossAxisAlignment.center,
                    mainAxisAlignment = MainAxisAlignment.spaceAround,
                    children = listOf(
                        picture(user),
                        Text(
                            caption = user.displayName ?: "",
                            alignment = TextAlignment.Center,
                            size = Theme.TEXT_SIZE_LARGE,
                            bold = true
                        ),
                        Text(
                            caption = formattedPhoneNumber(user) ?: "",
                            alignment = TextAlignment.Center,
                        ),
                    ),
                ),
            ),
            listItem(
                "page.settings.listitem.personal.caption",
                "page.settings.listitem.personal.subcaption",
                Theme.ICON_VERIFIED_USER,
                Theme.COLOR_PRIMARY,
                "settings/profile"
            ),
        )

        if (togglesProvider.isAccountEnabled()) {
            children.add(
                listItem(
                    "page.settings.listitem.account.caption",
                    "page.settings.listitem.account.subcaption",
                    Theme.ICON_MONEY,
                    Theme.COLOR_SUCCESS,
                    "settings/account"
                ),
            )
        }

        children.addAll(
            listOf(
                listItem(
                    "page.settings.listitem.security.caption",
                    "page.settings.listitem.security.subcaption",
                    Theme.ICON_LOCK,
                    Theme.COLOR_DANGER,
                    "settings/security"
                ),
                listItem(
                    "page.settings.listitem.about.caption",
                    null,
                    Theme.ICON_INFO,
                    Theme.COLOR_PRIMARY,
                    "settings/about"
                ),
            )
        )

        return children
    }

    private fun listItem(caption: String, subCaption: String?, icon: String, color: String, url: String) = ListItem(
        padding = 5.0,
        caption = getText(caption),
        subCaption = subCaption?.let { getText(it) },
        leading = Icon(
            code = icon,
            size = 32.0,
            color = color
        ),
        trailing = Icon(
            code = Theme.ICON_CHEVRON_RIGHT,
            size = 24.0,
            color = Theme.COLOR_BLACK
        ),
        action = Action(
            type = Route,
            url = urlBuilder.build(url)
        )
    )

    private fun formattedPhoneNumber(user: Account): String? {
        val phone = user.phone ?: return null
        return formattedPhoneNumber(phone.number, phone.country)
    }

    protected fun picture(user: Account): WidgetAware {
        val picture = if (!user.pictureUrl.isNullOrBlank())
            CircleAvatar(
                radius = 32.0,
                child = Image(
                    width = 64.0,
                    height = 64.0,
                    url = user.pictureUrl!!
                )
            )
        else
            CircleAvatar(
                radius = 32.0,
                child = Text(
                    caption = StringUtil.initials(user.displayName),
                    size = 30.0,
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
                        url = urlBuilder.build("settings/picture")
                    ),
                    padding = 10.0,
                )
            )
        )
    }
}
