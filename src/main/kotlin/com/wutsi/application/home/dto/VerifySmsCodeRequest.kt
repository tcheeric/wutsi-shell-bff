package com.wutsi.application.home.dto

import javax.validation.constraints.NotEmpty

data class VerifySmsCodeRequest(
    @get:NotEmpty
    val code: String = ""
)
