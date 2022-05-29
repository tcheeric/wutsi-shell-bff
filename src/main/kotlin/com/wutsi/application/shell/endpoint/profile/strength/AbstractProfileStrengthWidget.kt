package com.wutsi.application.shell.endpoint.profile.strength

import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.platform.account.dto.Account
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder

abstract class AbstractProfileStrengthWidget : ProfileStrengthWidget {
    @Autowired
    protected lateinit var messages: MessageSource

    @Autowired
    protected lateinit var urlBuilder: URLBuilder

    protected abstract fun shouldShow(account: Account): Boolean
    protected abstract fun getIcon(account: Account, size: Double): Icon?
    protected abstract fun getTitle(): String
    protected abstract fun getDescription(): String
    protected abstract fun getActionTitle(): String
    protected abstract fun getAction(): Action

    protected open fun getContent(): WidgetAware =
        Column(
            mainAxisAlignment = MainAxisAlignment.start,
            crossAxisAlignment = CrossAxisAlignment.start,
            children = listOf(
                Text(
                    caption = getTitle(),
                    bold = true,
                    size = Theme.TEXT_SIZE_LARGE
                ),
                Container(padding = 5.0),
                Text(
                    caption = getDescription(),
                    maxLines = 5
                ),
                Container(padding = 10.0),
                Button(
                    padding = 10.0,
                    stretched = false,
                    caption = getActionTitle(),
                    action = getAction()
                )
            ),
        )

    override fun toWidget(account: Account): WidgetAware? =
        if (shouldShow(account))
            Row(
                children = listOfNotNull(
                    Container(
                        background = Theme.COLOR_WHITE,
                        borderColor = Theme.COLOR_PRIMARY,
                        border = 1.0,
                        width = 40.0,
                        height = 40.0,
                        borderRadius = 20.0,
                        child = getIcon(account, 32.0),
                    ),
                    Container(padding = 5.0),
                    Flexible(
                        child = getContent()
                    )
                ),
                mainAxisAlignment = MainAxisAlignment.start,
                crossAxisAlignment = CrossAxisAlignment.start,
            )
        else
            null

    protected fun getText(key: String, args: Array<Any?> = emptyArray()) =
        messages.getMessage(key, args, LocaleContextHolder.getLocale()) ?: key
}
