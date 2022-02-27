package com.wutsi.application.shell.endpoint.profile.widget

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.SharedUIMapper
import com.wutsi.application.shared.ui.ProfileListItem
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.flutter.sdui.Center
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.QrImage
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.qr.WutsiQrApi
import com.wutsi.platform.qr.dto.EncodeQRCodeRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/profile/qr-code-widget")
class QrCodeWidget(
    private val qrApi: WutsiQrApi,
    private val accountApi: WutsiAccountApi,
    private val sharedUIMapper: SharedUIMapper,
) : AbstractQuery() {
    @PostMapping
    fun index(
        @RequestParam id: Long
    ): Widget {
        val code = qrApi.encode(
            EncodeQRCodeRequest(
                type = "account",
                id = id.toString(),
                timeToLive = 300
            )
        ).token
        val user = accountApi.getAccount(id).account

        return Column(
            children = listOf(
                ProfileListItem(
                    model = sharedUIMapper.toAccountModel(user)
                ),
                Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
                Center(
                    child = Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = QrImage(
                            data = code,
                            size = 230.0,
                            padding = 10.0,
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }
}
