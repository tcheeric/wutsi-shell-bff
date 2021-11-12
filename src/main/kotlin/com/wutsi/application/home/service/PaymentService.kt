package com.wutsi.application.home.service

import com.wutsi.platform.payment.WutsiPaymentApi
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.tenant.dto.Tenant
import feign.FeignException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PaymentService(
    private val paymentApi: WutsiPaymentApi,
    private val userProvider: UserProvider,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(PaymentService::class.java)
    }

    fun getBalance(tenant: Tenant): Money {
        try {
            val userId = userProvider.id()
            val account = paymentApi.getUserAccount(userId).account
            return Money(
                value = account.balance,
                currency = account.currency
            )
        } catch (ex: FeignException.NotFound) {
            return Money(currency = tenant.currency)
        } catch (ex: Throwable) {
            LOGGER.error("Unexpected error while resolving the balance", ex)
            return Money(currency = tenant.currency)
        }
    }
}
