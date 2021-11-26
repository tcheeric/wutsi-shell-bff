package com.wutsi.application.shell.service

import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.core.security.WutsiPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UserProvider(private val accountApi: WutsiAccountApi) {
    fun id(): Long =
        principal().id.toLong()

    fun get(): Account =
        accountApi.getAccount(id()).account

    fun principal(): WutsiPrincipal =
        SecurityContextHolder.getContext().authentication.principal as WutsiPrincipal
}
