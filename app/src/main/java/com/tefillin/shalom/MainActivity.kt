package com.tefillin.shalom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import kotlinx.coroutines.delay
import kotlin.random.Random

val BG       = Color(0xFF0A0A1A)
val NEON_RED = Color(0xFFFF2D55)
val NEON_BLU = Color(0xFF00E5FF)
val NEON_GRN = Color(0xFF00FF87)
val NEON_YLW = Color(0xFFFFE600)
val NEON_PRP = Color(0xFFBF5AF2)
val WHITE    = Color(0xFFFFFFFF)
val GRAY     = Color(0xFF8E8E93)
val GOLD     = Color(0xFFFFD700)

val COLORS = listOf(NEON_RED, NEON_BLU, NEON_GRN, NEON_YLW, NEON_PRP)

data class Target(
    val id: Int,
    val x: Float,
    val y: Float,
    val size: Float,
    val color: Color,
    val spawnTime: Long,
    val lifetime: Long
)

sealed class Screen {
    object Splash : Screen()
    object Menu : Screen()
    object Play : Screen()
    data class Over(val score: Int, val best: Int) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TapAttackApp() }
    }
}

@Composable
fun TapAttackApp() {
    var screen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var bestScore by remember { mutableStateOf(0) }

    when (val s = screen) {
        Screen.Splash -> SplashScreen { screen = Screen.Menu }
        Screen.Menu   -> MenuScreen { screen = Screen.Play }
        Screen.Play   -> GameScreen(onGameOver = { score ->
            if (score > bestScore) bestScore = score
            screen = Screen.Over(score, bestScore)
        })
        is Screen.Over -> GameOverScreen(s.score, s.best) { screen = Screen.Play }
    }
}

