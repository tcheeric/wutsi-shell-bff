package com.wutsi.application.shell.endpoint.profile.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.PhoneUtil
import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.SharedUIMapper
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.application.shared.ui.ProfileCard
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.CircleAvatar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.contact.WutsiContactApi
import com.wutsi.platform.contact.dto.SearchContactRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/profile")
class ProfileScreen(
    private val urlBuilder: URLBuilder,
    private val accountApi: WutsiAccountApi,
    private val contactApi: WutsiContactApi,
    private val securityContext: SecurityContext,
    private val sharedUIMapper: SharedUIMapper,
    private val togglesProvider: TogglesProvider,

    @Value("\${wutsi.application.cash-url}") private val cashUrl: String,
) : AbstractQuery() {

    @PostMapping
    fun index(@RequestParam id: Long): Widget {
        val user = accountApi.getAccount(id).account
        return Screen(
            id = Page.PROFILE,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_PRIMARY,
                foregroundColor = Theme.COLOR_WHITE,
                title = getText("page.profile.app-bar.title"),
                actions = listOfNotNull(
                    if (user.business && togglesProvider.isBusinessAccountEnabled())
                        PhoneUtil.toWhatsAppUrl(user.whatsapp)?.let {
                            Container(
                                padding = 4.0,
                                child = CircleAvatar(
                                    radius = 16.0,
                                    backgroundColor = Theme.COLOR_PRIMARY_LIGHT,
                                    child = IconButton(
                                        icon = Theme.ICON_CHAT,
                                        size = 16.0
                                    )
                                ),
                                action = Action(
                                    type = ActionType.Navigate,
                                    url = it,
                                )
                            )
                        }
                    else
                        null,

                    if (canAddContact(user))
                        Container(
                            padding = 4.0,
                            child = CircleAvatar(
                                radius = 16.0,
                                backgroundColor = Theme.COLOR_PRIMARY_LIGHT,
                                child = IconButton(
                                    icon = Theme.ICON_ADD_PERSON,
                                    size = 16.0
                                )
                            ),
                            action = Action(
                                type = ActionType.Command,
                                url = urlBuilder.build("commands/add-contact?contact-id=${user.id}")
                            )
                        )
                    else
                        null,
                )
            ),
            child = Column(
                mainAxisAlignment = MainAxisAlignment.start,
                crossAxisAlignment = CrossAxisAlignment.start,
                children = listOf(
                    ProfileCard(
                        model = sharedUIMapper.toAccountModel(user)
                    ),
                    Divider(color = Theme.COLOR_DIVIDER),
                    buttons(user)
                )
            )
        ).toWidget()
    }

    private fun buttons(user: Account): WidgetAware {
        val buttons = mutableListOf<WidgetAware>()

        if (!user.business)
            buttons.add(
                Button(
                    type = ButtonType.Outlined,
                    caption = getText("page.profile.button.send"),
                    action = Action(
                        type = ActionType.Route,
                        url = urlBuilder.build(cashUrl, "send?recipient-id=${user.id}")
                    ),
                )
            )

        return Container(
            child = Column(
                children = buttons.map {
                    Container(
                        padding = 10.0,
                        child = it
                    )
                }
            )
        )
    }

    private fun canAddContact(user: Account): Boolean =
        if (user.id == securityContext.currentAccountId())
            false
        else
            contactApi.searchContact(
                request = SearchContactRequest(
                    contactIds = listOf(user.id)
                )
            ).contacts.isEmpty()
}
