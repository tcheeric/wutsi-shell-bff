package com.wutsi.application.shell.endpoint.settings.profile.command

import com.wutsi.application.shell.endpoint.AbstractCommand
import com.wutsi.application.shell.endpoint.settings.profile.entity.BusinessEntity
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.Cache

abstract class AbstractBusinessCommand : AbstractCommand() {
    @Autowired
    protected lateinit var cache: Cache

    @Autowired
    private lateinit var tracingContext: TracingContext

    protected fun getKey(): String =
        tracingContext.deviceId() + "-business-data"

    protected fun getData(key: String): BusinessEntity =
        cache.get(key, BusinessEntity::class.java)
            ?: BusinessEntity()
}
