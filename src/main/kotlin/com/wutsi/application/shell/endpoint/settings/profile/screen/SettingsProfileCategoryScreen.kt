package com.wutsi.application.shell.endpoint.settings.profile.screen

import com.wutsi.application.shared.service.CategoryService
import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.SharedUIMapper
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.flutter.sdui.DropdownMenuItem
import com.wutsi.flutter.sdui.SearchableDropdown
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/profile/category")
class SettingsProfileCategoryScreen(
    urlBuilder: URLBuilder,
    securityContext: SecurityContext,
    private val categoryService: CategoryService,
    private val sharedUIMapper: SharedUIMapper,
) : AbstractSettingsProfileAttributeScreen(urlBuilder, securityContext) {
    override fun getAttributeName() = "category-id"

    override fun getPageId() = Page.SETTINGS_PROFILE_CATEGORY

    override fun getInputWidget(account: Account): WidgetAware {
        val user = securityContext.currentAccount()
        return SearchableDropdown(
            name = "value",
            value = user.categoryId?.toString(),
            children = categoryService.all()
                .sortedBy { sharedUIMapper.toCategoryText(it) }
                .map {
                    DropdownMenuItem(
                        caption = sharedUIMapper.toCategoryText(it) ?: "",
                        value = it.id.toString()
                    )
                }
        )
    }
}
