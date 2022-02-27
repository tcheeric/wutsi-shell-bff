package com.wutsi.application.shell.endpoint.profile.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.PhoneUtil
import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.SharedUIMapper
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.application.shared.ui.ProfileCard
import com.wutsi.application.shared.ui.ProfileCardType
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.CircleAvatar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.DefaultTabController
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.DynamicWidget
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.TabBar
import com.wutsi.flutter.sdui.TabBarView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.DialogType
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
    private val tenantProvider: TenantProvider,

    @Value("\${wutsi.application.store-url}") private val storeUrl: String,
) : AbstractQuery() {

    @PostMapping
    fun index(
        @RequestParam id: Long
    ): Widget {
        val user = accountApi.getAccount(id).account
        val tenant = tenantProvider.get()

        val tabs = TabBar(
            tabs = listOfNotNull(
                Text(getText("page.profile.tab.about").uppercase(), bold = true),

                if (user.business && togglesProvider.isStoreEnabled())
                    Text(getText("page.profile.tab.store").uppercase(), bold = true)
                else
                    null,

                Text(getText("page.profile.tab.qr-code").uppercase(), bold = true),
            )
        )
        val tabViews = TabBarView(
            children = listOfNotNull(
                aboutTab(user),

                if (user.business && togglesProvider.isStoreEnabled())
                    storeTab(user)
                else
                    null,

                qrCodeTab(user)
            )
        )

        return DefaultTabController(
            length = tabs.tabs.size,
            child = Screen(
                id = Page.PROFILE,
                backgroundColor = Theme.COLOR_WHITE,
                appBar = AppBar(
                    elevation = 0.0,
                    backgroundColor = Theme.COLOR_PRIMARY,
                    foregroundColor = Theme.COLOR_WHITE,
                    title = user.displayName ?: getText("page.profile.app-bar.title"),
                    actions = listOfNotNull(
                        if (user.business && togglesProvider.isBusinessAccountEnabled())
                            PhoneUtil.toWhatsAppUrl(user.whatsapp)?.let {
                                Container(
                                    padding = 4.0,
                                    child = CircleAvatar(
                                        radius = 20.0,
                                        backgroundColor = Theme.COLOR_PRIMARY_LIGHT,
                                        child = IconButton(
                                            icon = Theme.ICON_CHAT,
                                            size = 20.0,
                                            action = Action(
                                                type = ActionType.Navigate,
                                                url = it,
                                            )
                                        ),
                                    ),
                                )
                            }
                        else
                            null,

                        if (canAddContact(user))
                            Container(
                                padding = 4.0,
                                child = CircleAvatar(
                                    radius = 20.0,
                                    backgroundColor = Theme.COLOR_PRIMARY_LIGHT,
                                    child = IconButton(
                                        icon = Theme.ICON_ADD_PERSON,
                                        size = 20.0,
                                        action = Action(
                                            type = ActionType.Command,
                                            url = urlBuilder.build("commands/add-contact?contact-id=${user.id}"),
                                            prompt = Dialog(
                                                type = DialogType.Confirm,
                                                title = getText("prompt.confirm.title"),
                                                message = getText(
                                                    "page.profile.confirm-add-contact",
                                                    arrayOf(user.displayName ?: "")
                                                )
                                            ).toWidget(),
                                        )
                                    ),
                                ),
                            )
                        else
                            null,

                        Container(
                            padding = 4.0,
                            child = CircleAvatar(
                                radius = 20.0,
                                backgroundColor = Theme.COLOR_PRIMARY_LIGHT,
                                child = IconButton(
                                    icon = Theme.ICON_SHARE,
                                    size = 20.0,
                                    action = Action(
                                        type = ActionType.Share,
                                        url = "${tenant.webappUrl}/profile?id=$id",
                                    )
                                ),
                            )
                        ),
                    ),
                    bottom = tabs
                ),
                child = tabViews
            )
        ).toWidget()
    }

    private fun aboutTab(user: Account): WidgetAware {
        val children = mutableListOf<WidgetAware>(
            ProfileCard(
                model = sharedUIMapper.toAccountModel(user),
                type = ProfileCardType.Full
            )
        )
        return Column(
            children = children,
            mainAxisAlignment = MainAxisAlignment.start,
            crossAxisAlignment = CrossAxisAlignment.start,
        )
    }

    private fun storeTab(user: Account) = DynamicWidget(
        url = urlBuilder.build(storeUrl, "widget?id=${user.id}")
    )

    private fun qrCodeTab(user: Account) = DynamicWidget(
        url = urlBuilder.build("profile/qr-code-widget?id=${user.id}")
    )

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
