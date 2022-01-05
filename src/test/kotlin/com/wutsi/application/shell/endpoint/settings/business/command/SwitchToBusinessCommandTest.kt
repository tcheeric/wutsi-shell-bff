package com.wutsi.application.shell.endpoint.settings.business.command

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SwitchToBusinessCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/commands/switch-to-business"
    }

    @Test
    fun invoke() {
        // GIVEN
        val response = rest.postForEntity(url, null, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)
        val action = response.body
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0/settings/business", action.url)

        verify(accountApi).updateAccountAttribute(ACCOUNT_ID, "business", UpdateAccountAttributeRequest("true"))
    }
}
