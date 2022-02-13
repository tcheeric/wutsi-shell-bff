package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.SharedUIMapper
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.DropdownMenuItem
import com.wutsi.flutter.sdui.SearchableDropdown
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/profile/category")
class SettingsProfileCategoryScreen(
    urlBuilder: URLBuilder,
    securityContext: SecurityContext,
    private val sharedUIMapper: SharedUIMapper,
    private val accountApi: WutsiAccountApi,
) : AbstractSettingsProfileAttributeScreen(urlBuilder, securityContext) {
    override fun getAttributeName() = "category-id"

    override fun getPageId() = Page.SETTINGS_PROFILE_CATEGORY

    override fun getInputWidget(account: Account): WidgetAware {
        val user = securityContext.currentAccount()
        return SearchableDropdown(
            name = "value",
            value = user.category?.id?.toString(),
            children = accountApi.listCategories().categories
                .sortedBy { sharedUIMapper.toTitle(it) }
                .map {
                    DropdownMenuItem(
                        caption = sharedUIMapper.toTitle(it),
                        value = it.id.toString()
                    )
                }
        )
    }
}
