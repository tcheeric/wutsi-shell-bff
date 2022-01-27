package com.wutsi.application.shell.endpoint.home.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.platform.account.dto.AccountSummary
import com.wutsi.platform.account.dto.ListPaymentMethodResponse
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.account.dto.SearchAccountResponse
import com.wutsi.platform.payment.WutsiPaymentApi
import com.wutsi.platform.payment.dto.Balance
import com.wutsi.platform.payment.dto.GetBalanceResponse
import com.wutsi.platform.payment.dto.SearchTransactionResponse
import com.wutsi.platform.payment.dto.TransactionSummary
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import java.time.OffsetDateTime
import java.time.ZoneOffset

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class HomeScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @MockBean
    private lateinit var paymentApi: WutsiPaymentApi

    @MockBean
    private lateinit var togglesProvider: TogglesProvider

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port"

        doReturn(GetBalanceResponse(balance = Balance(amount = 10000.0, currency = "XAF"))).whenever(paymentApi)
            .getBalance(
                any()
            )

        val txs = listOf(
            createTransferTransactionSummary(102, USER_ID, type = "PAYMENT"),
            createCashInOutTransactionSummary(true, "A", "FAILED"),
            createCashInOutTransactionSummary(true, "A"),
            createTransferTransactionSummary(USER_ID, 100),
            createTransferTransactionSummary(101, USER_ID, "PENDING"),
            createTransferTransactionSummary(101, USER_ID),
            createCashInOutTransactionSummary(false, "B")
        )
        doReturn(SearchTransactionResponse(txs)).whenever(paymentApi).searchTransaction(any())

        val paymentMethods = listOf(
            createPaymentMethodSummary("A", "11111"),
            createPaymentMethodSummary("B", "22222"),
            createPaymentMethodSummary("C", "33333"),
        )
        doReturn(ListPaymentMethodResponse(paymentMethods)).whenever(accountApi).listPaymentMethods(any())

        val accounts = listOf(
            createAccount(USER_ID),
            createAccount(100),
            createAccount(101),
            createAccount(102),
            createAccount(103),
        )
        doReturn(SearchAccountResponse(accounts)).whenever(accountApi).searchAccount(any())
    }

    @Test
    fun empty() {
        doReturn(GetBalanceResponse(balance = Balance(amount = 0.0, currency = "XAF"))).whenever(paymentApi)
            .getBalance(
                any()
            )

        doReturn(SearchTransactionResponse()).whenever(paymentApi).searchTransaction(any())
        doReturn(ListPaymentMethodResponse()).whenever(accountApi).listPaymentMethods(any())

        assertEndpointEquals("/screens/home-empty.json", url)
    }

    @Test
    fun home() {
        assertEndpointEquals("/screens/home.json", url)
    }

    @Test
    fun homeAccountEnabled() {
        doReturn(true).whenever(togglesProvider).isAccountEnabled()

        assertEndpointEquals("/screens/home-account-enabled.json", url)
    }

    @Test
    fun homeContactEnabled() {
        doReturn(true).whenever(togglesProvider).isContactEnabled()

        assertEndpointEquals("/screens/home-contact-enabled.json", url)
    }

    @Test
    fun homeFeedbackEnabled() {
        doReturn(true).whenever(togglesProvider).isFeedbackEnabled()

        assertEndpointEquals("/screens/home-feedback-enabled.json", url)
    }

    private fun createTransferTransactionSummary(
        accountId: Long,
        recipientId: Long,
        status: String = "SUCCESSFUL",
        type: String = "TRANSFER"
    ) =
        TransactionSummary(
            accountId = accountId,
            recipientId = recipientId,
            type = type,
            status = status,
            net = 10000.0,
            amount = 10000.0,
            description = "Sample description",
            created = OffsetDateTime.of(2021, 1, 1, 1, 1, 1, 1, ZoneOffset.UTC)
        )

    private fun createCashInOutTransactionSummary(
        cashin: Boolean,
        paymentMethodToken: String,
        status: String = "SUCCESSFUL"
    ) = TransactionSummary(
        accountId = USER_ID,
        type = if (cashin) "CASHIN" else "CASHOUT",
        status = status,
        net = 10000.0,
        amount = 10000.0,
        paymentMethodToken = paymentMethodToken,
        description = "Sample description",
        created = OffsetDateTime.of(2021, 1, 1, 1, 1, 1, 1, ZoneOffset.UTC)
    )

    private fun createPaymentMethodSummary(token: String, maskedNumber: String) = PaymentMethodSummary(
        token = token,
        maskedNumber = maskedNumber
    )

    private fun createAccount(id: Long) = AccountSummary(
        id = id,
        displayName = "Name $id"
    )
}
