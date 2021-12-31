package com.wutsi.application.shell.endpoint.scan.command

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.application.shell.endpoint.scan.dto.ScanRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.qr.WutsiQrApi
import com.wutsi.platform.qr.dto.DecodeQRCodeRequest
import com.wutsi.platform.qr.dto.DecodeQRCodeResponse
import com.wutsi.platform.qr.dto.Entity
import com.wutsi.platform.qr.error.ErrorURN
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ScanCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @MockBean
    lateinit var qrApi: WutsiQrApi

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/commands/scan"
    }

    @Test
    fun paymentRequest() {
        // GIVEN
        val entity = Entity("payment-request", "1111")
        doReturn(DecodeQRCodeResponse(entity)).whenever(qrApi).decode(any())

        // WHEN
        val request = ScanRequest(
            code = "xxxxxx"
        )
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val req = argumentCaptor<DecodeQRCodeRequest>()
        verify(qrApi).decode(req.capture())
        assertEquals(request.code, req.firstValue.token)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("https://wutsi-gateway-test.herokuapp.com/cash/pay/confirm?payment-request-id=1111", action.url)
    }

    @Test
    fun account() {
        // GIVEN
        val entity = Entity("account", "1111")
        doReturn(DecodeQRCodeResponse(entity)).whenever(qrApi).decode(any())

        // WHEN
        val request = ScanRequest(
            code = "xxxxxx"
        )
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val req = argumentCaptor<DecodeQRCodeRequest>()
        verify(qrApi).decode(req.capture())
        assertEquals(request.code, req.firstValue.token)

        val action = response.body!!
        assertEquals(ActionType.Prompt, action.type)
        assertEquals(getText("prompt.error.unsupported-qr-code"), action.prompt?.attributes?.get("message"))
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
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val req = argumentCaptor<DecodeQRCodeRequest>()
        verify(qrApi).decode(req.capture())
        assertEquals(request.code, req.firstValue.token)

        val action = response.body!!
        assertEquals(ActionType.Prompt, action.type)
        assertEquals(getText("prompt.error.unsupported-qr-code"), action.prompt?.attributes?.get("message"))
    }

    @Test
    fun expired() {
        val ex = createFeignException(ErrorURN.EXPIRED.urn)
        doThrow(ex).whenever(qrApi).decode(any())

        // WHEN
        val request = ScanRequest(
            code = "xxxxxx"
        )
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body!!
        assertEquals(ActionType.Prompt, action.type)
        assertEquals(getText("prompt.error.expired-qr-code"), action.prompt?.attributes?.get("message"))
    }

    @Test
    fun malformed() {
        val ex = createFeignException(ErrorURN.MALFORMED_TOKEN.urn)
        doThrow(ex).whenever(qrApi).decode(any())

        // WHEN
        val request = ScanRequest(
            code = "xxxxxx"
        )
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body!!
        assertEquals(ActionType.Prompt, action.type)
        assertEquals(getText("prompt.error.malformed-qr-code"), action.prompt?.attributes?.get("message"))
    }
}
