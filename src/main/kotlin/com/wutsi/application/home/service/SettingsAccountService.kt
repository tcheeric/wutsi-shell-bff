package com.wutsi.application.home.service

import com.wutsi.application.home.dto.SendSmsCodeRequest
import com.wutsi.application.home.entity.SmsCodeEntity
import com.wutsi.application.home.exception.InvalidPhoneNumberException
import com.wutsi.platform.core.tracing.DeviceIdProvider
import com.wutsi.platform.sms.WutsiSmsApi
import com.wutsi.platform.sms.dto.SendVerificationRequest
import com.wutsi.platform.tenant.dto.MobileCarrier
import com.wutsi.platform.tenant.dto.Tenant
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service
import org.springframework.web.servlet.LocaleResolver
import javax.servlet.http.HttpServletRequest

@Service
class SettingsAccountService(
    private val tenantProvider: TenantProvider,
    private val smsApi: WutsiSmsApi,
    private val cacheManager: CacheManager,
    private val deviceIdProvider: DeviceIdProvider,
    private val httpServletRequest: HttpServletRequest,
    private val localeResolver: LocaleResolver,

    @Value("\${wutsi.platform.cache.name}") private val cacheName: String
) {
    fun sendVerificationCode(request: SendSmsCodeRequest) {
        val tenant = tenantProvider.get()
        if (findCarrier(request.phoneNumber, tenant) == null) {
            throw InvalidPhoneNumberException()
        }

        val response = smsApi.sendVerification(
            SendVerificationRequest(
                phoneNumber = request.phoneNumber,
                language = localeResolver.resolveLocale(httpServletRequest).language
            )
        )
        storeVerificationNumber(request.phoneNumber, response.id)
    }

    fun storeVerificationNumber(phoneNumber: String, verificationId: Long) {
        cacheManager.getCache(cacheName).put(
            cacheKey(),
            SmsCodeEntity(
                phoneNumber = phoneNumber,
                verificationId = verificationId
            )
        )
    }

    private fun findCarrier(phoneNumber: String, tenant: Tenant): MobileCarrier? {
        val carriers = tenantProvider.mobileCarriers(tenant)
        return carriers.find { hasPrefix(phoneNumber, it) }
    }

    private fun hasPrefix(phoneNumber: String, carrier: MobileCarrier): Boolean =
        carrier.phonePrefixes.flatMap { it.prefixes }
            .find { phoneNumber.startsWith(it) } != null

    private fun cacheKey(): String =
        "verification-code-" + deviceIdProvider.get(httpServletRequest)
}
