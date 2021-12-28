package com.wutsi.application.shell.endpoint.qr.screen

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.Theme
import com.wutsi.application.shell.service.TenantProvider
import com.wutsi.application.shell.service.UserProvider
import com.wutsi.application.shell.util.StringUtil
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Center
import com.wutsi.flutter.sdui.CircleAvatar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.QrImage
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.platform.qr.WutsiQrApi
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/qr-code")
class QrCodeScreen(
    private val qrApi: WutsiQrApi,
    private val tenantProvider: TenantProvider,
    private val userProvider: UserProvider
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val code = qrApi.account().token
        val tenant = tenantProvider.get()
        val user = userProvider.get()

        return Screen(
            id = Page.QR_CODE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.WHITE_COLOR,
                foregroundColor = Theme.BLACK_COLOR,
                title = getText("page.qr-code.app-bar.title")
            ),
            child = Column(
                children = listOf(
                    Center(
                        child = Container(
                            padding = 10.0,
                            alignment = Alignment.Center,
                            child = if (!user.pictureUrl.isNullOrBlank())
                                CircleAvatar(
                                    radius = 32.0,
                                    child = Image(
                                        width = 64.0,
                                        height = 64.0,
                                        url = user.pictureUrl!!
                                    )
                                )
                            else
                                CircleAvatar(
                                    radius = 32.0,
                                    child = Text(
                                        caption = StringUtil.initials(user.displayName),
                                        size = 30.0,
                                        bold = true
                                    )
                                )
                        ),
                    ),
                    Center(
                        child = Container(
                            padding = 10.0,
                            alignment = Alignment.Center,
                            child = QrImage(
                                data = code,
                                size = 300.0,
                                padding = 10.0,
                                embeddedImageSize = 80.0,
                                embeddedImageUrl = tenant.logos.find { it.type == "PICTORIAL" }?.url
                            ),
                        ),
                    )
                ),
            )
        ).toWidget()
    }
}
