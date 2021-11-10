package com.wutsi.application.home.endpoint.screen.home

import com.wutsi.application.home.endpoint.AbstractQuery
import com.wutsi.application.home.endpoint.Theme
import com.wutsi.application.home.service.AccountService
import com.wutsi.application.home.service.TenantProvider
import com.wutsi.application.home.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType.Prompt
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.flutter.sdui.enums.Alignment.BottomCenter
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.Alignment.TopCenter
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.DialogType.Information
import com.wutsi.flutter.sdui.enums.MainAxisAlignment.center
import com.wutsi.flutter.sdui.enums.MainAxisSize.min
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.tenant.dto.Tenant
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class HomeScreen(
    private val urlBuilder: URLBuilder,
    private val tenantProvider: TenantProvider,
    private val accountService: AccountService
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val tenant = tenantProvider.get()
        val paymentMethods = accountService.getPaymentMethods(tenant)

        return Screen(
            appBar = AppBar(
                foregroundColor = Theme.HOME_TEXT_COLOR,
                backgroundColor = Theme.HOME_BACKGROUND_COLOR,
                elevation = 0.0,
                actions = listOf(
                    IconButton(
                        icon = Theme.ICON_SETTINGS,
                        action = Action(
                            type = Prompt,
                            prompt = Dialog(message = "Not implemented yet")
                        )
                    )
                )
            ),
            backgroundColor = Theme.HOME_BACKGROUND_COLOR,
            child = Column(
                children = listOf(
                    Flexible(
                        flex = 3,
                        child = Container(
                            padding = 10.0,
                            alignment = TopCenter,
                            child = Column(
                                mainAxisSize = min,
                                children = listOf(
                                    Container(
                                        alignment = Center,
                                        background = Theme.HOME_BACKGROUND_COLOR,
                                        child = Text(
                                            caption = getText("page.home.your-balance"),
                                            color = Theme.HOME_TEXT_COLOR,
                                            alignment = TextAlignment.Center
                                        )
                                    ),
                                    balance(paymentMethods, tenant)
                                )
                            ),
                            borderColor = "#ff0000",
                        ),
                    ),
                    Flexible(
                        flex = 9,
                        child = Container(
                            margin = 30.0,
                            background = Theme.HOME_TEXT_COLOR,
                            borderColor = Theme.DIVIDER_COLOR,
                            alignment = BottomCenter,
                            borderRadius = 20.0,
                            child = if (paymentMethods.isEmpty())
                                emptyAccountWidget()
                            else
                                accountListWidget(paymentMethods, tenant),
                        )
                    )
                ),
                crossAxisAlignment = CrossAxisAlignment.center,
            ),
        ).toWidget()
    }

    private fun balance(paymentMethods: List<PaymentMethodSummary>, tenant: Tenant): WidgetAware =
        if (paymentMethods.isEmpty()) {
            Container(
                alignment = Center,
                background = Theme.HOME_BACKGROUND_COLOR,
                padding = 10.0,
                child = MoneyText(value = 0.0, currency = tenant.currency, color = Theme.HOME_TEXT_COLOR)
            )
        } else {
            Column(
                children = listOf(
                    Container(
                        alignment = Center,
                        background = Theme.HOME_BACKGROUND_COLOR,
                        padding = 10.0,
                        child = MoneyText(value = 0.0, currency = tenant.currency, color = Theme.HOME_TEXT_COLOR)
                    ),
                    Button(
                        caption = getText("page.home.button.add-cash"),
                        padding = 5.0,
                        stretched = false,
                        action = Action(
                            type = Prompt,
                            prompt = Dialog(
                                type = Information,
                                message = "Not implemented"
                            )
                        )
                    )
                ),
                mainAxisAlignment = center,
                crossAxisAlignment = CrossAxisAlignment.center,
                mainAxisSize = min
            )
        }

    private fun accountListWidget(paymentMethods: List<PaymentMethodSummary>, tenant: Tenant): WidgetAware {
        val children = mutableListOf<WidgetAware>()
        children.add(accountHeader())
        children.add(Divider(color = Theme.DIVIDER_COLOR))

        paymentMethods.forEach {
            children.add(
                Row(
                    children = listOf(
                        Container(
                            padding = 5.0,
                            child = accountService.getLogoUrl(tenant, it)
                                ?.let { Image(url = it, width = 48.0, height = 48.0) }
                                ?: Icon(code = Theme.ICON_MONEY, size = 48.0)
                        ),
                        Container(
                            alignment = Center,
                            padding = 5.0,
                            child = Text(caption = it.maskedNumber, bold = true)
                        )
                    )
                )
            )
            children.add(Divider(color = Theme.DIVIDER_COLOR))
        }
        return Column(children = children)
    }

    private fun emptyAccountWidget(): WidgetAware = Column(
        children = listOf(
            accountHeader(),
            Divider(color = Theme.DIVIDER_COLOR),
            Container(
                padding = 20.0,
                alignment = Center,
                child = Button(
                    caption = getText("page.home.button.add-account"),
                    action = Action(
                        type = Route,
                        url = urlBuilder.build("settings/accounts/link/mobile")
                    )
                )
            ),
        )
    )

    private fun accountHeader() = Container(
        padding = 20.0,
        alignment = Center,
        child = Text(caption = getText("page.home.your-accounts"), bold = true)
    )
}
