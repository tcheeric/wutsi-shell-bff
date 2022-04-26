package com.wutsi.application.shell.endpoint.profile.strength

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import org.springframework.stereotype.Service

@Service
class ProfileStrengthPaymentMethod(
    private val accountApi: WutsiAccountApi,
    private val togglesProvider: TogglesProvider,
) : AbstractProfileStrengthWidget() {
    override fun shouldShow(account: Account): Boolean =
        try {
            togglesProvider.isAccountEnabled() && accountApi.listPaymentMethods(account.id).paymentMethods.isEmpty()
        } catch (ex: Exception) {
            false
        }

    override fun getIcon(account: Account, size: Double) =
        Icon(code = Theme.ICON_PAYMENT, size = size, color = Theme.COLOR_PRIMARY)

    override fun getContent(account: Account): WidgetAware =
        Column(
            mainAxisAlignment = MainAxisAlignment.start,
            crossAxisAlignment = CrossAxisAlignment.start,
            children = listOf(
                Text(
                    caption = getText("profile-strength.payment-method.title"),
                    bold = true
                ),
                Text(
                    caption = getText(key = "profile-strength.payment-method.description"),
                    maxLines = 5
                ),
                Button(
                    padding = 10.0,
                    stretched = false,
                    caption = getText("profile-strength.payment-method.button"),
                    action = Action(
                        type = ActionType.Route,
                        url = urlBuilder.build("/settings/accounts/link/mobile")
                    )
                )
            ),
        )
}
