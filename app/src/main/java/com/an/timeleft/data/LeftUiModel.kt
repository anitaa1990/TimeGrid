package com.an.timeleft.data

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.an.timeleft.R

data class LeftUiModel(
    val category: LeftCategory,
    val title: String,
    val totalTime: Long,
    val timeCompleted: Long,
    val timeLeftString: UiString,
)

enum class LeftCategory {
    Year, Month, Life
}

sealed class UiString {
    data class ResourceString(@StringRes val resId: Int) : UiString()
    data class ResourceStringWithArgs(@StringRes val resId: Int, val args: Long) : UiString()
}

@Composable
fun UiString.asString(): String {
    return when (this) {
        is UiString.ResourceString -> stringResource(id = resId)
        is UiString.ResourceStringWithArgs -> stringResource(id = resId, args)
    }
}

@Composable
fun UiString.alpha(): Float {
    return if (this.asString() == stringResource(R.string.text_birth_date)) {
        0.4f
    } else 1f
}

