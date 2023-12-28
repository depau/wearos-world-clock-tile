package gay.depau.worldclocktile.presentation.views

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Confirmation
import androidx.wear.compose.material.dialog.Dialog
import androidx.wear.remote.interactions.RemoteActivityHelper
import androidx.wear.tooling.preview.devices.WearDevices
import gay.depau.worldclocktile.BuildConfig
import gay.depau.worldclocktile.composables.MainView
import gay.depau.worldclocktile.composables.ScalingLazyColumnWithRSB
import java.util.concurrent.Executors
import gay.depau.worldclocktile.shared.R as sharedR


@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun OpenOnPhoneDialog(showDialog: Boolean, dismiss: () -> Unit) {
    Dialog(onDismissRequest = { dismiss() }, showDialog = showDialog) {
        val animation = AnimatedImageVector.animatedVectorResource(androidx.wear.R.drawable.open_on_phone_animation)
        Confirmation(
            onTimeout = dismiss,
            icon = {
                // Initially, animation is static and shown at the start position (atEnd = false).
                // Then, we use the EffectAPI to trigger a state change to atEnd = true,
                // which plays the animation from start to end.
                var atEnd by remember { mutableStateOf(false) }
                DisposableEffect(Unit) {
                    atEnd = true
                    onDispose {}
                }
                Image(
                    painter = rememberAnimatedVectorPainter(animation, atEnd), contentDescription = "Open on phone",
                    modifier = Modifier.size(48.dp)
                )
            },
            durationMillis = animation.totalDuration * 2L,
        ) {
            Text(
                text = "Sent to phone", textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun simpleStringWithLink(prefix: String, linkText: String, link: String) = buildAnnotatedString {
    val str = prefix + linkText
    append(str)
    val startIndex = str.indexOf(linkText)
    val endIndex = startIndex + linkText.length

    addStyle(
        style = SpanStyle(
            color = MaterialTheme.colors.primary, textDecoration = TextDecoration.Underline
        ), start = startIndex, end = endIndex
    )
    addStringAnnotation(
        tag = "URL", annotation = link, start = startIndex, end = endIndex
    )
}

private fun openLink(annotatedStr: AnnotatedString, context: Context) {
    val remoteActivityHelper = RemoteActivityHelper(context, Executors.newSingleThreadExecutor())
    remoteActivityHelper.startRemoteActivity(
        Intent(Intent.ACTION_VIEW).addCategory(Intent.CATEGORY_BROWSABLE).setData(
            Uri.parse(
                annotatedStr.getStringAnnotations("URL", 0, annotatedStr.length).first().item
            )
        ), null
    )
}

@Composable
fun AboutView() {
    val listState = rememberScalingLazyListState(initialCenterItemIndex = 1)
    val context = LocalContext.current

    MainView(listState = listState) {
        ScalingLazyColumnWithRSB(
            state = listState,
            autoCentering = AutoCenteringParams(itemIndex = 1),
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 16.dp, alignment = Alignment.Top
            ),
        ) {
            item {
                Text(
                    softWrap = true, textAlign = TextAlign.Center, text = "About app",
                    style = MaterialTheme.typography.title1
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = sharedR.drawable.ic_launcher_noborder),
                        contentDescription = "App icon", modifier = Modifier.size(64.dp)
                    )
                }
            }
            item {
                Text(
                    softWrap = true, textAlign = TextAlign.Center, lineHeight = 16.sp,
                    text = "World Clock\nfor Wear OS", style = MaterialTheme.typography.caption2
                )
            }
            item {
                Text(
                    softWrap = true, textAlign = TextAlign.Center, lineHeight = 16.sp,
                    text = "Version ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.caption2
                )
            }
            item {
                var showDialog by remember { mutableStateOf(false) }
                val text = simpleStringWithLink(
                    prefix = "Brought to you by\n", linkText = "Davide Depau", link = "https://depau.eu"
                )
                ClickableText(softWrap = true, text = text, style = MaterialTheme.typography.caption1.copy(
                    textAlign = TextAlign.Center, color = MaterialTheme.colors.onSurface
                ), onClick = {
                    openLink(text, context)
                    showDialog = true
                })
                OpenOnPhoneDialog(showDialog) { showDialog = false }
            }
            item {
                Text(
                    softWrap = true, textAlign = TextAlign.Center,
                    text = "Licensed under the GNU General Public License v3.0; some parts are licensed under the Apache License 2.0",
                    style = MaterialTheme.typography.caption2
                )
            }
            item {
                var showDialog by remember { mutableStateOf(false) }
                val text = simpleStringWithLink(
                    prefix = "More info, issues and feedback on ", linkText = "GitHub",
                    link = "https://github.com/depau/wearos-world-clock-tile"
                )
                ClickableText(softWrap = true, text = text, style = MaterialTheme.typography.caption1.copy(
                    textAlign = TextAlign.Center, color = MaterialTheme.colors.onSurface
                ), onClick = {
                    openLink(text, context)
                    showDialog = true
                })
                OpenOnPhoneDialog(showDialog) { showDialog = false }
            }
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true, name = "About screen")
@Composable
fun AboutViewPreview() {
    AboutView()
}