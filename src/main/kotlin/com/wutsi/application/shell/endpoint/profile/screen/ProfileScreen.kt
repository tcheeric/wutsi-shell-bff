package com.wutsi.application.shell.endpoint.profile.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.CategoryService
import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.application.shared.ui.ProfileCard
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
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
    private val categoryService: CategoryService,
    private val securityContext: SecurityContext,
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
            ),
            child = Column(
                mainAxisAlignment = MainAxisAlignment.start,
                crossAxisAlignment = CrossAxisAlignment.start,
                children = listOf(
                    ProfileCard(
                        account = user,
                        phoneNumber = user.phone?.number,
                        categoryService = categoryService,
                        togglesProvider = togglesProvider
                    ),
                    Divider(color = Theme.COLOR_DIVIDER),
                    buttons(user)
                )
            )
        ).toWidget()
    }

    private fun buttons(user: Account): WidgetAware {
        val buttons = mutableListOf<WidgetAware>()
        if (canAddContact(user))
            buttons.add(
                Button(
                    type = ButtonType.Outlined,
                    caption = getText("page.profile.button.add-contact"),
                    action = Action(
                        type = ActionType.Command,
                        url = urlBuilder.build("commands/add-contact?contact-id=${user.id}")
                    ),
                )
            )

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

//        if (user.business && user.whatsapp && togglesProvider.isBusinessAccountEnabled()) {
//            var phone = user.phone?.number
//            if (!phone.isNullOrEmpty()) {
//                if (phone.startsWith("+"))
//                    phone = phone.substring(1)
//
//                buttons.add(
//                    Button(
//                        caption = "WhatsApp",
//                        padding = 5.0,
//                        stretched = false,
//                        action = Action(
//                            type = ActionType.Navigate,
//                            url = "https://wa.me/$phone"
//                        ),
//                    )
//                )
//            }
//        }

        return Container(
            child = Column(
                children = buttons
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
