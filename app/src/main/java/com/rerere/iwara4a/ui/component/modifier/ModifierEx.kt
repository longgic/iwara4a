package com.rerere.iwara4a.ui.component.modifier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import me.rerere.compose_setting.preference.rememberBooleanPreference

/**
 * 一个没有点击效果的 clickable modifier
 *
 * @param onClick 点击处理
 */
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}

/**
 * 自动根据用户设置对NSFW内容进行模糊
 *
 * 需要Android 12+支持
 */
fun Modifier.nsfw() = composed {
    val demoMode by rememberBooleanPreference(
        key = "demoMode",
        default = false
    )
    if(demoMode) this.blur(5.dp) else Modifier
}