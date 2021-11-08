package com.wutsi.application.home.endpoint.home

import com.wutsi.application.home.endpoint.AbstractQuery
import com.wutsi.application.home.endpoint.Theme
import com.wutsi.application.home.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.tenant.WutsiTenantApi
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class HomeScreen(
    private val urlBuilder: URLBuilder,
    private val accountApi: WutsiAccountApi,
    private val tenant: WutsiTenantApi
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        return Screen(
            appBar = AppBar(
                foregroundColor = Theme.HOME_TEXT_COLOR,
                backgroundColor = Theme.HOME_BACKGROUND_COLOR,
                actions = listOf(
                    IconButton(
                        icon = Theme.ICON_SETTINGS,
                        action = Action(
                            type = Route,
                            url = urlBuilder.build("/settings")
                        )
                    )
                )
            ),
            child = Column(
                children = listOf(
                    Container(
                        alignment = Center,
                        child = Text(caption = getText("page.home.your-balance"))
                    ),
                    Container(
                        alignment = Center,
                        child = MoneyText(value = 0.0, currency = "XAF")
                    ),
                )
            ),
        ).toWidget()
    }
}
