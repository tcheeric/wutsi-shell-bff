package com.wutsi.application.shell.endpoint.profile.strength

import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.platform.account.dto.Account
import org.springframework.stereotype.Service

@Service
class ProfileStrengthPicture : AbstractProfileStrengthWidget() {
    override fun shouldShow(account: Account): Boolean =
        account.pictureUrl.isNullOrEmpty()

    override fun getIcon(account: Account, size: Double) =
        Icon(code = Theme.ICON_PICTURE, size = size, color = Theme.COLOR_PRIMARY)

    override fun getContent(account: Account): WidgetAware =
        Column(
            mainAxisAlignment = MainAxisAlignment.start,
            crossAxisAlignment = CrossAxisAlignment.start,
            children = listOf(
                Text(
                    caption = getText("profile-strength.picture.title"),
                    bold = true
                ),
                Text(
                    caption = getText(
                        "profile-strength.picture.description-" +
                            if (account.business) "business" else "personal"
                    ),
                    maxLines = 5
                ),
                Button(
                    padding = 10.0,
                    stretched = false,
                    caption = getText("profile-strength.picture.button"),
                    action = Action(
                        type = ActionType.Route,
                        url = urlBuilder.build("/settings/picture")
                    )
                )
            ),
        )
}
