package com.wutsi.application.shell.endpoint.profile.strength

import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.platform.account.dto.Account

interface ProfileStrengthWidget {
    fun toWidget(account: Account): WidgetAware?
}
