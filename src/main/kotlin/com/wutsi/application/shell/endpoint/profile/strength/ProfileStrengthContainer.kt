package com.wutsi.application.shell.endpoint.profile.strength

import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.dto.Account
import org.springframework.stereotype.Service

@Service
class ProfileStrengthContainer(
    private val picture: ProfileStrengthPicture,
    private val whatsapp: ProfileStrengthWhatsapp,
    private val paymentMethod: ProfileStrengthPaymentMethod,
    private val email: ProfileStrengthEmailWidget,
    private val location: ProfileStrengthLocationWidget,
    private val shipping: ProfileStrengthShipping
) : ProfileStrengthWidget {
    override fun toWidget(account: Account): WidgetAware? {
        val all = createComponents()
        val widgets = all.mapNotNull { it.toWidget(account) }

        return if (widgets.isEmpty())
            null
        else
            Column(
                children = listOf(
                    widgets.random().let {
                        Container(
                            padding = 10.0,
                            margin = 10.0,
                            background = Theme.COLOR_PRIMARY_LIGHT,
                            borderColor = Theme.COLOR_PRIMARY,
                            border = 1.0,
                            borderRadius = 5.0,
                            child = it
                        )
                    }
                )
            )
    }

    private fun createComponents(): List<ProfileStrengthWidget> =
        listOf(
            paymentMethod,
            whatsapp,
            location,
            shipping,
            email,
            picture,
        )
}
