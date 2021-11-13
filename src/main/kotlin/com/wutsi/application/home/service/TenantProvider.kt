package com.wutsi.application.home.service

import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.tenant.WutsiTenantApi
import com.wutsi.platform.tenant.dto.MobileCarrier
import com.wutsi.platform.tenant.dto.Tenant
import org.springframework.stereotype.Service

@Service
class TenantProvider(
    private val tenantApi: WutsiTenantApi,
    private val tracingContext: TracingContext,
) {
    fun get(): Tenant =
        tenantApi.getTenant(tenantId()).tenant

    fun mobileCarriers(tenant: Tenant) =
        tenant.mobileCarriers.filter { it.countries.any { it in tenant.countries } }

    fun logo(carrier: MobileCarrier): String? =
        carrier.logos.find { it.type == "PICTORIAL" }?.url

    private fun tenantId(): Long =
        tracingContext.tenantId()!!.toLong()
}
