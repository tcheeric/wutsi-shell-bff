package com.wutsi.application.shell.endpoint.settings.business.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.account.dto.Phone
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SettingsBusinessScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/settings/business"
    }

    @Test
    fun personal() {
        // THEN
        assertEndpointEquals("/screens/settings/business/business-off.json", url)
    }

    @Test
    fun business() {
        // GIVEN
        val account = Account(
            id = ACCOUNT_ID,
            displayName = "Ray Sponsible",
            country = "CM",
            language = "en",
            status = "ACTIVE",
            phone = Phone(
                id = 1,
                number = "+1237666666666",
                country = "CM"
            ),
            business = true,
            website = "https://www.google.ca",
            biography = "This is my bio",
            categoryId = 1000
        )
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        // THEN
        assertEndpointEquals("/screens/settings/business/business-on.json", url)
    }
}
