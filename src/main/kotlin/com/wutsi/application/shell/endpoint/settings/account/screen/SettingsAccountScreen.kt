package com.wutsi.application.shell.endpoint.settings.account.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.service.AccountService
import com.wutsi.application.shell.service.PaymentService
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.tenant.dto.Tenant
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.text.DecimalFormat

@RestController
@RequestMapping("/settings/account")
class SettingsAccountScreen(
    private val tenantProvider: TenantProvider,
    private val accountService: AccountService,
    private val paymentService: PaymentService,
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val tenant = tenantProvider.get()
        val paymentMethods = accountService.getPaymentMethods(tenant)
        val balance = paymentService.getBalance(tenant)
        val balanceText = DecimalFormat(tenant.monetaryFormat).format(balance.value)

        return Screen(
            id = Page.SETTINGS_ACCOUNT,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.account.app-bar.title"),
            ),
            child = Column(
                children = listOf(
                    Container(
                        padding = 10.0,
                        alignment = Center,
                        child = Text(
                            getText("page.settings.account.your-balance", arrayOf(balanceText)),
                            size = Theme.TEXT_SIZE_LARGE
                        )
                    ),
                    Container(
                        padding = 10.0,
                        alignment = Center,
                        child = toolbar(balance, paymentMethods)
                    ),
                    Divider(color = Theme.COLOR_DIVIDER),
                    Flexible(
                        child = Container(
                            alignment = Alignment.TopCenter,
                            child = accountListWidget(paymentMethods, tenant),
                        )
                    )
                ),
                crossAxisAlignment = CrossAxisAlignment.center,
            ),
        ).toWidget()
    }

    private fun accountListWidget(paymentMethods: List<PaymentMethodSummary>, tenant: Tenant): WidgetAware {
        val children = mutableListOf<WidgetAware>()
        children.addAll(
            paymentMethods.map {
                ListItem(
                    caption = formattedPhoneNumber(it.phone?.number, it.phone?.country) ?: it.maskedNumber,
                    iconLeft = accountService.getLogoUrl(tenant, it),
                    padding = 10.0
                )
            }
        )
        children.add(
            Container(
                padding = 20.0,
                alignment = Center,
                child = Button(
                    caption = getText("page.settings.account.button.add-account"),
                    action = Action(
                        type = Route,
                        url = urlBuilder.build("settings/accounts/link/mobile")
                    )
                )
            )
        )
        return ListView(
            children = children,
            separatorColor = Theme.COLOR_DIVIDER,
            separator = true,
        )
    }

    private fun toolbar(balance: Money, paymentMethods: List<PaymentMethodSummary>): WidgetAware {
        if (paymentMethods.isEmpty())
            return Container()

        val buttons = mutableListOf<WidgetAware>()
        buttons.add(
            Button(
                type = ButtonType.Text,
                caption = getText("page.settings.account.button.add-cash"),
                stretched = false,
                action = Action(
                    type = Route,
                    url = urlBuilder.build(cashUrl, "cashin")
                ),
            )
        )
        if (balance.value > 0) {
            buttons.add(
                Button(
                    type = ButtonType.Text,
                    caption = getText("page.settings.account.button.cash-out"),
                    stretched = false,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(cashUrl, "cashout")
                    ),
                )
            )
        }
        buttons.add(
            Button(
                type = ButtonType.Text,
                caption = getText("page.settings.account.button.history"),
                stretched = false,
                action = Action(
                    type = Route,
                    url = urlBuilder.build(cashUrl, "history")
                ),
            )
        )
        return Row(
            mainAxisAlignment = MainAxisAlignment.spaceAround,
            crossAxisAlignment = CrossAxisAlignment.center,
            children = buttons
        )
    }
}
