package com.wutsi.application.shell.endpoint.pay.screen

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Theme
import com.wutsi.application.shell.endpoint.pay.dto.ScanRequest
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.Alignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pay/process")
class ProcessScreen(
    private val urlBuilder: URLBuilder
) : AbstractQuery() {
    @PostMapping
    fun index(@RequestBody request: ScanRequest): Widget {
        println(">>>> ${request.format}")
        println(">>>> ${request.code}")
        return Screen(
            id = "page.pay.process",
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.WHITE_COLOR,
                foregroundColor = Theme.BLACK_COLOR,
                title = "Pay"
            ),
            child = Column(
                children = listOf(
                    Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = Icon(code = Theme.ICON_CHECK, color = Theme.SUCCESS_COLOR, size = 64.0)
                    ),
                ),
            )
        ).toWidget()
    }
}