// ===== מסך פתיחה מרהיב =====
@Composable
fun SplashScreen(onDone: () -> Unit) {
    var phase by remember { mutableStateOf(0) }

    // אנימציות
    val logoScale by animateFloatAsState(
        targetValue = if (phase >= 1) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 200f)
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (phase >= 2) 1f else 0f,
        animationSpec = tween(600)
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (phase >= 3) 1f else 0f,
        animationSpec = tween(800)
    )
    val badgeAlpha by animateFloatAsState(
        targetValue = if (phase >= 4) 1f else 0f,
        animationSpec = tween(600)
    )

    val infiniteTransition = rememberInfiniteTransition()
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse)
    )
    val rotate by infiniteTransition.animateFloat(
        initialValue = -3f, targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse)
    )

    LaunchedEffect(Unit) {
        delay(200); phase = 1
        delay(400); phase = 2
        delay(500); phase = 3
        delay(600); phase = 4
        delay(2000); onDone()
    }

    Box(
        Modifier.fillMaxSize().background(BG),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // לוגו ⚡ עם אנימציה
            Text(
                "⚡",
                fontSize = 44.sp,
                modifier = Modifier
                    .scale(logoScale)
                    .graphicsLayer { rotationZ = rotate }
            )

            Spacer(Modifier.height(6.dp))

            // שם המשחק
            Text(
                "TAP ATTACK",
                color = NEON_RED.copy(alpha = titleAlpha * glowPulse),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            // תג הפקה
            Box(
                modifier = Modifier
                    .graphicsLayer { alpha = badgeAlpha }
                    .background(
                        GOLD.copy(alpha = 0.15f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "🏆 נוצר על ידי",
                        color = GOLD.copy(alpha = 0.8f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "מאסטר קוד משחקים",
                        color = GOLD,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "בע\"מ",
                        color = GOLD.copy(alpha = 0.7f),
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // כוכבים
            Text(
                "★★★★★",
                color = NEON_YLW.copy(alpha = subtitleAlpha),
                fontSize = 12.sp
            )
        }
    }
}

// ===== מסך תפריט =====
@Composable
fun MenuScreen(onStart: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse)
    )

    Box(Modifier.fillMaxSize().background(BG), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("⚡", fontSize = 36.sp, modifier = Modifier.scale(pulse))
            Text("TAP", color = NEON_RED, fontSize = 26.sp, fontWeight = FontWeight.Black)
            Text("ATTACK", color = NEON_BLU, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Text("לחץ מהר לפני שנעלמים!", color = GRAY, fontSize = 10.sp)
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = onStart,
                modifier = Modifier.size(width = 110.dp, height = 38.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = NEON_RED)
            ) {
                Text("התחל!", color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ===== מסך משחק =====
@Composable
fun GameScreen(onGameOver: (Int) -> Unit) {
    var score by remember { mutableStateOf(0) }
    var lives by remember { mutableStateOf(3) }
    var targets by remember { mutableStateOf(listOf<Target>()) }
    var nextId by remember { mutableStateOf(0) }
    var level by remember { mutableStateOf(1) }

    val lifetime = maxOf(600L, 1800L - level * 120L)
    val spawnRate = maxOf(400L, 1200L - level * 80L)
    val maxTargets = minOf(2 + level / 2, 5)

    LaunchedEffect(lives) {
        if (lives <= 0) return@LaunchedEffect
        while (lives > 0) {
            delay(spawnRate)
            if (targets.size < maxTargets) {
                targets = targets + Target(
                    id = nextId++,
                    x = Random.nextFloat() * 0.75f + 0.05f,
                    y = Random.nextFloat() * 0.70f + 0.10f,
                    size = Random.nextFloat() * 16f + 24f,
                    color = COLORS.random(),
                    spawnTime = System.currentTimeMillis(),
                    lifetime = lifetime
                )
            }
        }
    }

    LaunchedEffect(lives) {
        if (lives <= 0) return@LaunchedEffect
        while (lives > 0) {
            delay(100)
            val now = System.currentTimeMillis()
            val expired = targets.filter { now - it.spawnTime > it.lifetime }
            if (expired.isNotEmpty()) {
                targets = targets - expired.toSet()
                lives -= expired.size
                if (lives <= 0) { onGameOver(score); break }
            }
        }
    }

    LaunchedEffect(score) { level = 1 + score / 5 }

    BoxWithConstraints(Modifier.fillMaxSize().background(BG)) {
        val w = maxWidth
        val h = maxHeight

        targets.forEach { target ->
            val now = System.currentTimeMillis()
            val progress = (now - target.spawnTime).toFloat() / target.lifetime
            val alpha = 1f - progress * 0.6f

            val inf = rememberInfiniteTransition()
            val pulse by inf.animateFloat(
                initialValue = 1f, targetValue = 1.15f,
                animationSpec = infiniteRepeatable(tween(300), RepeatMode.Reverse)
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = (w * target.x) - (target.size / 2).dp,
                        y = (h * target.y) - (target.size / 2).dp
                    )
                    .size(target.size.dp)
                    .scale(pulse)
                    .clip(CircleShape)
                    .background(target.color.copy(alpha = alpha))
                    .clickable {
                        targets = targets.filter { it.id != target.id }
                        score++
                    }
            )
        }

        Text("$score", modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp),
            color = NEON_YLW, fontSize = 16.sp, fontWeight = FontWeight.Black)

        Row(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(3) { i -> Text(if (i < lives) "❤️" else "🖤", fontSize = 12.sp) }
        }

        Text("LV$level", modifier = Modifier.align(Alignment.TopEnd).padding(top = 4.dp, end = 6.dp),
            color = NEON_PRP, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

// ===== מסך סיום =====
@Composable
fun GameOverScreen(score: Int, best: Int, onRestart: () -> Unit) {
    Box(Modifier.fillMaxSize().background(BG), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("💥", fontSize = 32.sp)
            Text("GAME OVER", color = NEON_RED, fontSize = 16.sp, fontWeight = FontWeight.Black)
            Text("ניקוד: $score", color = NEON_YLW, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("שיא: $best", color = NEON_BLU, fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = onRestart,
                modifier = Modifier.size(width = 110.dp, height = 38.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = NEON_GRN)
            ) {
                Text("שוב!", color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
