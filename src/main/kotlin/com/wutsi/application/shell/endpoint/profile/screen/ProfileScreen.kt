package com.wutsi.application.shell.endpoint.profile.screen

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.Theme
import com.wutsi.application.shell.service.CategoryService
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.application.shell.service.UserProvider
import com.wutsi.application.shell.util.StringUtil
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Center
import com.wutsi.flutter.sdui.CircleAvatar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.contact.WutsiContactApi
import com.wutsi.platform.contact.dto.SearchContactRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/profile")
class ProfileScreen(
    private val urlBuilder: URLBuilder,
    private val accountApi: WutsiAccountApi,
    private val contactApi: WutsiContactApi,
    private val categoryService: CategoryService,
    private val userProvider: UserProvider,

    @Value("\${wutsi.application.cash-url}") private val cashUrl: String,
) : AbstractQuery() {

    @PostMapping
    fun index(@RequestParam id: Long): Widget {
        val user = accountApi.getAccount(id).account
        val isContact = if (id == userProvider.id())
            false
        else
            contactApi.searchContact(
                request = SearchContactRequest(
                    contactIds = listOf(id)
                )
            ).contacts.isNotEmpty()

        return Screen(
            id = Page.PROFILE,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_PRIMARY,
                foregroundColor = Theme.COLOR_WHITE,
                title = getText("page.profile.app-bar.title"),
            ),
            child = if (user.business) business(user, isContact) else personal(user, isContact)
        ).toWidget()
    }

    private fun personal(user: Account, isContact: Boolean): WidgetAware {
        val children = mutableListOf<WidgetAware>(
            Container(
                padding = 10.0,
                alignment = Alignment.Center,
                background = Theme.COLOR_PRIMARY,
                child = Center(child = picture(user, 64.0)),
            ),
            Container(
                padding = 10.0,
                alignment = Alignment.Center,
                background = Theme.COLOR_PRIMARY,
                child = Center(
                    child = Text(
                        user.displayName ?: "",
                        bold = true,
                        color = Theme.COLOR_WHITE,
                        size = Theme.TEXT_SIZE_LARGE
                    )
                )
            ),
        )

        val buttons = personalButtons(user, isContact)
        if (buttons.isNotEmpty()) {
            children.add(Container(padding = 20.0))
            children.addAll(buttons)
        }

        return Column(
            children = children,
            crossAxisAlignment = CrossAxisAlignment.center
        )
    }

    private fun personalButtons(user: Account, isContact: Boolean): List<WidgetAware> {
        val buttons = mutableListOf<WidgetAware>()
        if (!isContact)
            buttons.add(
                button(
                    getText("page.profile.button.add-contact"),
                    Action(
                        type = ActionType.Command,
                        url = urlBuilder.build("commands/add-contact?contact-id=${user.id}")
                    )
                )
            )

        buttons.add(
            button(
                getText("page.profile.button.send"),
                Action(
                    type = ActionType.Route,
                    url = urlBuilder.build(cashUrl, "send")
                )
            )
        )
        return buttons
    }

    private fun business(user: Account, isContact: Boolean): WidgetAware {
        val children = mutableListOf<WidgetAware>(
            Container(
                background = Theme.COLOR_PRIMARY,
                child = Row(
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.start,
                    children = listOf(
                        Flexible(
                            flex = 2,
                            child = Container(
                                padding = 10.0,
                                alignment = Alignment.TopCenter,
                                background = Theme.COLOR_PRIMARY,
                                child = picture(
                                    user, 48.0
                                ),
                            )
                        ),
                        Flexible(
                            flex = 10,
                            child = Container(
                                padding = 10.0,
                                background = Theme.COLOR_PRIMARY,
                                child = Column(
                                    children = businessProfile(user),
                                    mainAxisAlignment = MainAxisAlignment.start,
                                    crossAxisAlignment = CrossAxisAlignment.start
                                ),
                            )
                        )
                    ),
                )
            )
        )

        val buttons = businessButtons(user, isContact)
        if (buttons.isNotEmpty()) {
            children.add(Container(padding = 20.0))
            children.addAll(buttons)
        }

        return Column(
            children = children
        )
    }

    private fun businessProfile(user: Account): List<WidgetAware> {
        val category = user.categoryId?.let { categoryService.get(it) }
        val profile = mutableListOf<WidgetAware>(
            Container(
                padding = 5.0,
                child = Text(
                    user.displayName ?: "",
                    bold = true,
                    color = Theme.COLOR_WHITE,
                    size = Theme.TEXT_SIZE_LARGE
                ),
            )
        )

        if (category != null)
            profile.add(
                Container(
                    padding = 5.0,
                    child = Text(
                        if (user.language == "fr") category.titleFrench else category.title,
                        color = Theme.COLOR_WHITE,
                        alignment = TextAlignment.Left,
                        italic = true
                    )
                )
            )

        if (!user.biography.isNullOrEmpty())
            profile.add(
                Container(
                    padding = 5.0,
                    alignment = Alignment.TopLeft,
                    child = Flexible(
                        child = Text(
                            user.biography ?: "",
                            alignment = TextAlignment.Left,
                            color = Theme.COLOR_WHITE,
                        )
                    ),
                )
            )

        return profile
    }

    private fun businessButtons(user: Account, isContact: Boolean): List<WidgetAware> {
        val buttons = mutableListOf<WidgetAware>()
        if (!user.website.isNullOrEmpty()) {
            val i = user.website?.indexOf("//")
            val website = i?.let { user.website?.substring(it + 2) } ?: user.website
            website?.let {
                buttons.add(
                    button(
                        it,
                        Action(
                            type = ActionType.Navigate,
                            url = user.website ?: ""
                        )
                    )
                )
            }
        }

        if (!isContact)
            buttons.add(
                button(
                    getText("page.profile.button.add-contact"),
                    Action(
                        type = ActionType.Command,
                        url = urlBuilder.build("commands/add-contact?contact-id=${user.id}")
                    )
                )
            )

        return buttons
    }

    private fun button(caption: String, action: Action, type: ButtonType = ButtonType.Outlined) = Container(
        padding = 10.0,
        child = Button(
            caption = caption,
            action = action,
            type = type,
            padding = 10.0
        )
    )

    private fun picture(user: Account, size: Double): WidgetAware =
        if (!user.pictureUrl.isNullOrBlank())
            CircleAvatar(
                radius = size / 2,
                child = Image(
                    width = size,
                    height = size,
                    url = user.pictureUrl!!
                )
            )
        else
            CircleAvatar(
                radius = size / 2,
                child = Text(
                    caption = StringUtil.initials(user.displayName),
                    size = (size / 2 - 2),
                    bold = true
                )
            )
}
