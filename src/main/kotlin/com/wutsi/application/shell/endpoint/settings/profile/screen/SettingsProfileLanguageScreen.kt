package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shared.service.StringUtil
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.DropdownButton
import com.wutsi.flutter.sdui.DropdownMenuItem
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.dto.Account
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Locale

@RestController
@RequestMapping("/settings/profile/language")
class SettingsProfileLanguageScreen(
    private val tenantProvider: TenantProvider
) : AbstractSettingsProfileAttributeScreen() {
    override fun getAttributeName() = "language"

    override fun getPageId() = Page.SETTINGS_PROFILE_LANGUAGE

    override fun getInputWidget(account: Account): WidgetAware {
        val tenant = tenantProvider.get()
        val user = securityContext.currentAccount()
        val locale = LocaleContextHolder.getLocale()
        return DropdownButton(
            name = "value",
            value = user.language,
            required = true,
            children = tenant.languages.map {
                DropdownMenuItem(
                    caption = StringUtil.capitalizeFirstLetter(Locale(it).getDisplayLanguage(locale)),
                    value = it
                )
            }
        )
    }
}
