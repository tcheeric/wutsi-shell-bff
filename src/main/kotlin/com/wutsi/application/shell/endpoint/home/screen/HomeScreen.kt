package com.wutsi.application.shell.endpoint.home.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.profile.strength.ProfileStrengthContainer
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Center
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment.spaceAround
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.payment.WutsiPaymentApi
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.tenant.dto.Tenant
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class HomeScreen(
    private val paymentApi: WutsiPaymentApi,
    private val tenantProvider: TenantProvider,
    private val togglesProvider: TogglesProvider,
    private val profileStrength: ProfileStrengthContainer,

    @Value("\${wutsi.application.store-url}") private val storeUrl: String,
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val tenant = tenantProvider.get()
        val balance = getBalance(tenant)
        val me = securityContext.currentAccount()

        // Primary Buttons
        val children = mutableListOf<WidgetAware>(
            Container(
                alignment = Alignment.Center,
                background = Theme.COLOR_PRIMARY,
                child = Center(
                    child = Column(
                        mainAxisAlignment = MainAxisAlignment.center,
                        crossAxisAlignment = CrossAxisAlignment.center,
                        children = listOf(
                            Text(
                                getText("page.home.balance"),
                                color = Theme.COLOR_WHITE,
                            ),
                            MoneyText(
                                color = Theme.COLOR_WHITE,
                                value = balance.value,
                                currency = tenant.currencySymbol,
                                numberFormat = tenant.numberFormat,
                            )
                        ),
                    )
                ),
            ),
            Container(
                background = Theme.COLOR_PRIMARY,
                child = Row(
                    mainAxisAlignment = spaceAround,
                    children = primaryButtons(),
                )
            ),
        )
        val applications = applicationButtons(me)
        if (applications.isNotEmpty()) {
            children.addAll(
                toRows(applications, 4)
                    .map {
                        Container(
                            child = Row(
                                children = it,
                                mainAxisAlignment = spaceAround
                            )
                        )
                    }
            )
            children.add(Divider(color = Theme.COLOR_DIVIDER, height = 1.0))
        }

        val strength = profileStrength.toWidget(me)
        if (strength != null)
            children.add(strength)

        return Screen(
            id = Page.HOME,
            appBar = AppBar(
                foregroundColor = Theme.COLOR_WHITE,
                backgroundColor = Theme.COLOR_PRIMARY,
                elevation = 0.0,
                automaticallyImplyLeading = false,
            ),
            child = Column(children = children),
            bottomNavigationBar = bottomNavigationBar()
        ).toWidget()
    }

    // Buttons
    private fun primaryButtons(): List<WidgetAware> {
        val buttons = mutableListOf<WidgetAware>()
        if (togglesProvider.isScanEnabled()) {
            buttons.add(
                primaryButton(
                    caption = getText("page.home.button.scan"),
                    icon = Theme.ICON_SCAN,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build("scan")
                    ),
                ),
            )
        }
        if (togglesProvider.isAccountEnabled()) {
            buttons.add(
                primaryButton(
                    caption = getText("page.home.button.cashin"),
                    icon = Theme.ICON_CASHIN,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(cashUrl, "cashin")
                    )
                ),
            )
        }
        buttons.addAll(
            listOf(
                primaryButton(
                    caption = getText("page.home.button.send"),
                    icon = Theme.ICON_SEND,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(cashUrl, "send")
                    )
                ),
            )
        )
        if (togglesProvider.isPaymentEnabled())
            buttons.add(
                primaryButton(
                    caption = getText("page.home.button.payment"),
                    icon = Theme.ICON_MONEY,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(cashUrl, "pay")
                    )
                )
            )

        return buttons
    }

    private fun primaryButton(caption: String, icon: String, action: Action) = Button(
        type = ButtonType.Text,
        caption = caption,
        icon = icon,
        stretched = false,
        color = Theme.COLOR_WHITE,
        iconColor = Theme.COLOR_WHITE,
        padding = 1.0,
        action = action
    )

    private fun applicationButtons(me: Account): List<WidgetAware> {
        val buttons = mutableListOf<WidgetAware>()

        if (togglesProvider.isStoreEnabled())
            buttons.addAll(
                listOf(
                    applicationButton(
                        caption = getText("page.home.button.marketplace"),
                        icon = Theme.ICON_CART,
                        action = Action(
                            type = Route,
                            url = "$storeUrl/marketplace"
                        )
                    )
                )
            )

        buttons.add(
            applicationButton(
                caption = getText("page.home.button.contact"),
                icon = Theme.ICON_GROUP,
                action = Action(
                    type = Route,
                    url = urlBuilder.build("contact")
                )
            )
        )

        buttons.add(
            applicationButton(
                caption = getText("page.home.button.feedback"),
                icon = Theme.ICON_FEEDBACK,
                action = Action(
                    type = Route,
                    url = urlBuilder.build("feedback")
                )
            )
        )

        return buttons
    }

    private fun applicationButton(caption: String, icon: String, action: Action) = Button(
        type = ButtonType.Text,
        caption = caption,
        icon = icon,
        stretched = false,
        color = Theme.COLOR_PRIMARY,
        iconColor = Theme.COLOR_PRIMARY,
        padding = 1.0,
        action = action
    )

    private fun getBalance(tenant: Tenant): Money {
        try {
            val userId = securityContext.currentAccountId()
            val balance = paymentApi.getBalance(userId).balance
            return Money(
                value = balance.amount,
                currency = balance.currency
            )
        } catch (ex: Throwable) {
            return Money(currency = tenant.currency)
        }
    }

    private fun toRows(products: List<WidgetAware>, size: Int): List<List<WidgetAware>> {
        val rows = mutableListOf<List<WidgetAware>>()
        var cur = mutableListOf<WidgetAware>()
        products.forEach {
            cur.add(it)
            if (cur.size == size) {
                rows.add(cur)
                cur = mutableListOf()
            }
        }
        if (cur.isNotEmpty())
            rows.add(cur)
        return rows
    }
}
