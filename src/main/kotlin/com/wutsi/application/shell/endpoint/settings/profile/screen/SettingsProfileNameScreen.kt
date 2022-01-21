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
@RequestMapping("/settings/profile/name")
class SettingsProfileNameScreen(
    urlBuilder: URLBuilder,
    securityContext: SecurityContext
) : AbstractSettingsProfileAttributeScreen(urlBuilder, securityContext) {
    override fun getAttributeName() = "name"

    override fun getPageId() = Page.SETTINGS_PROFILE_NAME

    override fun getInputWidget(account: Account): WidgetAware = Input(
        name = "name",
        value = account.street,
        maxLength = 100,
        required = true
    )
}
