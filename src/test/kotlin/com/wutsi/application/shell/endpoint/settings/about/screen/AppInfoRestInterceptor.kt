package com.wutsi.application.shell.endpoint.settings.about.screen

import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

internal class AppInfoRestInterceptor : ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        request.headers["X-App-Name"] = "Foo"
        request.headers["X-App-Version"] = "1.1.100"
        request.headers["X-App-Build-Number"] = "43094039"

        return execution.execute(request, body)
    }
}
