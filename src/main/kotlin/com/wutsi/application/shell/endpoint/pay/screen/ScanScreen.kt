package com.wutsi.application.shell.endpoint.pay.screen

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Theme
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.QrView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pay/scan")
class ScanScreen(
    private val urlBuilder: URLBuilder
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        return Screen(
            id = "page.pay.scan",
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.WHITE_COLOR,
                foregroundColor = Theme.BLACK_COLOR,
                title = "Scan"
            ),
            child = Column(
                children = listOf(
                    Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = Text("Scan the QR Code")
                    ),
                    Flexible(
                        child = QrView(
                            action = Action(
                                type = ActionType.Command,
                                url = urlBuilder.build("commands/scan")
                            )
                        )
                    ),
                ),
            )
        ).toWidget()
    }
}
