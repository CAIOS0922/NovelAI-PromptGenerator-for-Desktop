import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

object TextStyle {
    val UltraSmall = TextStyle(color = ComposeColor.Black, fontSize = 6.sp)
    val HyperSmall = TextStyle(color = ComposeColor.Black, fontSize = 8.sp)
    val SuperSmall = TextStyle(color = ComposeColor.Black, fontSize = 10.sp)
    val ExtraSmall = TextStyle(color = ComposeColor.Black, fontSize = 12.sp)
    val Small = TextStyle(color = ComposeColor.Black, fontSize = 14.sp)
    val Default = TextStyle(color = ComposeColor.Black, fontSize = 16.sp)
    val Large = TextStyle(color = ComposeColor.Black, fontSize = 18.sp)
    val ExtraLarge = TextStyle(color = ComposeColor.Black, fontSize = 20.sp)
    val SuperLarge = TextStyle(color = ComposeColor.Black, fontSize = 26.sp)
    val HyperLarge = TextStyle(color = ComposeColor.Black, fontSize = 32.sp)
    val UltraLarge = TextStyle(color = ComposeColor.Black, fontSize = 38.sp)
}

// Align
fun TextStyle.start() = this.merge(TextStyle(textAlign = TextAlign.Start))
fun TextStyle.center() = this.merge(TextStyle(textAlign = TextAlign.Center))
fun TextStyle.end() = this.merge(TextStyle(textAlign = TextAlign.End))

// Style
fun TextStyle.bold() = this.merge(TextStyle(fontWeight = FontWeight.Bold))
fun TextStyle.extraBold() = this.merge(TextStyle(fontWeight = FontWeight.ExtraBold))
fun TextStyle.italic() = this.merge(TextStyle(fontStyle = FontStyle.Italic))

// Color
@Composable
fun TextStyle.primaryColor() = this.merge(TextStyle(color = ComposeColor.Black))
@Composable
fun TextStyle.secondaryColor() = this.merge(TextStyle(color = ComposeColor.DarkGray))
@Composable
fun TextStyle.nightPrimaryColor() = this.merge(TextStyle(color = ComposeColor.White))