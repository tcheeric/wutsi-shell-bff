package com.wutsi.application.home.service

import com.wutsi.application.home.dto.SendSmsCodeRequest
import com.wutsi.application.home.dto.VerifySmsCodeRequest
import com.wutsi.application.home.entity.SmsCodeEntity
import com.wutsi.application.home.exception.InvalidPhoneNumberException
import com.wutsi.application.home.exception.SmsCodeMismatchException
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.AddPaymentMethodRequest
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.DeviceIdProvider
import com.wutsi.platform.payment.PaymentMethodProvider
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
class AccountService(
    private val tenantProvider: TenantProvider,
    private val smsApi: WutsiSmsApi,
    private val accountApi: WutsiAccountApi,
    private val cacheManager: CacheManager,
    private val deviceIdProvider: DeviceIdProvider,
    private val httpServletRequest: HttpServletRequest,
    private val localeResolver: LocaleResolver,
    private val togglesProvider: TogglesProvider,
    private val userProvider: UserProvider,
    private val logger: KVLogger,

    @Value("\${wutsi.platform.cache.name}") private val cacheName: String,
) {
    fun sendVerificationCode(request: SendSmsCodeRequest) {
        val tenant = tenantProvider.get()
        val carrier = findCarrier(request.phoneNumber, tenant)
            ?: throw InvalidPhoneNumberException()

        val verificationId = sendVerificationCode(request.phoneNumber)
        storeVerificationNumber(request.phoneNumber, verificationId, carrier.code)

        logger.add("phone_number", request.phoneNumber)
        logger.add("verification_id", verificationId)
    }

    fun resentVerificationCode() {
        val state = getSmsCodeEntity()
        val verificationId = sendVerificationCode(state.phoneNumber)
        storeVerificationNumber(state.phoneNumber, verificationId, state.carrier)

        logger.add("phone_number", state.phoneNumber)
        logger.add("verification_id", verificationId)
    }

    fun verifyCode(request: VerifySmsCodeRequest) {
        val state = getSmsCodeEntity()

        // Verify
        logger.add("phone_number", state.phoneNumber)
        logger.add("code", request.code)
        if (togglesProvider.get().verifySmsCode) {
            try {
                smsApi.validateVerification(
                    id = state.verificationId,
                    code = request.code
                )
            } catch (ex: Exception) {
                throw SmsCodeMismatchException(ex)
            }
        }

        // Create account
        val principal = userProvider.principal()
        val paymentProvider = toPaymentProvider(state.carrier)
        val response = accountApi.addPaymentMethod(
            principal.id.toLong(),
            request = AddPaymentMethodRequest(
                ownerName = principal.name,
                phoneNumber = state.phoneNumber,
                type = paymentProvider!!.paymentType.name,
                provider = paymentProvider.name
            )
        )
        logger.add("payment_provider", paymentProvider)
        logger.add("payment_method_token", response.token)
    }

    fun getPaymentMethods(tenant: Tenant): List<PaymentMethodSummary> {
        val userId = userProvider.id()
        return accountApi.listPaymentMethods(userId).paymentMethods
            .filter { findMobileCarrier(tenant, it) != null }
    }

    fun getLogoUrl(tenant: Tenant, paymentMethod: PaymentMethodSummary): String? {
        val carrier = findMobileCarrier(tenant, paymentMethod)
        if (carrier != null) {
            return tenantProvider.logo(carrier)
        }
        return null
    }

    private fun findMobileCarrier(tenant: Tenant, paymentMethod: PaymentMethodSummary): MobileCarrier? =
        tenant.mobileCarriers.find { it.code.equals(paymentMethod.provider, true) }

    fun getSmsCodeEntity(): SmsCodeEntity =
        cacheManager.getCache(cacheName).get(cacheKey(), SmsCodeEntity::class.java)

    fun storeVerificationNumber(phoneNumber: String, verificationId: Long, carrier: String) {
        cacheManager.getCache(cacheName).put(
            cacheKey(),
            SmsCodeEntity(
                phoneNumber = phoneNumber,
                carrier = carrier,
                verificationId = verificationId
            )
        )
    }

    private fun toPaymentProvider(carrier: String): PaymentMethodProvider? =
        PaymentMethodProvider.values().find { it.name.equals(carrier, ignoreCase = true) }

    private fun sendVerificationCode(phoneNumber: String): Long {
        if (!togglesProvider.get().sendSmsCode)
            return -1

        return smsApi.sendVerification(
            SendVerificationRequest(
                phoneNumber = phoneNumber,
                language = localeResolver.resolveLocale(httpServletRequest).language
            )
        ).id
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
