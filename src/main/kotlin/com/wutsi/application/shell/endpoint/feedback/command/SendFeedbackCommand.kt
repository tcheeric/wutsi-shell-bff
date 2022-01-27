package com.wutsi.application.shell.endpoint.feedback.command

import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.feedback.dto.SendFeedbackRequest
import com.wutsi.application.shell.endpoint.feedback.service.FeedbackService
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.platform.contact.WutsiContactApi
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/send-feedback")
class SendFeedbackCommand(
    private val contactApi: WutsiContactApi,
    private val urlBuilder: URLBuilder,
    private val service: FeedbackService,
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestBody request: SendFeedbackRequest): Action {
        service.send(request)
        return Action(
            type = ActionType.Prompt,
            prompt = Dialog(
                type = DialogType.Information,
                message = getText("page.feedback.sent"),
                actions = listOf(
                    Button(
                        caption = getText("page.feedback.button.ok"),
                        action = Action(
                            type = ActionType.Route,
                            url = "route:/~"
                        )
                    )
                )
            ).toWidget()
        )
    }
}
