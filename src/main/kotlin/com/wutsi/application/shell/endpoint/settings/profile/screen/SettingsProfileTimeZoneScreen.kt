package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.DropdownMenuItem
import com.wutsi.flutter.sdui.SearchableDropdown
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.TimeZone

@RestController
@RequestMapping("/settings/profile/timezone")
class SettingsProfileTimeZoneScreen(
    urlBuilder: URLBuilder,
    securityContext: SecurityContext,
) : AbstractSettingsProfileAttributeScreen(urlBuilder, securityContext) {
    override fun getAttributeName() = "timezone-id"

    override fun getPageId() = Page.SETTINGS_PROFILE_TIMEZONE

    override fun getInputWidget(account: Account): WidgetAware {
        val user = securityContext.currentAccount()

        return SearchableDropdown(
            name = "value",
            value = user.timezoneId,
            children = TimeZone.getAvailableIDs()
                .filter { it.contains("/") }
                .map {
                    DropdownMenuItem(it, it)
                }.sortedBy { it.caption }
        )
    }
}
