package com.wutsi.application.shell.endpoint.contact.dto

data class SyncContactRequest(
    val phoneNumbers: List<String> = emptyList()
)
