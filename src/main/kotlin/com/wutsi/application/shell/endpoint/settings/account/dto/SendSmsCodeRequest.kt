package com.wutsi.application.shell.endpoint.settings.account.dto

import javax.validation.constraints.NotEmpty

data class SendSmsCodeRequest(
    @get:NotEmpty
    val phoneNumber: String = ""
)
