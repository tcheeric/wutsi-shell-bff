package com.wutsi.application.shell.endpoint.feedback.service

import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shell.endpoint.feedback.dto.SendFeedbackRequest
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.mail.WutsiMailApi
import com.wutsi.platform.mail.dto.Message
import com.wutsi.platform.mail.dto.Party
import com.wutsi.platform.mail.dto.SendMessageRequest
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class FeedbackService(
    private val mailApi: WutsiMailApi,
    private val securityContext: SecurityContext,
    private val tracingContext: TracingContext,
    private val tenantProvider: TenantProvider
) {
    @Async
    fun send(request: SendFeedbackRequest) {
        val user = securityContext.currentAccount()
        val tenant = tenantProvider.get()
        mailApi.sendMessage(
            request = SendMessageRequest(
                recipient = Party(email = tenant.supportEmail),
                content = Message(
                    mimeType = "text/plain",
                    subject = "User Feedback",
                    body = """
                        ${request.message}
                        --------------------------------------
                        User: ${user.id} - ${user.displayName}
                        Device-ID: ${tracingContext.deviceId()}
                        Trace-ID: ${tracingContext.traceId()}
                        Client-Info: ${tracingContext.clientInfo()}
                    """.trimIndent()
                )
            )
        )
    }
}
