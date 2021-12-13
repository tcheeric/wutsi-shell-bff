package com.wutsi.application.shell.endpoint.settings.profile.command

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.application.shell.endpoint.settings.profile.dto.UpdateProfileRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.platform.account.dto.UpdateAccountRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class UpdateProfileCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/commands/update-profile"
    }

    @Test
    fun update() {
        // WHEN
        val request = UpdateProfileRequest(
            displayName = "Roger Milla",
            language = "fr",
            country = "CA"
        )
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        assertEquals(request.language, response.headers["x-language"]?.get(0))

        val req = argumentCaptor<UpdateAccountRequest>()
        verify(accountApi).updateAccount(eq(ACCOUNT_ID), req.capture())
        assertEquals(request.country, req.firstValue.country)
        assertEquals(request.displayName, req.firstValue.displayName!!)
        assertEquals(request.language, req.firstValue.language)
    }
}
