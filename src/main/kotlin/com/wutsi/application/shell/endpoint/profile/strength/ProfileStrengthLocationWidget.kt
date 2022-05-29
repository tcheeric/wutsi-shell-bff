package com.wutsi.application.shell.endpoint.profile.strength

import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.dto.Account
import org.springframework.stereotype.Service

@Service
class ProfileStrengthLocationWidget : AbstractProfileStrengthWidget() {
    override fun shouldShow(account: Account): Boolean =
        account.cityId == null

    override fun getIcon(account: Account, size: Double) =
        Icon(code = Theme.ICON_PLACE, size = size, color = Theme.COLOR_PRIMARY)

    override fun getTitle() = getText("profile-strength.location.title")
    override fun getDescription() = getText("profile-strength.location.description")
    override fun getActionTitle() = getText("profile-strength.location.button")
    override fun getAction() = Action(
        type = ActionType.Route,
        url = urlBuilder.build("/settings/profile/city")
    )
}
