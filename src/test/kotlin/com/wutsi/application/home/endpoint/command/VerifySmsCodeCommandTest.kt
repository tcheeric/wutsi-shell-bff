package com.wutsi.application.home.endpoint.command

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.home.dto.VerifySmsCodeRequest
import com.wutsi.application.home.endpoint.AbstractEndpointTest
import com.wutsi.application.home.entity.SmsCodeEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.platform.account.dto.AddPaymentMethodRequest
import com.wutsi.platform.account.dto.AddPaymentMethodResponse
import com.wutsi.platform.payment.PaymentMethodProvider
import com.wutsi.platform.payment.PaymentMethodType
import com.wutsi.platform.sms.WutsiSmsApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa")
internal class VerifySmsCodeCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    public val port: Int = 0

    private lateinit var url: String

    @MockBean
    lateinit var smsApi: WutsiSmsApi

    private lateinit var state: SmsCodeEntity

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/commands/verify-sms-code"

        state = SmsCodeEntity(phoneNumber = "+23799509999", verificationId = 777L, carrier = "mtn")
        doReturn(state).whenever(cache).get(any(), eq(SmsCodeEntity::class.java))
    }

    @Test
    fun index() {
        // GIVEN
        val token = "xxxx"
        doReturn(AddPaymentMethodResponse(token)).whenever(accountApi).addPaymentMethod(any(), any())

        // WHEN
        val request = VerifySmsCodeRequest(
            code = "123456"
        )
        val response = rest.postForEntity(url, request, Action::class.java)

        assertEquals(200, response.statusCodeValue)
        val action = response.body
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0/settings/accounts/link/success", action.url)

        verify(smsApi).validateVerification(state.verificationId, request.code)

        val entity = argumentCaptor<AddPaymentMethodRequest>()
        verify(accountApi).addPaymentMethod(eq(ACCOUNT_ID), entity.capture())
        assertEquals(state.phoneNumber, entity.firstValue.phoneNumber)
        assertEquals(PaymentMethodType.MOBILE.name, entity.firstValue.type)
        assertEquals(PaymentMethodProvider.MTN.name, entity.firstValue.provider)
        assertEquals(ACCOUNT_NAME, entity.firstValue.ownerName)
    }

    @Test
    fun verificationFailed() {
        // GIVEN
        doThrow(RuntimeException::class).whenever(smsApi).validateVerification(any(), any())

        // WHEN
        val request = VerifySmsCodeRequest(
            code = "123456"
        )
        val response = rest.postForEntity(url, request, Action::class.java)

        assertEquals(200, response.statusCodeValue)
        val action = response.body
        assertEquals(ActionType.Prompt, action.type)
        assertEquals(DialogType.Error, action.prompt?.type)
    }
}
