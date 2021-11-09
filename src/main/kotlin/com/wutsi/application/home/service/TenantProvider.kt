package com.wutsi.application.home.service

import com.wutsi.platform.tenant.WutsiTenantApi
import com.wutsi.platform.tenant.dto.MobileCarrier
import com.wutsi.platform.tenant.dto.Tenant
import org.springframework.stereotype.Service

@Service
class TenantProvider(
    private val tenantApi: WutsiTenantApi
) {
    fun get(): Tenant =
        tenantApi.getTenant(1).tenant

    fun mobileCarriers(tenant: Tenant) =
        tenant.mobileCarriers.filter { it.countries.any { it in tenant.countries } }

    fun logo(carrier: MobileCarrier): String? =
        carrier.logos.find { it.type == "PICTORIAL" }?.url
}
