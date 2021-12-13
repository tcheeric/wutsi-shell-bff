package com.wutsi.application.shell.endpoint.settings.profile.dto

data class UpdateProfileRequest(
    val displayName: String,
    val country: String,
    val language: String
)
