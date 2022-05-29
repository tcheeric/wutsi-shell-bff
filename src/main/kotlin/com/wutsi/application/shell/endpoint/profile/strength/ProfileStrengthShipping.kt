package com.wutsi.application.shell.endpoint.profile.strength

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.ecommerce.shipping.WutsiShippingApi
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.dto.Account
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ProfileStrengthShipping(
    private val shippingApi: WutsiShippingApi,
    private val togglesProvider: TogglesProvider,
    @Value("\${wutsi.application.store-url}") private val storeUrl: String,
) : AbstractProfileStrengthWidget() {
    override fun shouldShow(account: Account): Boolean =
        try {
            togglesProvider.isShippingEnabled() && account.business && account.hasStore && shippingApi.listShipping().shippings.isEmpty()
        } catch (ex: Exception) {
            false
        }

    override fun getIcon(account: Account, size: Double) =
        Icon(code = Theme.ICON_SHIPPING, size = size, color = Theme.COLOR_PRIMARY)

    override fun getTitle() = getText("profile-strength.shipping.title")
    override fun getDescription() = getText("profile-strength.shipping.description")
    override fun getActionTitle() = getText("profile-strength.shipping.button")
    override fun getAction() = Action(
        type = ActionType.Route,
        url = urlBuilder.build(storeUrl, "/settings/store/shipping")
    )
}
