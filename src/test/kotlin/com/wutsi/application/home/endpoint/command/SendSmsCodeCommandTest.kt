package com.wutsi.application.home.endpoint.command

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.home.dto.SendSmsCodeRequest
import com.wutsi.application.home.endpoint.AbstractEndpointTest
import com.wutsi.application.home.entity.SmsCodeEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.platform.sms.WutsiSmsApi
import com.wutsi.platform.sms.dto.SendVerificationResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SendSmsCodeCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    public val port: Int = 0

    private lateinit var url: String

    @MockBean
    lateinit var smsApi: WutsiSmsApi

    @MockBean
    lateinit var cacheManager: CacheManager
    lateinit var cache: Cache

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/commands/send-sms-code"

        cache = mock()
        doReturn(cache).whenever(cacheManager).getCache(any())
    }

    @Test
    fun sendVerification() {
        // GIVEN
        val verificationId = 777L
        doReturn(SendVerificationResponse(verificationId)).whenever(smsApi).sendVerification(any())

        // WHEN
        val request = SendSmsCodeRequest(
            phoneNumber = PHONE_NUMBER
        )
        val response = rest.postForEntity(url, request, Action::class.java)

        assertEquals(200, response.statusCodeValue)
        val action = response.body
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0/settings/accounts/verify/mobile", action.url)

        val entity = argumentCaptor<SmsCodeEntity>()
        verify(cache).put(any(), entity.capture())
        assertEquals(request.phoneNumber, entity.firstValue.phoneNumber)
        assertEquals(verificationId, entity.firstValue.verificationId)
    }

    @Test
    fun invalidPhoneNumber() {
        // WHEN
        val request = SendSmsCodeRequest(
            phoneNumber = "111111111"
        )
        val response = rest.postForEntity(url, request, Action::class.java)

        assertEquals(200, response.statusCodeValue)
        val action = response.body
        assertEquals(ActionType.Prompt, action.type)
        assertEquals(DialogType.Error, action.prompt?.type)

        verify(cache, never()).put(any(), any())
    }
}
