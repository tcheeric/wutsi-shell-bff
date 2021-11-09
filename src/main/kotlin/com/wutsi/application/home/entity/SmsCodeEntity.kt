package com.wutsi.application.home.entity

import java.io.Serializable

data class SmsCodeEntity(
    val phoneNumber: String = "",
    val verificationId: Long = -1
) : Serializable
