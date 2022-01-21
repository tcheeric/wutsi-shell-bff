package com.wutsi.application.shell.endpoint.qr.screen

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.SharedUIMapper
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shared.ui.Avatar
import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Center
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.QrImage
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.platform.qr.WutsiQrApi
import com.wutsi.platform.qr.dto.EncodeQRCodeRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/qr-code")
class QrCodeScreen(
    private val qrApi: WutsiQrApi,
    private val tenantProvider: TenantProvider,
    private val securityContext: SecurityContext,
    private val sharedUIMapper: SharedUIMapper,
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val code = qrApi.encode(
            EncodeQRCodeRequest(
                type = "account",
                id = securityContext.currentAccountId().toString(),
                timeToLive = 300
            )
        ).token
        val tenant = tenantProvider.get()
        val user = securityContext.currentAccount()

        return Screen(
            id = Page.QR_CODE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.qr-code.app-bar.title")
            ),
            child = Column(
                children = listOf(
                    Center(
                        child = Container(
                            padding = 10.0,
                            alignment = Alignment.Center,
                            child = Avatar(
                                radius = 32.0,
                                model = sharedUIMapper.toAccountModel(user)
                            )
                        ),
                    ),
                    Center(
                        child = Container(
                            padding = 10.0,
                            alignment = Alignment.Center,
                            child = QrImage(
                                data = code,
                                size = 230.0,
                                padding = 10.0,
                                embeddedImageSize = 64.0,
                                embeddedImageUrl = tenant.logos.find { it.type == "PICTORIAL" }?.url
                            ),
                        ),
                    ),
                    Container(
                        padding = 20.0,
                        margin = 20.0,
                        alignment = Alignment.Center,
                        background = Theme.COLOR_PRIMARY_LIGHT,
                        border = 1.0,
                        borderRadius = 5.0,
                        child = Text(
                            getText("page.qr-code.warning"),
                            alignment = TextAlignment.Center
                        )
                    )
                ),
            )
        ).toWidget()
    }
}
