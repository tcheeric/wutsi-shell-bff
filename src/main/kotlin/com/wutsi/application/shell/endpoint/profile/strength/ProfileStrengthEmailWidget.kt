package com.wutsi.application.shell.endpoint.profile.strength

import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.dto.Account
import org.springframework.stereotype.Service

@Service
class ProfileStrengthEmailWidget : AbstractProfileStrengthWidget() {
    override fun shouldShow(account: Account): Boolean =
        account.email.isNullOrEmpty()

    override fun getIcon(account: Account, size: Double) =
        Icon(code = Theme.ICON_EMAIL, size = size, color = Theme.COLOR_PRIMARY)

    override fun getTitle() = getText("profile-strength.email.title")
    override fun getDescription() = getText("profile-strength.email.description")
    override fun getActionTitle() = getText("profile-strength.email.button")
    override fun getAction() = Action(
        type = ActionType.Route,
        url = urlBuilder.build("/settings/profile/email")
    )
}
