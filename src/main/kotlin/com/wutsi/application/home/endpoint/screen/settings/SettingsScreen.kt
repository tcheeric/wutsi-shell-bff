package com.wutsi.application.home.endpoint.screen.settings

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.application.home.endpoint.AbstractQuery
import com.wutsi.application.home.endpoint.Page
import com.wutsi.application.home.endpoint.Theme
import com.wutsi.application.home.service.URLBuilder
import com.wutsi.application.home.service.UserProvider
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType.Route
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
                child = Column(
                    children = listOf(
                        Container(
                            alignment = Alignment.topCenter,
                            padding: const EdgeInsets.all(20),
                            child = Column(
                                children = listOf(
                                    Container(
                                        child = CircleAvatar(
                                            radius = BorderRadius.circular(64),
                                            child = Image(
                                                imageUrl = user.pictureUrl,                                                
                                                width = 128, 
                                                height = 128
                                            ) 
                                        )
                                    ),
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
                                    )
                                )
                            )
                        ),
                        Container(
                            padding = 20.0
                        ),
                        Flexible(
                            flex = 4,
                            child = ListView(
                                children = listOf(
                                    ListTile(
                                        leading = Icon(Icons.verified_user, color: Colors.blue),
                                        trailing = Icon(Icons.chevron_right),
                                        title = Text('Personal'),
                                        subtitle = Text('Edit your personal information'),
                                        onTap: () =>
                                            {Navigator.pushNamed(context, '/settings/personal')},
                                    ),
                                    Divider(),
                                    ListTile(
                                        leading = Icon(Icons.verified_user, color: Colors.blue),
                                        trailing = Icon(Icons.chevron_right),
                                        title = Text('Accounts'),
                                        subtitle = Text('Manage your accounts for payments'),
                                        onTap: () =>
                                            {Navigator.pushNamed(context, '/settings/account')},
                                    ),
                                    Divider(),
                                    ListTile(
                                        leading = Icon(Icons.verified_user, color: Colors.blue),
                                        trailing = Icon(Icons.chevron_right),
                                        title = Text('Personal'),
                                        subtitle = Text('Edit your personal information'),
                                        onTap: () =>
                                            {Navigator.pushNamed(context, '/settings/security')},
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            child = Container(
                padding = 20.0,
                alignment = Center,
                child = Column(
                    children = listOf(
                        Container(
                            alignment = TopCenter,
                            padding = 10.0,
                            child = Button(
                                caption = "Account",
                                action = Action(
                                    type = Route,
                                    url = urlBuilder.build("settings/account"),
                                )
                            )
                        ),  )
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
