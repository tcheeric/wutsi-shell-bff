package com.wutsi.application.shell.endpoint.profile.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.PhoneUtil
import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.SharedUIMapper
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.application.shared.ui.ProductCard
import com.wutsi.application.shared.ui.ProfileCard
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.CircleAvatar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextDecoration
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.catalog.WutsiCatalogApi
import com.wutsi.platform.catalog.dto.SearchProductRequest
import com.wutsi.platform.contact.WutsiContactApi
import com.wutsi.platform.contact.dto.SearchContactRequest
import com.wutsi.platform.tenant.dto.Tenant
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
    private val catalogApi: WutsiCatalogApi,
    private val securityContext: SecurityContext,
    private val sharedUIMapper: SharedUIMapper,
    private val togglesProvider: TogglesProvider,
    private val tenantProvider: TenantProvider,

    @Value("\${wutsi.application.cash-url}") private val cashUrl: String,
    @Value("\${wutsi.application.store-url}") private val storeUrl: String,
) : AbstractQuery() {

    @PostMapping
    fun index(@RequestParam id: Long): Widget {
        val user = accountApi.getAccount(id).account
        val tenant = tenantProvider.get()
        val children = mutableListOf<WidgetAware>(
            ProfileCard(
                model = sharedUIMapper.toAccountModel(user)
            )
        )
        addButtons(user, children)
        addProducts(user, tenant, 2, children)

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
                )
            ),
            child = SingleChildScrollView(
                child = Column(
                    children = children,
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.start,
                )
            )
        ).toWidget()
    }

    private fun addButtons(user: Account, children: MutableList<WidgetAware>) {
        if (!user.business)
            children.addAll(
                listOf(
                    Divider(color = Theme.COLOR_DIVIDER),
                    Container(
                        padding = 10.0,
                        child = Button(
                            type = ButtonType.Outlined,
                            caption = getText("page.profile.button.send"),
                            action = Action(
                                type = ActionType.Route,
                                url = urlBuilder.build(cashUrl, "send?recipient-id=${user.id}")
                            ),
                        )
                    )
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

    private fun addProducts(user: Account, tenant: Tenant, limit: Int, children: MutableList<WidgetAware>) {
        if (!togglesProvider.isStoreEnabled())
            return

        val products = catalogApi.searchProducts(
            request = SearchProductRequest(
                accountId = user.id,
                limit = limit
            )
        ).products
        if (products.isEmpty())
            return

        children.addAll(
            listOf(
                Divider(color = Theme.COLOR_DIVIDER),
                Container(
                    alignment = Alignment.TopLeft,
                    child = Column(
                        children = listOf(
                            Row(
                                children = products.map {
                                    Flexible(
                                        child = ProductCard(
                                            model = sharedUIMapper.toProductModel(it, tenant, messages),
                                            action = Action(
                                                type = ActionType.Route,
                                                url = urlBuilder.build(storeUrl, "product?id=${it.id}")
                                            )
                                        )
                                    )
                                }
                            ),
                        )
                    )
                ),
                Container(
                    child = Row(
                        mainAxisAlignment = MainAxisAlignment.end,
                        children = listOf(
                            Container(
                                padding = 10.0,
                                child = Text(
                                    caption = getText("page.profile.more-products"),
                                    color = Theme.COLOR_PRIMARY,
                                    decoration = TextDecoration.Underline
                                ),
                                action = Action(
                                    type = ActionType.Route,
                                    url = urlBuilder.build(storeUrl, "catalog?id=${user.id}")
                                ),
                            )
                        )
                    ),
                ),
            )
        )
    }
}
