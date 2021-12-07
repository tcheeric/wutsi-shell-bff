package com.wutsi.application.shell.endpoint.settings.picture.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.service.UserProvider
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

@RestController
@RequestMapping("/commands/upload-picture")
class UploadPictureCommand(
    private val accountApi: WutsiAccountApi,
    private val storageService: StorageService,
    private val userProvider: UserProvider,
    private val logger: KVLogger
) : AbstractCommand() {
    @PostMapping
    fun index(@RequestParam file: MultipartFile) {
        val contentType = Files.probeContentType(Path.of(file.originalFilename))
        logger.add("file_name", file.originalFilename)
        logger.add("file_content_type", contentType)

        // Upload file
        val userId = userProvider.id()
        val path = "user/$userId/picture/${UUID.randomUUID()}-${file.originalFilename}"
        val url = storageService.store(path, file.inputStream, contentType)
        logger.add("picture_url", url)

        // Update user profile
        accountApi.updateAccountAttribute(
            id = userId,
            name = "picture-url",
            request = UpdateAccountAttributeRequest(
                value = url.toString()
            )
        )
    }
}
