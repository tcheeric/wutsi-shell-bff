package com.wutsi.application.shell.endpoint

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.BottomNavigationBar
import com.wutsi.flutter.sdui.BottomNavigationBarItem
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.ActionType.Prompt
import com.wutsi.flutter.sdui.enums.DialogType.Error
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import java.net.URLEncoder

abstract class AbstractEndpoint {
    @Autowired
    protected lateinit var messages: MessageSource

    @Autowired
    protected lateinit var logger: KVLogger

    @Autowired
    private lateinit var phoneNumberUtil: PhoneNumberUtil

    @Autowired
    protected lateinit var urlBuilder: URLBuilder

    @Autowired
    protected lateinit var securityContext: SecurityContext

    @Value("\${wutsi.application.cash-url}")
    protected lateinit var cashUrl: String

    protected fun bottomNavigationBar() = BottomNavigationBar(
        background = Theme.COLOR_PRIMARY,
        selectedItemColor = Theme.COLOR_WHITE,
        unselectedItemColor = Theme.COLOR_WHITE,
        items = listOf(
            BottomNavigationBarItem(
                icon = Theme.ICON_HOME,
                caption = getText("page.home.bottom-nav-bar.home"),
                action = Action(
                    type = ActionType.Route,
                    url = "route:/~"
                )
            ),
            BottomNavigationBarItem(
                icon = Theme.ICON_CONTACT,
                caption = getText("page.home.bottom-nav-bar.me"),
                action = Action(
                    type = ActionType.Route,
                    url = urlBuilder.build("profile?id=${securityContext.currentAccountId()}"),
                )
            ),
//            BottomNavigationBarItem(
//                icon = Theme.ICON_HISTORY,
//                caption = getText("page.home.bottom-nav-bar.transactions"),
//                action = Action(
//                    type = ActionType.Route,
//                    url = urlBuilder.build(cashUrl, "history")
//                )
//            ),
            BottomNavigationBarItem(
                icon = Theme.ICON_SETTINGS,
                caption = getText("page.home.bottom-nav-bar.settings"),
                action = Action(
                    type = ActionType.Route,
                    url = urlBuilder.build("settings")
                )
            )
        )
    )

    protected fun createErrorAction(e: Throwable?, messageKey: String): Action {
        val action = Action(
            type = Prompt,
            prompt = Dialog(
                title = getText("prompt.error.title"),
                type = Error,
                message = getText(messageKey)
            ).toWidget()
        )
        log(action, e)
        return action
    }

    private fun log(action: Action, e: Throwable?) {
        logger.add("action_type", action.type)
        logger.add("action_url", action.url)
        logger.add("action_prompt_type", action.prompt?.type)
        logger.add("action_prompt_message", action.prompt?.attributes?.get("message"))
        if (e != null)
            logger.setException(e)

        LoggerFactory.getLogger(this::class.java).error("Unexpected error", e)
    }

    protected fun getText(key: String, args: Array<Any?> = emptyArray()) =
        messages.getMessage(key, args, LocaleContextHolder.getLocale()) ?: key

    protected fun formattedPhoneNumber(number: String?, country: String? = null): String? {
        if (number == null)
            return null

        val phoneNumber = phoneNumberUtil.parse(number, country ?: detectCountry(number))
        return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
    }

    private fun detectCountry(number: String): String {
        val phone = phoneNumberUtil.parse(number, "")
        return phoneNumberUtil.getRegionCodeForCountryCode(phone.countryCode)
    }

    protected fun encodeURLParam(text: String?): String =
        text?.let { URLEncoder.encode(it, "utf-8") } ?: ""
}
