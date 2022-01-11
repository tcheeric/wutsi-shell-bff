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
    var logout: Boolean = true
    var testerUserIds: List<Long> = emptyList()
    var testPhoneNumbers: List<String> = emptyList()
}

@Service
@EnableConfigurationProperties(Toggles::class)
class TogglesProvider(
    private val toggles: Toggles,
    private val userProvider: UserProvider,
) {
    fun isBusinessAccountEnabled(): Boolean =
        toggles.business

    fun isPaymentEnabled(account: Account): Boolean =
        account.business && (toggles.payment || isTester(account.id))

    fun isScanEnabled(account: Account): Boolean =
        toggles.scan || isTester(account.id)

    fun isSendSmsEnabled(phoneNumber: String): Boolean =
        toggles.sendSmsCode || isTestTestPhoneNumber(phoneNumber)

    fun isVerifySmsCodeEnabled(phoneNumber: String): Boolean =
        toggles.verifySmsCode || isTestTestPhoneNumber(phoneNumber)

    fun isAccountEnabled(): Boolean =
        toggles.account

    fun isLogoutEnabled(): Boolean =
        toggles.logout || isTester()

    private fun isTester(): Boolean =
        isTester(userProvider.id())

    private fun isTester(userId: Long): Boolean =
        toggles.testerUserIds.contains(userId)

    private fun isTestTestPhoneNumber(phoneNumber: String): Boolean =
        toggles.testPhoneNumbers.contains(phoneNumber)
}
