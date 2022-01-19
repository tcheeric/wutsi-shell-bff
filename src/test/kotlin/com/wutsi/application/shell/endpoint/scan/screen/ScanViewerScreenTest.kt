package com.wutsi.application.shell.endpoint.scan.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.application.shell.endpoint.scan.dto.ScanRequest
import com.wutsi.flutter.sdui.Widget
import com.wutsi.platform.qr.WutsiQrApi
import com.wutsi.platform.qr.dto.DecodeQRCodeResponse
import com.wutsi.platform.qr.dto.Entity
import com.wutsi.platform.qr.error.ErrorURN
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ScanViewerScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    lateinit var qrApi: WutsiQrApi

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/scan/viewer"
    }

    @Test
    fun payment() {
        // GIVEN
        val entity = Entity("payment-request", "1111")
        doReturn(DecodeQRCodeResponse(entity)).whenever(qrApi).decode(any())

        // WHEN
        val request = ScanRequest(
            code = "xxxxxx"
        )
        val response = rest.postForEntity(url, request, Widget::class.java)

        // THEN
        assertJsonEquals("/screens/scan/viewer-payment.json", response.body)
    }

    @Test
    fun contact() {
        // GIVEN
        val entity = Entity("contact", "1111")
        doReturn(DecodeQRCodeResponse(entity)).whenever(qrApi).decode(any())

        // WHEN
        val request = ScanRequest(
            code = "xxxxxx"
        )
        val response = rest.postForEntity(url, request, Widget::class.java)

        // THEN
        assertJsonEquals("/screens/scan/viewer-contact.json", response.body)
    }

    @Test
    fun url() {
        // GIVEN
        val entity = Entity("url", "https://www.google.ca")
        doReturn(DecodeQRCodeResponse(entity)).whenever(qrApi).decode(any())

        // WHEN
        val request = ScanRequest(
            code = "xxxxxx"
        )
        val response = rest.postForEntity(url, request, Widget::class.java)

        // THEN
        assertJsonEquals("/screens/scan/viewer-url.json", response.body)
    }

    @Test
    fun transactionApproval() {
        // GIVEN
        val entity = Entity("transaction-approval", "xxxx")
        doReturn(DecodeQRCodeResponse(entity)).whenever(qrApi).decode(any())

        // WHEN
        val request = ScanRequest(
            code = "xxxxxx"
        )
        val response = rest.postForEntity(url, request, Widget::class.java)

        // THEN
        assertJsonEquals("/screens/scan/viewer-transaction-approval.json", response.body)
    }

    @Test
    fun invalid() {
        // GIVEN
        val ex = createFeignException(ErrorURN.EXPIRED.urn)
        doThrow(ex).whenever(qrApi).decode(any())

        // WHEN
        val request = ScanRequest(
            code = "xxxxxx"
        )
        val response = rest.postForEntity(url, request, Widget::class.java)

        // THEN
        assertJsonEquals("/screens/scan/viewer-invalid.json", response.body)
    }
}
