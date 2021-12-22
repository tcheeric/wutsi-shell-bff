package com.wutsi.application.shell.endpoint.settings.picture.screen

import com.wutsi.application.shell.endpoint.AbstractQuery
import com.wutsi.application.shell.endpoint.Page
import com.wutsi.application.shell.endpoint.Theme
import com.wutsi.application.shell.service.URLBuilder
import com.wutsi.application.shell.service.UserProvider
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.ImageSource
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/picture")
class SettingsPictureScreen(
    private val urlBuilder: URLBuilder,
    private val userProvider: UserProvider
) : AbstractQuery() {
    @PostMapping
    fun index(): Widget {
        val me = userProvider.get()
        val pictureUrl = me.pictureUrl ?: ""
        return Screen(
            id = Page.SETTINGS_PICTURE,
            backgroundColor = Theme.WHITE_COLOR,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.WHITE_COLOR,
                foregroundColor = Theme.BLACK_COLOR,
                title = getText("page.settings.picture.app-bar.title"),
            ),
            child = Column(
                crossAxisAlignment = CrossAxisAlignment.center,
                children = listOf(
                    Row(
                        mainAxisAlignment = MainAxisAlignment.center,
                        children = listOf(
                            Container(
                                padding = 10.0,
                                alignment = Alignment.Center,
                                child = Image(
                                    url = pictureUrl,
                                    width = 256.0,
                                    height = 256.0
                                )
                            )
                        ),
                    ),
                    Divider(color = Theme.DIVIDER_COLOR),
                    Row(
                        mainAxisAlignment = MainAxisAlignment.spaceAround,
                        children = listOf(
                            Row(
                                mainAxisAlignment = MainAxisAlignment.center,
                                children = listOf(
                                    Icon(
                                        code = Theme.ICON_CAMERA,
                                        size = 16.0
                                    ),
                                    Input(
                                        name = "file",
                                        uploadUrl = urlBuilder.build("commands/upload-picture"),
                                        type = InputType.Image,
                                        imageSource = ImageSource.Camera,
                                        caption = getText("page.settings.picture.camera"),
                                        action = Action(
                                            type = ActionType.Route,
                                            url = "route:/.."
                                        ),
                                    )
                                ),
                            ),
                            Row(
                                mainAxisAlignment = MainAxisAlignment.center,
                                children = listOf(
                                    Icon(
                                        code = Theme.ICON_FOLDER,
                                        size = 16.0
                                    ),
                                    Input(
                                        name = "file",
                                        uploadUrl = urlBuilder.build("commands/upload-picture"),
                                        type = InputType.Image,
                                        imageSource = ImageSource.Gallery,
                                        caption = getText("page.settings.picture.gallery"),
                                        action = Action(
                                            type = ActionType.Route,
                                            url = "route:/.."
                                        )
                                    )
                                ),
                            ),
                        )
                    ),
                    Divider(color = Theme.DIVIDER_COLOR),
                )
            )
        ).toWidget()
    }
}
