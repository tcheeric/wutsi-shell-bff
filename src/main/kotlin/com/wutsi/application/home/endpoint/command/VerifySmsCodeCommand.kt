package com.wutsi.application.home.endpoint.command

import com.wutsi.application.home.dto.VerifySmsCodeRequest
import com.wutsi.application.home.endpoint.AbstractCommand
import com.wutsi.application.home.exception.AccountAlreadyLinkedException
import com.wutsi.application.home.exception.SmsCodeMismatchException
import com.wutsi.application.home.service.AccountService
import com.wutsi.application.home.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/commands/verify-sms-code")
class VerifySmsCodeCommand(
    private val service: AccountService,
    private val urlBuilder: URLBuilder
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestBody @Valid request: VerifySmsCodeRequest): Action {
        service.verifyCode(request)
        service.linkAccount()
        return Action(
            type = ActionType.Route,
            url = urlBuilder.build("settings/accounts/link/success")
        )
    }

    @ExceptionHandler(SmsCodeMismatchException::class)
    fun onSmsCodeMismatchException(ex: SmsCodeMismatchException): Action =
        createErrorAction(ex, "page.verify-account-mobile.error.invalid-code")

    @ExceptionHandler(AccountAlreadyLinkedException::class)
    fun onAccountAlreadyLinkedException(ex: AccountAlreadyLinkedException): Action =
        createErrorAction(ex, "page.verify-account-mobile.error.already-linked")
}
