package com.wutsi.application.shell.endpoint.profile.strength

import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.dto.Account
import org.springframework.stereotype.Service

@Service
class ProfileStrengthPicture : AbstractProfileStrengthWidget() {
    override fun shouldShow(account: Account): Boolean =
        account.pictureUrl.isNullOrEmpty()

    override fun getIcon(account: Account, size: Double) =
        Icon(code = Theme.ICON_PICTURE, size = size, color = Theme.COLOR_PRIMARY)

    override fun getTitle() = getText("profile-strength.picture.title")
    override fun getDescription() = getText("profile-strength.picture.description")
    override fun getActionTitle() = getText("profile-strength.picture.button")
    override fun getAction() = Action(
        type = ActionType.Route,
        url = urlBuilder.build("/settings/picture")
    )
}
