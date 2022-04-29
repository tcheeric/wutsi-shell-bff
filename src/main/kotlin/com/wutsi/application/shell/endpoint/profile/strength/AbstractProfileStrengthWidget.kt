package com.wutsi.application.shell.endpoint.profile.strength

import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Row
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
    protected abstract fun getContent(account: Account): WidgetAware

    override fun toWidget(account: Account): WidgetAware? =
        if (shouldShow(account))
            Row(
                children = listOfNotNull(
                    getIcon(account, 24.0)?.let {
                        Container(
                            padding = 5.0,
                            child = it
                        )
                    },
                    Flexible(
                        child = getContent(account)
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
