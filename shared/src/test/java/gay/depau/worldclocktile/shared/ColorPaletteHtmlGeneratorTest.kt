package gay.depau.worldclocktile.shared

import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import gay.depau.worldclocktile.shared.utils.dynamicDarkColorPalette
import gay.depau.worldclocktile.shared.utils.dynamicLightColorPalette
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.PrintWriter

@OptIn(ExperimentalStdlibApi::class)
private val ULong.hex: String get() = toHexString().slice(2..7)

@OptIn(ExperimentalStdlibApi::class)
private val Int.hex: String get() = toHexString().slice(2..7)


// This is actually not a test, it's just a hack to be able to run this code locally without
// extracting the dynamic color generation code into a separate module.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O], manifest = Config.NONE)
class ColorPaletteHtmlGeneratorTest {
    @Test
    fun generate() {
        val colors = mapOf(
            "graphite" to 0xFF949494,
            "cloud" to 0xFFE4E4E4,
            "almond" to 0xFFFDF8EB,
            "watermelon" to 0xFFFAF7E8,
            "coral" to 0xFFFF9D85,
            "pomelo" to 0xFFFFAD9D,
            "guava" to 0xFFFDC1B8,
            "peach" to 0xFFFFBB8C,
            "champagne" to 0xFFFFD9B0,
            "chai" to 0xFFDBAC73,
            "sand" to 0xFFC6B371,
            "honey" to 0xFFFFC879,
            "melon" to 0xFFFFD786,
            "wheat" to 0xFFFFE8B7,
            "dandelion" to 0xFFFFF481,
            "limoncello" to 0xFFFCFBB3,
            "lime" to 0xFFE7F577,
            "pear" to 0xFFC3F27C,
            "spearmint" to 0xFF99EAAA,
            "fern" to 0xFF54CA95,
            "forest" to 0xFF72958B,
            "mint" to 0xFFA3F5DE,
            "jade" to 0xFFBBE7DD,
            "sage" to 0xFFCAE1DE,
            "stream" to 0xFFA9DCEC,
            "aqua" to 0xFF86E8FF,
            "sky" to 0xFFC3E8FF,
            "ocean" to 0xFF9FC3FA,
            "sapphire" to 0xFF7DACF8,
            "amethyst" to 0xFFACB7FF,
            "lilac" to 0xFFC8B8EB,
            "lavender" to 0xFFC8B8EB,
            "flamingo" to 0xFFFFC5FF,
            "bubble_gum" to 0xFFFFC5FF,
        )

        val output = PrintWriter("/tmp/colors.html")

        output.println("<!DOCTYPE html>")
        output.println("<html>")
        output.println("<head>")
        output.println("<style>")
        output.println(
            """
            table {
                border-collapse: collapse;
            }
            table, th, td {
                border: 1px solid black;
            }
            .container {
              display: flex;
              flex-direction: row;
              flex-wrap: wrap;
              justify-content: center;
              align-items: normal;
              align-content: normal;
            }
            .color {
              display: block;
              padding: 10px;
              flex-grow: 0;
              flex-shrink: 1;
              flex-basis: auto;
              align-self: auto;
              order: 0;
            }
            """
        )
        output.println("</style>")
        output.println("</head>")
        output.println("<body>")

        output.println("<div class=\"container\">")

        for ((name, color) in colors) {
            val composeColor = Color(color)
            val darkPalette = dynamicDarkColorPalette(composeColor)
            val lightPalette = dynamicLightColorPalette(composeColor)

            output.println("<div class=\"color\">")
            output.println("<h1 style=\"color: #${composeColor.toArgb().hex}\">$name</h1>")
            output.println("<table>")
            output.println("<tr>")
            output.println("<th>Light</th>")
            output.println("<th>Dark</th>")
            output.println("<th>Color purpose</th>")
            output.println("</tr>")

            // primary
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.primary.value.hex}\" style=\"color: #${lightPalette.onPrimary.value.hex}\">${lightPalette.primary.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.primary.value.hex}\" style=\"color: #${darkPalette.onPrimary.value.hex}\">${darkPalette.primary.value.hex}</td>"
            )
            output.println("<td>Primary</td>")
            output.println("</tr>")

