package com.wutsi.application.shell.endpoint.profile.strength

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.dto.Account
import org.springframework.stereotype.Service

@Service
class ProfileStrengthWhatsapp(
    private val phoneNumberUtil: PhoneNumberUtil
) : AbstractProfileStrengthWidget() {
    override fun shouldShow(account: Account): Boolean =
        account.business && account.whatsapp.isNullOrEmpty()

    override fun getIcon(account: Account, size: Double) =
        Icon(code = Theme.ICON_WHATSAPP, size = size, color = Theme.COLOR_WHATSAPP)

    override fun getTitle() = getText("profile-strength.whatsapp.title")
    override fun getDescription() = getText("profile-strength.whatsapp.description")
    override fun getActionTitle() = getText("profile-strength.whatsapp.button")
    override fun getAction() = Action(
        type = ActionType.Route,
        url = urlBuilder.build("/settings/profile/whatsapp")
    )
}
