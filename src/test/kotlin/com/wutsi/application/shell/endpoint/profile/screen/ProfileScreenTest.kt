package com.wutsi.application.shell.endpoint.profile.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.account.dto.Phone
import com.wutsi.platform.contact.WutsiContactApi
import com.wutsi.platform.contact.dto.SearchContactResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ProfileScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var contactApi: WutsiContactApi

    @Test
    fun personal() {
        // GIVEN
        doReturn(SearchContactResponse()).whenever(contactApi).searchContact(any())

        val account = Account(
            id = 5555,
            displayName = "Ray Sponsible",
            country = "CM",
            language = "en",
            status = "ACTIVE",
            phone = Phone(
                id = 1,
                number = "+1237666666666",
                country = "CM"
            ),
            business = false,
        )
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(555L)

        // WHEN
        val url = "http://localhost:$port/profile?id=555"
        val response = rest.postForEntity(url, null, Any::class.java)

        // THEN
        assertJsonEquals("/screens/profile/personal.json", response.body)
    }

    @Test
    fun business() {
        // GIVEN
        doReturn(SearchContactResponse()).whenever(contactApi).searchContact(any())

        val account = Account(
            id = 5555,
            displayName = "Ray Sponsible",
            country = "CM",
            language = "en",
            status = "ACTIVE",
            phone = Phone(
                id = 1,
                number = "+1237666666666",
                country = "CM"
            ),
            pictureUrl = "https://img.com/1.png",
            business = true,
            biography = "This is my bio",
            categoryId = 1000L,
        )
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(555L)

        // WHEN
        val url = "http://localhost:$port/profile?id=555"
        val response = rest.postForEntity(url, null, Any::class.java)

        // THEN
        assertJsonEquals("/screens/profile/business.json", response.body)
    }
}
