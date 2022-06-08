package com.wutsi.application.shell.endpoint.settings.profile.page

import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("settings/business/pages/street")
class SettingsBusinessStreetPage : AbstractBusinessAttributePage() {
    override fun getAttributeName() = "street"
    override fun getPageIndex(): Int = 5

    override fun getInputWidget(account: Account): WidgetAware = Input(
        name = "value",
        value = account.street,
        maxLength = 160
    )
}
