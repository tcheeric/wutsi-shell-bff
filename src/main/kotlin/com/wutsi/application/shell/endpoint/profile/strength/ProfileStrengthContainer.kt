package com.wutsi.application.shell.endpoint.profile.strength

import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.dto.Account
import org.springframework.stereotype.Service

@Service
class ProfileStrengthContainer(
    private val picture: ProfileStrengthPicture,
    private val whatsapp: ProfileStrengthWhatsapp,
    private val paymentMethod: ProfileStrengthPaymentMethod,
) : ProfileStrengthWidget {
    override fun toWidget(account: Account): WidgetAware? {
        val all = createComponents()
        val widgets = all.mapNotNull { it.toWidget(account) }
        if (widgets.isEmpty())
            return null

        return Container(
            padding = 10.0,
            margin = 10.0,
            background = Theme.COLOR_PRIMARY_LIGHT,
            borderColor = Theme.COLOR_PRIMARY,
            border = 1.0,
            borderRadius = 5.0,
            child = widgets[0]
        )
    }

    private fun createComponents(): List<ProfileStrengthWidget> =
        listOf(
            paymentMethod,
            whatsapp,
            picture,
        )
}
