package com.wutsi.application.home.service

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

@ConfigurationProperties(prefix = "wutsi.toggles")
class Toggles {
    var sendSmsCode: Boolean = true
    var verifySmsCode: Boolean = true
}

@Service
@EnableConfigurationProperties(Toggles::class)
class TogglesProvider(
    private val toggles: Toggles
) {
    fun get(): Toggles = toggles
}
