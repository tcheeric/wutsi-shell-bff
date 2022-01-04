package com.wutsi.application.shell.endpoint.settings.screen

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.shell.endpoint.AbstractEndpointTest
import com.wutsi.application.shell.service.TogglesProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SettingsScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var togglesProvider: TogglesProvider

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/settings"

        doReturn(true).whenever(togglesProvider).isAccountEnabled()
    }

    @Test
    fun index() {
        // THEN
        assertEndpointEquals("/screens/settings/settings.json", url)
    }

    @Test
    fun accountDisabled() {
        doReturn(false).whenever(togglesProvider).isAccountEnabled()
        assertEndpointEquals("/screens/settings/settings-no-account.json", url)
    }
}
