package com.wutsi.application.home.config

import com.wutsi.application.home.service.RequestLocaleResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.web.servlet.LocaleResolver

@Configuration
class InternationalizationConfiguration {
    @Bean
    fun messageSource(): ResourceBundleMessageSource? {
        val messageSource = ResourceBundleMessageSource()
        messageSource.setBasename("messages")
        return messageSource
    }

    @Bean
    fun localeResolver(): LocaleResolver =
        RequestLocaleResolver()
}
