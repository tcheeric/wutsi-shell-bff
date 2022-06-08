package com.wutsi.application.shell.endpoint.settings.profile.page

import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.dto.Account
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/business/pages/display-name")
class BusinessDisplayNamePage : AbstractBusinessAttributePage() {
    override fun getAttributeName() = "display-name"
    override fun getPageIndex(): Int = 1

    override fun getInputWidget(account: Account): WidgetAware = Input(
        name = "value",
        value = account.displayName,
        maxLength = 50,
        required = true
    )

    override fun getDescription(account: Account): String =
        if (account.business)
            getText("page.settings.profile.attribute.${getAttributeName()}.description-business")
        else
            super.getDescription(account)
}