            // onPrimary
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.onPrimary.value.hex}\" style=\"color: #${lightPalette.primary.value.hex}\">${lightPalette.onPrimary.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.onPrimary.value.hex}\" style=\"color: #${darkPalette.primary.value.hex}\">${darkPalette.onPrimary.value.hex}</td>"
            )
            output.println("<td>On primary</td>")
            output.println("</tr>")

            // primaryContainer
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.primaryContainer.value.hex}\" style=\"color: #${lightPalette.onPrimaryContainer.value.hex}\">${lightPalette.primaryContainer.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.primaryContainer.value.hex}\" style=\"color: #${darkPalette.onPrimaryContainer.value.hex}\">${darkPalette.primaryContainer.value.hex}</td>"
            )
            output.println("<td>Primary container</td>")
            output.println("</tr>")

            // onPrimaryContainer
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.onPrimaryContainer.value.hex}\" style=\"color: #${lightPalette.primaryContainer.value.hex}\">${lightPalette.onPrimaryContainer.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.onPrimaryContainer.value.hex}\" style=\"color: #${darkPalette.primaryContainer.value.hex}\">${darkPalette.onPrimaryContainer.value.hex}</td>"
            )
            output.println("<td>On primary container</td>")
            output.println("</tr>")

            // inversePrimary
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.inversePrimary.value.hex}\" style=\"color: #${lightPalette.inverseSurface.value.hex}\">${lightPalette.inversePrimary.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.inversePrimary.value.hex}\" style=\"color: #${darkPalette.inverseSurface.value.hex}\">${darkPalette.inversePrimary.value.hex}</td>"
            )
            output.println("<td>Inverse primary</td>")
            output.println("</tr>")

            // secondary
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.secondary.value.hex}\" style=\"color: #${lightPalette.onSecondary.value.hex}\">${lightPalette.secondary.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.secondary.value.hex}\" style=\"color: #${darkPalette.onSecondary.value.hex}\">${darkPalette.secondary.value.hex}</td>"
            )
            output.println("<td>Secondary</td>")
            output.println("</tr>")

            // onSecondary
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.onSecondary.value.hex}\" style=\"color: #${lightPalette.secondary.value.hex}\">${lightPalette.onSecondary.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.onSecondary.value.hex}\" style=\"color: #${darkPalette.secondary.value.hex}\">${darkPalette.onSecondary.value.hex}</td>"
            )
            output.println("<td>On secondary</td>")
            output.println("</tr>")

            // secondaryContainer
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.secondaryContainer.value.hex}\" style=\"color: #${lightPalette.onSecondaryContainer.value.hex}\">${lightPalette.secondaryContainer.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.secondaryContainer.value.hex}\" style=\"color: #${darkPalette.onSecondaryContainer.value.hex}\">${darkPalette.secondaryContainer.value.hex}</td>"
            )
            output.println("<td>Secondary container</td>")
            output.println("</tr>")

            // onSecondaryContainer
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.onSecondaryContainer.value.hex}\" style=\"color: #${lightPalette.secondaryContainer.value.hex}\">${lightPalette.onSecondaryContainer.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.onSecondaryContainer.value.hex}\" style=\"color: #${darkPalette.secondaryContainer.value.hex}\">${darkPalette.onSecondaryContainer.value.hex}</td>"
            )
            output.println("<td>On secondary container</td>")
            output.println("</tr>")

            // tertiary
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.tertiary.value.hex}\" style=\"color: #${lightPalette.onTertiary.value.hex}\">${lightPalette.tertiary.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.tertiary.value.hex}\" style=\"color: #${darkPalette.onTertiary.value.hex}\">${darkPalette.tertiary.value.hex}</td>"
            )
            output.println("<td>Tertiary</td>")
            output.println("</tr>")

            // onTertiary
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.onTertiary.value.hex}\" style=\"color: #${lightPalette.tertiary.value.hex}\">${lightPalette.onTertiary.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.onTertiary.value.hex}\" style=\"color: #${darkPalette.tertiary.value.hex}\">${darkPalette.onTertiary.value.hex}</td>"
            )
            output.println("<td>On tertiary</td>")
            output.println("</tr>")

            // tertiaryContainer
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.tertiaryContainer.value.hex}\" style=\"color: #${lightPalette.onTertiaryContainer.value.hex}\">${lightPalette.tertiaryContainer.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.tertiaryContainer.value.hex}\" style=\"color: #${darkPalette.onTertiaryContainer.value.hex}\">${darkPalette.tertiaryContainer.value.hex}</td>"
            )
            output.println("<td>Tertiary container</td>")
            output.println("</tr>")

            // onTertiaryContainer
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.onTertiaryContainer.value.hex}\" style=\"color: #${lightPalette.tertiaryContainer.value.hex}\">${lightPalette.onTertiaryContainer.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.onTertiaryContainer.value.hex}\" style=\"color: #${darkPalette.tertiaryContainer.value.hex}\">${darkPalette.onTertiaryContainer.value.hex}</td>"
            )
            output.println("<td>On tertiary container</td>")
            output.println("</tr>")

            // background
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.background.value.hex}\" style=\"color: #${lightPalette.onBackground.value.hex}\">${lightPalette.background.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.background.value.hex}\" style=\"color: #${darkPalette.onBackground.value.hex}\">${darkPalette.background.value.hex}</td>"
            )
            output.println("<td>Background</td>")
            output.println("</tr>")

            // onBackground
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.onBackground.value.hex}\" style=\"color: #${lightPalette.background.value.hex}\">${lightPalette.onBackground.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.onBackground.value.hex}\" style=\"color: #${darkPalette.background.value.hex}\">${darkPalette.onBackground.value.hex}</td>"
            )
            output.println("<td>On background</td>")
            output.println("</tr>")

            // surface
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.surface.value.hex}\" style=\"color: #${lightPalette.onSurface.value.hex}\">${lightPalette.surface.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.surface.value.hex}\" style=\"color: #${darkPalette.onSurface.value.hex}\">${darkPalette.surface.value.hex}</td>"
            )
            output.println("<td>Surface</td>")
            output.println("</tr>")

            // onSurface
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.onSurface.value.hex}\" style=\"color: #${lightPalette.surface.value.hex}\">${lightPalette.onSurface.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.onSurface.value.hex}\" style=\"color: #${darkPalette.surface.value.hex}\">${darkPalette.onSurface.value.hex}</td>"
            )
            output.println("<td>On surface</td>")
            output.println("</tr>")

            // surfaceVariant
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.surfaceVariant.value.hex}\" style=\"color: #${lightPalette.onSurfaceVariant.value.hex}\">${lightPalette.surfaceVariant.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.surfaceVariant.value.hex}\" style=\"color: #${darkPalette.onSurfaceVariant.value.hex}\">${darkPalette.surfaceVariant.value.hex}</td>"
            )
            output.println("<td>Surface variant</td>")
            output.println("</tr>")

            // onSurfaceVariant
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.onSurfaceVariant.value.hex}\" style=\"color: #${lightPalette.surfaceVariant.value.hex}\">${lightPalette.onSurfaceVariant.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.onSurfaceVariant.value.hex}\" style=\"color: #${darkPalette.surfaceVariant.value.hex}\">${darkPalette.onSurfaceVariant.value.hex}</td>"
            )
            output.println("<td>On surface variant</td>")
            output.println("</tr>")

            // inverseSurface
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.inverseSurface.value.hex}\" style=\"color: #${lightPalette.inverseOnSurface.value.hex}\">${lightPalette.inverseSurface.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.inverseSurface.value.hex}\" style=\"color: #${darkPalette.inverseOnSurface.value.hex}\">${darkPalette.inverseSurface.value.hex}</td>"
            )
            output.println("<td>Inverse surface</td>")
            output.println("</tr>")

            // inverseOnSurface
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.inverseOnSurface.value.hex}\" style=\"color: #${lightPalette.inverseSurface.value.hex}\">${lightPalette.inverseOnSurface.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.inverseOnSurface.value.hex}\" style=\"color: #${darkPalette.inverseSurface.value.hex}\">${darkPalette.inverseOnSurface.value.hex}</td>"
            )
            output.println("<td>Inverse on surface</td>")
            output.println("</tr>")

            // outline
            output.println("<tr>")
            output.println(
                "<td bgcolor=\"#${lightPalette.outline.value.hex}\" style=\"color: #${lightPalette.onSurfaceVariant.value.hex}\">${lightPalette.outline.value.hex}</td>"
            )
            output.println(
                "<td bgcolor=\"#${darkPalette.outline.value.hex}\" style=\"color: #${darkPalette.onSurfaceVariant.value.hex}\">${darkPalette.outline.value.hex}</td>"
            )
            output.println("<td>Outline</td>")
            output.println("</tr>")

            output.println("</table>")
            output.println("</div>")
        }

        output.println("</div>")
        output.println("</body>")
        output.println("</html>")

        output.close()
    }
}