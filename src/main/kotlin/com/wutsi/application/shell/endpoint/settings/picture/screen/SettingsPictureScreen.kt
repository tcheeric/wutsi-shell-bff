package com.wutsi.application.shell.endpoint.settings.picture.screen

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.Theme
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Camera
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.flutter.sdui.enums.CameraLensDirection
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/picture")
class SettingsPictureScreen(
    private val urlBuilder: URLBuilder,
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        return Screen(
            id = Page.SETTINGS_PICTURE,
            backgroundColor = Theme.WHITE_COLOR,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.WHITE_COLOR,
                foregroundColor = Theme.BLACK_COLOR,
                title = getText("page.settings.picture.app-bar.title"),
            ),
            child = Camera(
                uploadUrl = urlBuilder.build("commands/upload-picture"),
                name = "file",
                lensDirection = CameraLensDirection.back,
                action = Action(
                    type = Route,
                    url = "route:/.."
                ),
            ),
        ).toWidget()
    }
}
