package com.wutsi.application.home.service

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa")
internal class TogglesProviderTest {
    @Autowired
    private lateinit var togglesProvider: TogglesProvider

    @Test
    fun get() {
        val toggles = togglesProvider.get()

        assertTrue(toggles.sendSmsCode)
        assertTrue(toggles.verifySmsCode)
    }
}
