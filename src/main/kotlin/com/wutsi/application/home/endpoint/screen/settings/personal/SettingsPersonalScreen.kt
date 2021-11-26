package com.wutsi.application.home.endpoint.screen.settings.account

import com.wutsi.application.home.endpoint.AbstractQuery
import com.wutsi.application.home.endpoint.Page
import com.wutsi.application.home.endpoint.Theme
import com.wutsi.application.home.service.AccountService
import com.wutsi.application.home.service.PaymentService
import com.wutsi.application.home.service.TenantProvider
import com.wutsi.application.home.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.Alignment.TopCenter
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisSize
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.tenant.dto.Tenant
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/personal")
class SettingsPersonalScreen(
    private val urlBuilder: URLBuilder,
    private val tenantProvider: TenantProvider,
    private val accountService: AccountService,
    private val paymentService: PaymentService,

    @Value("\${wutsi.application.cash-url}") private val cashUrl: String,
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val tenant = tenantProvider.get()
        val paymentMethods = accountService.getPaymentMethods(tenant)

        return Screen(
            id = Page.SETTINGS_ACCOUNT,
            backgroundColor = Theme.WHITE_COLOR,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.WHITE_COLOR,
                foregroundColor = Theme.BLACK_COLOR,
                title = getText("page.settings.account.app-bar.title"),
            ),
            child = Column(
                children = listOf(
                    Container(
                        padding = 10.0,
                        alignment = TopCenter,
                        child = Column(
                            mainAxisSize = MainAxisSize.min,
                            children = listOf(
                                Container(
                                    alignment = Center,
                                    child = Text(
                                        caption = getText("page.settings.account.your-balance"),
                                        color = Theme.BLACK_COLOR,
                                        alignment = TextAlignment.Center
                                    )
                                ),
                                balance(paymentMethods, tenant)
                            )
                        ),
                        borderColor = "#ff0000",
                    ),
                    Divider(color = Theme.DIVIDER_COLOR),
                    Flexible(
                        child = Container(
                            margin = 10.0,
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
                    caption = it.maskedNumber,
                    iconLeft = accountService.getLogoUrl(tenant, it),
                    iconRight = Theme.ICON_CHEVRON_RIGHT,
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
        return ListView(children = children)
    }

    private fun balance(paymentMethods: List<PaymentMethodSummary>, tenant: Tenant): WidgetAware {
        val balance = paymentService.getBalance(tenant)
        return if (paymentMethods.isEmpty()) {
            Container(
                alignment = Center,
                padding = 10.0,
                child = MoneyText(
                    value = balance.value,
                    currency = balance.currency,
                    color = Theme.BLACK_COLOR,
                    numberFormat = tenant.numberFormat,
                )
            )
        } else {
            Column(
                children = listOf(
                    Container(
                        alignment = Center,
                        padding = 10.0,
                        child = MoneyText(
                            value = balance.value,
                            currency = balance.currency,
                            color = Theme.BLACK_COLOR,
                            numberFormat = tenant.numberFormat
                        )
                    ),
                    Row(
                        mainAxisAlignment = MainAxisAlignment.spaceAround,
                        crossAxisAlignment = CrossAxisAlignment.center,
                        children = balanceButtons(balance)
                    ),
                ),
                mainAxisAlignment = MainAxisAlignment.center,
                crossAxisAlignment = CrossAxisAlignment.center,
                mainAxisSize = MainAxisSize.min
            )
        }
    }

    private fun balanceButtons(balance: Money): List<WidgetAware> {
        val buttons = mutableListOf<WidgetAware>()
        buttons.add(
            Button(
                caption = getText("page.settings.account.button.add-cash"),
                padding = 5.0,
                stretched = false,
                action = Action(
                    type = Route,
                    url = urlBuilder.build(cashUrl, "cashin")
                ),
                type = ButtonType.Text
            )
        )
        if (balance.value > 0) {
            buttons.add(
                Button(
                    caption = getText("page.settings.account.button.cash-out"),
                    padding = 5.0,
                    stretched = false,
                    action = Action(
                        type = Route,
                        url = urlBuilder.build(cashUrl, "cashout")
                    ),
                    type = ButtonType.Text
                )
            )
        }
        return buttons
    }
}
