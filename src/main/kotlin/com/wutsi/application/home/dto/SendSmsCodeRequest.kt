package com.wutsi.application.home.dto

import javax.validation.constraints.NotEmpty

data class SendSmsCodeRequest(
    @get:NotEmpty
    val phoneNumber: String = ""
)
