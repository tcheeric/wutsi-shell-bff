package com.wutsi.application.home.endpoint.screen.home

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.home.endpoint.AbstractEndpointTest
import com.wutsi.platform.account.dto.ListPaymentMethodResponse
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.payment.PaymentMethodProvider
import com.wutsi.platform.payment.PaymentMethodType
import com.wutsi.platform.payment.WutsiPaymentApi
import com.wutsi.platform.payment.dto.Balance
import com.wutsi.platform.payment.dto.GetBalanceResponse
import feign.FeignException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class HomeScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    public val port: Int = 0

    private lateinit var url: String

    @MockBean
    private lateinit var paymentApi: WutsiPaymentApi

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port"

        val balance = Balance(
            currency = "XAF",
            amount = 150000.0
        )
        doReturn(GetBalanceResponse(balance)).whenever(paymentApi).getBalance(any())

        val m1 = PaymentMethodSummary(
            token = "123",
            type = PaymentMethodType.MOBILE.name,
            provider = PaymentMethodProvider.MTN.name,
            maskedNumber = "xxxxx"
        )
        val m2 = PaymentMethodSummary(
            token = "456",
            type = PaymentMethodType.MOBILE.name,
            provider = PaymentMethodProvider.ORANGE.name,
            maskedNumber = "yyy"
        )
        doReturn(ListPaymentMethodResponse(listOf(m1, m2))).whenever(accountApi).listPaymentMethods(any())
    }

    @Test
    fun noPaymentMethod() {
        // GIVEN
        doReturn(ListPaymentMethodResponse()).whenever(accountApi).listPaymentMethods(any())

        // THEN
        assertEndpointEquals("/screens/home-without-payment-method.json", url)
    }

    @Test
    fun withPaymentMethods() {
        assertEndpointEquals("/screens/home-with-payment-method.json", url)
    }

    @Test
    fun withUnsupportedPaymentMethods() {
        val m1 = PaymentMethodSummary(
            token = "123",
            type = PaymentMethodType.CASH.name,
            provider = PaymentMethodProvider.NEXTTEL.name,
            maskedNumber = "xxxxx"
        )
        doReturn(ListPaymentMethodResponse(listOf(m1))).whenever(accountApi).listPaymentMethods(any())

        assertEndpointEquals("/screens/home-without-payment-method.json", url)
    }

    @Test
    fun noBalance() {
        // GIVEN
        doReturn(GetBalanceResponse(balance = Balance(currency = "XAF"))).whenever(paymentApi).getBalance(any())

        // THEN
        assertEndpointEquals("/screens/home-no-balance.json", url)
    }
}
