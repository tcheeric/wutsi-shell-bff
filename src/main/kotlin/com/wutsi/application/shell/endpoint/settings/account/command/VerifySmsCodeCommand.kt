package com.wutsi.application.shell.endpoint.settings.account.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.settings.account.dto.VerifySmsCodeRequest
import com.wutsi.application.shell.exception.AccountAlreadyLinkedException
import com.wutsi.application.shell.exception.SmsCodeMismatchException
import com.wutsi.application.shell.service.AccountService
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.payment.PaymentMethodType.MOBILE
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
        service.linkAccount(MOBILE)
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
