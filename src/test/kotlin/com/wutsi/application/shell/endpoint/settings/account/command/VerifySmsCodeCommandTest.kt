package com.wutsi.application.shell.endpoint.settings.account.command

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.application.shell.endpoint.settings.account.dto.VerifySmsCodeRequest
import com.wutsi.application.shell.entity.SmsCodeEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.platform.account.dto.AddPaymentMethodResponse
import com.wutsi.platform.sms.WutsiSmsApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.MessageSource
import org.springframework.test.context.ActiveProfiles
import java.util.Locale

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa")
internal class VerifySmsCodeCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @MockBean
    lateinit var smsApi: WutsiSmsApi

    @Autowired
    lateinit var messageSource: MessageSource

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
        assertEquals(
            "https://wutsi-gateway-test.herokuapp.com/login/?phone=%2B1237666666666&icon=f197&screen-id=page.settings.account.link.pin&title=Authorization&sub-title=Enter+your+PIN+to+link+the+account&auth=false&return-to-route=false&return-url=http%3A%2F%2Flocalhost%3A0%2Fcommands%2Flink-account",
            action.url
        )

        verify(smsApi).validateVerification(state.verificationId, request.code)
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
        assertEquals(DialogType.Error.name, action.prompt?.attributes?.get("type"))
        assertEquals(
            messageSource.getMessage(
                "page.verify-account-mobile.error.invalid-code",
                emptyArray(),
                Locale.ENGLISH
            ),
            action.prompt?.attributes?.get("message")
        )
    }
}
