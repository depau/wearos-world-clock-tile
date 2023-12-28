package gay.depau.worldclock.mobile

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import gay.depau.worldclock.mobile.ui.theme.WorldClockTheme
import gay.depau.worldclock.mobile.utils.WriteReviewHelper
import gay.depau.worldclock.mobile.utils.activity
import gay.depau.worldclocktile.shared.R as sharedR

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorldClockTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    AboutView()
                }
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutView() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        val box = createRef()

        Column(modifier = Modifier
            .constrainAs(box) {
                centerTo(parent)
            }
            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(48.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${stringResource(R.string.app_name)} v${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 28.sp),
                    textAlign = TextAlign.Center,
                )
                SelectionContainer {
                    Text(
                        text = "${BuildConfig.APPLICATION_ID}\n v${BuildConfig.VERSION_NAME}+${BuildConfig.VERSION_CODE}, ${BuildConfig.BUILD_TYPE}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp, fontFamily = FontFamily(Typeface.MONOSPACE)
                        ),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            val iconBackgroundColor = MaterialTheme.colorScheme.onSurfaceVariant
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp), horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = sharedR.drawable.ic_launcher_noborder),
                    contentDescription = null,
                    modifier = Modifier.size(128.dp),
                )
            }

            val annotatedText = buildAnnotatedString {
                val name = "Davide Depau"
                val contributors = stringResource(R.string.contributors)
                val github = "GitHub"
                val str = stringResource(R.string.developed_by, name, contributors, github)
                val nameStart = str.indexOf(name)
                val nameEnd = nameStart + name.length
                val contributorsStart = str.indexOf(contributors)
                val contributorsEnd = contributorsStart + contributors.length
                val githubStart = str.indexOf(github)
                val githubEnd = githubStart + github.length
                append(str)

                for ((start, end) in listOf(
                    nameStart to nameEnd, contributorsStart to contributorsEnd, githubStart to githubEnd
                )) {
                    addStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline
                        ), start = start, end = end
                    )
                }

                addStringAnnotation(
                    tag = "URL", annotation = "https://depau.eu", start = nameStart, end = nameEnd
                )
                addStringAnnotation(
                    tag = "URL", annotation = "https://github.com/EtchDroid/EtchDroid/graphs/contributors",
                    start = contributorsStart, end = contributorsEnd
                )
                addStringAnnotation(
                    tag = "URL", annotation = "https://github.com/EtchDroid/EtchDroid", start = githubStart,
                    end = githubEnd
                )
            }

            val activity = LocalContext.current.activity
            ClickableText(modifier = Modifier.fillMaxWidth(), text = annotatedText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center
                ), onClick = {
                    annotatedText.getStringAnnotations("URL", it, it).firstOrNull()?.let { stringAnnotation ->
                        activity?.startActivity(
                            Intent(
                                Intent.ACTION_VIEW, Uri.parse(stringAnnotation.item)
                            )
                        )
                    }
                })

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                /* // One day, maybe
                OutlinedButton(
                    onClick = {
                        activity?.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://etchdroid.app")
                            )
                        )
                    }
                ) {
                    Text("Website")
                } */
                OutlinedButton(onClick = {
                    activity?.startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse("https://etchdroid.app/donate")
                        )
                    )
                }) {
                    Text(stringResource(R.string.support_the_project))
                }

                val reviewHelper = remember { activity?.let { WriteReviewHelper(it) } }
                if (reviewHelper != null) {
                    OutlinedButton(onClick = { reviewHelper.launchReviewFlow() }) {
                        Text(
                            text = if (reviewHelper.isGPlayFlavor) stringResource(R.string.write_a_review)
                            else stringResource(R.string.star_on_github)
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, device = Devices.PIXEL_4, showSystemUi = true)
@Composable
fun AboutViewPreview() {
    AboutView()
}