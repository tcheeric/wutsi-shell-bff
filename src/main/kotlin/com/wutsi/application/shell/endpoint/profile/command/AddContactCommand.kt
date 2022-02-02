package com.wutsi.application.shell.endpoint.profile.command

import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.contact.WutsiContactApi
import com.wutsi.platform.contact.dto.CreateContactRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/add-contact")
class AddContactCommand(
    private val contactApi: WutsiContactApi,
    private val accountApi: WutsiAccountApi,
    private val urlBuilder: URLBuilder
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestParam(name = "contact-id") contactId: Long): Action {
        val contact = accountApi.getAccount(contactId).account
        contactApi.createContact(
            CreateContactRequest(
                contactId = contactId
            )
        )
        return Action(
            type = ActionType.Prompt,
            prompt = Dialog(
                title = getText("prompt.message.title"),
                message = getText("page.profile.contact-added", arrayOf(contact.displayName ?: "")),
                actions = listOf(
                    Button(
                        caption = getText("button.ok"),
                        action = Action(
                            type = ActionType.Route,
                            url = urlBuilder.build("profile?id=$contactId")
                        )
                    )
                )
            ).toWidget()
        )
    }
}
