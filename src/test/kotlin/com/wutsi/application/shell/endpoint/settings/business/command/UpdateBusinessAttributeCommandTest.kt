package com.wutsi.application.shell.endpoint.settings.business.command

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class UpdateBusinessAttributeCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun invoke() {
        // GIVEN
        val request = UpdateAccountAttributeRequest(value = "oreoiroei")
        val url = "http://localhost:$port/commands/update-business-attribute?name=biography"
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)
        val action = response.body
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/..", action.url)
        assertEquals(false, action.replacement)

        verify(accountApi).updateAccountAttribute(ACCOUNT_ID, "biography", UpdateAccountAttributeRequest(request.value))
    }

    @Test
    fun business() {
        // GIVEN
        val request = UpdateAccountAttributeRequest(value = "true")
        val url = "http://localhost:$port/commands/update-business-attribute?name=business"
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)
        val action = response.body
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0/settings/business", action.url)
        assertEquals(true, action.replacement)

        verify(accountApi).updateAccountAttribute(ACCOUNT_ID, "business", UpdateAccountAttributeRequest(request.value))
    }
}
