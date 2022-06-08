package com.wutsi.application.shell.endpoint.settings.profile.page

import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/business/pages/biography")
class SettingBusinessBiographyPage : AbstractBusinessAttributePage() {
    override fun getAttributeName() = "biography"
    override fun getPageIndex(): Int = 2

    override fun getInputWidget(account: Account): WidgetAware = Input(
        name = "value",
        value = account.biography,
        maxLength = 160
    )
}
