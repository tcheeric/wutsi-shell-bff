package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/profile/street")
class SettingsProfileStreetScreen(
    urlBuilder: URLBuilder,
    securityContext: SecurityContext
) : AbstractSettingsProfileAttributeScreen(urlBuilder, securityContext) {
    override fun getAttributeName() = "street"

    override fun getPageId() = Page.SETTINGS_PROFILE_STREET

    override fun getInputWidget(account: Account): WidgetAware = Input(
        name = "value",
        value = account.street,
        maxLength = 160
    )
}
