package com.wutsi.application.shell.endpoint.qr.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.platform.account.dto.ListPaymentMethodResponse
import com.wutsi.platform.qr.WutsiQrApi
import com.wutsi.platform.qr.dto.CreateAccountQRCodeResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class QrCodeScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @MockBean
    private lateinit var qrApi: WutsiQrApi

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/qr-code"
    }

    @Test
    fun profile() {
        doReturn(CreateAccountQRCodeResponse("xxxxx")).whenever(qrApi).account()

        // GIVEN
        doReturn(ListPaymentMethodResponse()).whenever(accountApi).listPaymentMethods(any())

        // THEN
        assertEndpointEquals("/screens/qr/qr-code.json", url)
    }
}
