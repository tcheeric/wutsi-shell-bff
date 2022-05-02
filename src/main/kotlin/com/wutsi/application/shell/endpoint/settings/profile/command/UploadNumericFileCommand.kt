package com.wutsi.application.shell.endpoint.settings.profile.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.ecommerce.catalog.WutsiCatalogApi
import com.wutsi.ecommerce.catalog.dto.UpdateProductAttributeRequest
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
@RequestMapping("/commands/upload-numeric-file")
class UploadNumericFileCommand(
    private val catalogApi: WutsiCatalogApi,
    private val storageService: StorageService,
) : AbstractCommand() {
    @PostMapping
    fun index(
        @RequestParam("product-id") productId: Long,
        @RequestParam file: MultipartFile
    ) {
        val contentType = Files.probeContentType(Path.of(file.originalFilename))
        logger.add("file_name", file.originalFilename)
        logger.add("file_content_type", contentType)

        // Upload file
        val userId = securityContext.currentAccountId()
        val path = "catalog/$userId/product/$productId/file/${UUID.randomUUID()}-${file.originalFilename}"
        val url = storageService.store(path, file.inputStream, contentType)
        logger.add("file_url", url)

        // Update product
        catalogApi.updateProductAttribute(
            id = productId,
            name = "numeric-file-url",
            request = UpdateProductAttributeRequest(
                value = url.toString()
            )
        )
    }
}
