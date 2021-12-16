package com.wutsi.application.shell.endpoint.settings.account.screen

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.Theme
import com.wutsi.application.shell.service.TenantProvider
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType.Command
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.Alignment.TopCenter
import com.wutsi.flutter.sdui.enums.InputType.Phone
import com.wutsi.flutter.sdui.enums.InputType.Submit
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/accounts/link/mobile")
class LinkAccountMobileScreen(
    private val urlBuilder: URLBuilder,
    private val tenantProvider: TenantProvider
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val tenant = tenantProvider.get()
        return Screen(
            id = Page.SETTINGS_ACCOUNT_LINK_MOBILE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.WHITE_COLOR,
                foregroundColor = Theme.BLACK_COLOR,
                title = getText("page.link-account-mobile.app-bar.title")
            ),
            child = Container(
                alignment = Center,
                child = Column(
                    children = listOf(
                        Container(
                            alignment = Center,
                            padding = 10.0,
                            child = Text(
                                caption = getText("page.link-account-mobile.title"),
                                alignment = TextAlignment.Center,
                                size = Theme.LARGE_TEXT_SIZE,
                                bold = true
                            )
                        ),
                        Container(
                            alignment = TopCenter,
                            child = Text(
                                caption = getText("page.link-account-mobile.sub-title"),
                                alignment = TextAlignment.Center,
                            )
                        ),
                        Form(
                            children = listOf(
                                Container(
                                    padding = 10.0,
                                    child = Input(
                                        name = "phoneNumber",
                                        type = Phone,
                                        required = true,
                                        countries = tenant.countries
                                    ),
                                ),
                                Container(
                                    padding = 10.0,
                                    child = Row(
                                        children = tenantProvider.mobileCarriers(tenant)
                                            .mapNotNull { tenantProvider.logo(it) }
                                            .map {
                                                Image(
                                                    width = 48.0,
                                                    height = 48.0,
                                                    url = it
                                                )
                                            }
                                    ),
                                ),
                                Container(
                                    padding = 10.0,
                                    child = Input(
                                        name = "command",
                                        type = Submit,
                                        caption = getText("page.link-account-mobile.button.submit"),
                                        action = Action(
                                            type = Command,
                                            url = urlBuilder.build("commands/send-sms-code")
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            ),
        ).toWidget()
    }
}
