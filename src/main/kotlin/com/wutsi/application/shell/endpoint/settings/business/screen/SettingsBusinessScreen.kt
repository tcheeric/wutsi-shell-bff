package com.wutsi.application.shell.endpoint.settings.business.screen

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.Theme
import com.wutsi.application.shell.service.CategoryService
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.application.shell.service.UserProvider
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.DialogType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/business")
class SettingsBusinessScreen(
    private val urlBuilder: URLBuilder,
    private val userProvider: UserProvider,
    private val categoryService: CategoryService,
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val account = userProvider.get()

        return Screen(
            id = Page.SETTINGS_BUSINESS,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.business.app-bar.title"),
            ),
            child = Container(
                child = ListView(
                    separator = true,
                    separatorColor = Theme.COLOR_DIVIDER,
                    children = listOf(
                        ListItem(
                            caption = getText("page.settings.business.biography"),
                            subCaption = account.biography,
                            trailing = Icon(
                                code = Theme.ICON_EDIT,
                                size = 24.0,
                                color = Theme.COLOR_BLACK
                            ),
                            action = Action(
                                type = ActionType.Route,
                                url = urlBuilder.build("settings/business/biography")
                            )
                        ),
                        ListItem(
                            caption = getText("page.settings.business.website"),
                            subCaption = account.website,
                            trailing = Icon(
                                code = Theme.ICON_EDIT,
                                size = 24.0,
                                color = Theme.COLOR_BLACK
                            ),
                            action = Action(
                                type = ActionType.Route,
                                url = urlBuilder.build("settings/business/website")
                            )
                        ),
                        ListItem(
                            caption = getText("page.settings.business.category"),
                            subCaption = categoryService.getTitle(account),
                            trailing = Icon(
                                code = Theme.ICON_EDIT,
                                size = 24.0,
                                color = Theme.COLOR_BLACK
                            ),
                            action = Action(
                                type = ActionType.Route,
                                url = urlBuilder.build("settings/business/category")
                            )
                        ),
                        Container(
                            padding = 20.0,
                            child = Button(
                                caption = getText("page.settings.business.button.switch-to-personal"),
                                type = ButtonType.Text,
                                action = Action(
                                    type = ActionType.Prompt,
                                    prompt = Dialog(
                                        type = DialogType.Confirm,
                                        message = getText("page.settings.business.switch-to-personal.message"),
                                        actions = listOf(
                                            Button(
                                                caption = getText("page.settings.business.switch-to-personal.yes"),
                                                action = Action(
                                                    type = ActionType.Command,
                                                    url = urlBuilder.build("commands/switch-to-business?value=false")
                                                )
                                            ),
                                            Button(
                                                caption = getText("page.settings.business.switch-to-personal.no"),
                                                type = ButtonType.Text
                                            )
                                        )
                                    ).toWidget()
                                )
                            )
                        )
                    )
                )
            )
        ).toWidget()
    }
}
