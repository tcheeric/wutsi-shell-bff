package com.wutsi.application.shell.service

import com.wutsi.platform.account.dto.Account
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

@ConfigurationProperties(prefix = "wutsi.toggles")
class Toggles {
    var sendSmsCode: Boolean = true
    var verifySmsCode: Boolean = true
    var payment: Boolean = true
    var scan: Boolean = true
    var account: Boolean = true
    var business: Boolean = true
    var testerUserIds: List<Long> = emptyList()
}

@Service
@EnableConfigurationProperties(Toggles::class)
class TogglesProvider(
    private val toggles: Toggles
) {
    fun isBusinessAccountEnabled(): Boolean =
        toggles.business

    fun isPaymentEnabled(account: Account): Boolean =
        account.business && (toggles.payment || isTester(account))

    fun isScanEnabled(account: Account): Boolean =
        toggles.scan || isTester(account)

    fun isSendSmsEnabled(): Boolean =
        toggles.sendSmsCode

    fun isVerifySmsCodeEnabled(): Boolean =
        toggles.verifySmsCode

    fun isAccountEnabled(): Boolean =
        toggles.account

    private fun isTester(account: Account): Boolean =
        toggles.testerUserIds.contains(account.id)
}
