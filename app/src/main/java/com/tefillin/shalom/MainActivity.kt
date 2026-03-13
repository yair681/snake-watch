package com.tefillin.shalom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import kotlinx.coroutines.*
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

val BG          = Color(0xFF0A0A1A)
val NEON_RED    = Color(0xFFFF2D55)
val NEON_BLU    = Color(0xFF00E5FF)
val NEON_GRN    = Color(0xFF00FF87)
val NEON_YLW    = Color(0xFFFFE600)
val NEON_PRP    = Color(0xFFBF5AF2)
val NEON_ORG    = Color(0xFFFF9500)
val GOLD        = Color(0xFFFFD700)
val CARD_BG     = Color(0xFF16162A)
val GRAY        = Color(0xFF8E8E93)
val NEON_COLORS = listOf(NEON_RED, NEON_BLU, NEON_GRN, NEON_YLW, NEON_PRP, NEON_ORG)

sealed class Screen {
    object Splash   : Screen()
    object Menu     : Screen()
    object Snake    : Screen()
    object TapAtk   : Screen()
    object Brick    : Screen()
    object Memory   : Screen()
    object Reaction : Screen()
    object Flappy   : Screen()
    object Space    : Screen()
    object Dodge    : Screen()
}

data class GameInfo(val emoji: String, val name: String, val color: Color, val screen: Screen)

val GAMES = listOf(
    GameInfo("S", "Snake",         NEON_GRN, Screen.Snake),
    GameInfo("T", "Tap Attack",    NEON_RED, Screen.TapAtk),
    GameInfo("B", "Brick",         NEON_BLU, Screen.Brick),
    GameInfo("M", "Memory",        NEON_PRP, Screen.Memory),
    GameInfo("R", "Reaction",      NEON_ORG, Screen.Reaction),
    GameInfo("F", "Flappy Bird",   NEON_YLW, Screen.Flappy),
    GameInfo("X", "Space Shooter", NEON_BLU, Screen.Space),
    GameInfo("D", "Dodge",         NEON_RED, Screen.Dodge),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GameHubApp() }
    }
}

@Composable
fun GameHubApp() {
    var screen by remember { mutableStateOf<Screen>(Screen.Splash) }
    when (screen) {
        Screen.Splash   -> SplashScreen   { screen = Screen.Menu }
        Screen.Menu     -> MenuScreen     { screen = it }
        Screen.Snake    -> SnakeGame      { screen = Screen.Menu }
        Screen.TapAtk   -> TapAttackGame  { screen = Screen.Menu }
        Screen.Brick    -> BrickGame      { screen = Screen.Menu }
        Screen.Memory   -> MemoryGame     { screen = Screen.Menu }
        Screen.Reaction -> ReactionGame   { screen = Screen.Menu }
        Screen.Flappy   -> FlappyGame     { screen = Screen.Menu }
        Screen.Space    -> SpaceGame      { screen = Screen.Menu }
        Screen.Dodge    -> DodgeGame      { screen = Screen.Menu }
    }
}

// ==================== SPLASH ====================
@Composable
fun SplashScreen(onDone: () -> Unit) {
    var phase by remember { mutableStateOf(0) }
    val logoScale  by animateFloatAsState(if (phase >= 1) 1f else 0f, spring(0.4f, 200f))
    val titleAlpha by animateFloatAsState(if (phase >= 2) 1f else 0f, tween(600))
    val badgeAlpha by animateFloatAsState(if (phase >= 3) 1f else 0f, tween(700))
    val starsAlpha by animateFloatAsState(if (phase >= 4) 1f else 0f, tween(500))
    val inf = rememberInfiniteTransition()
    val pulse by inf.animateFloat(0.85f, 1f,   infiniteRepeatable(tween(700),  RepeatMode.Reverse))
    val rot   by inf.animateFloat(-4f,   4f,   infiniteRepeatable(tween(1000), RepeatMode.Reverse))

    LaunchedEffect(Unit) {
        delay(200); phase = 1
        delay(400); phase = 2
        delay(500); phase = 3
        delay(600); phase = 4
        delay(2200); onDone()
    }

    Box(Modifier.fillMaxSize().background(BG), Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("* GAMES *", color = NEON_GRN, fontSize = 14.sp, fontWeight = FontWeight.Black,
                modifier = Modifier.scale(logoScale).graphicsLayer { rotationZ = rot })
            Text("MASTER", color = NEON_BLU.copy(alpha = titleAlpha * pulse),
                fontSize = 20.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
            Text("GAMES HUB", color = NEON_RED.copy(alpha = titleAlpha * pulse),
                fontSize = 14.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(4.dp))
            Box(Modifier.graphicsLayer { alpha = badgeAlpha }
                .background(GOLD.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("~ mastercod gamez ~", color = GOLD.copy(alpha = 0.8f), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text("מאסטר קוד משחקים", color = GOLD, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    Text("בע\"מ", color = GOLD.copy(alpha = 0.7f), fontSize = 8.sp)
                }
            }
            Text("* * * * *", color = NEON_YLW.copy(alpha = starsAlpha), fontSize = 12.sp)
        }
    }
}

// ==================== MENU ====================
@Composable
fun MenuScreen(onSelect: (Screen) -> Unit) {
    val inf = rememberInfiniteTransition()
    val pulse by inf.animateFloat(0.97f, 1.03f, infiniteRepeatable(tween(900), RepeatMode.Reverse))

    Box(Modifier.fillMaxSize().background(BG)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
            contentPadding = PaddingValues(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            item {
                Text("GAME HUB", color = NEON_BLU, fontSize = 13.sp, fontWeight = FontWeight.Black,
                    modifier = Modifier.fillMaxWidth().scale(pulse), textAlign = TextAlign.Center)
            }
            items(GAMES) { game ->
                Box(modifier = Modifier.fillMaxWidth()
                    .background(CARD_BG, RoundedCornerShape(12.dp))
                    .clickable { onSelect(game.screen) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.size(34.dp).background(game.color.copy(alpha = 0.18f), CircleShape),
                            contentAlignment = Alignment.Center) {
                            Text(game.emoji, color = game.color, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }
                        Text(game.name, color = game.color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            item {
                Text("מאסטר קוד משחקים בע\"מ", color = GRAY, fontSize = 9.sp,
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

// ==================== FLAPPY BIRD ====================
@Composable
fun FlappyGame(onBack: () -> Unit) {
    data class Pipe(val x: Float, val gapY: Float)

    var birdY   by remember { mutableStateOf(0.5f) }
    var velY    by remember { mutableStateOf(0f) }
    var started by remember { mutableStateOf(false) }
    var dead    by remember { mutableStateOf(false) }
    var score   by remember { mutableStateOf(0) }
    var pipes   by remember { mutableStateOf(listOf(Pipe(1.3f, 0.45f))) }

    val birdX = 0.2f
    val gapH  = 0.30f
    val pipeW = 0.10f

    fun reset() {
        dead = false; started = false; score = 0
        birdY = 0.5f; velY = 0f
        pipes = listOf(Pipe(1.3f, 0.45f))
    }

    LaunchedEffect(started) {
        if (!started) return@LaunchedEffect
        while (!dead) {
            delay(16)
            velY = (velY + 0.0020f).coerceAtMost(0.04f)
            birdY = (birdY + velY).coerceIn(0.04f, 0.96f)

            pipes = pipes.map { it.copy(x = it.x - 0.011f) }
            if (pipes.last().x < 0.55f)
                pipes = pipes + Pipe(1.3f, Random.nextFloat() * 0.38f + 0.22f)
            pipes = pipes.filter { it.x > -0.15f }

            // score
            pipes.forEach { p ->
                if (p.x < birdX + 0.01f && p.x > birdX - 0.01f) score++
            }

            // collision: walls
            if (birdY >= 0.95f || birdY <= 0.05f) { dead = true; break }

            // collision: pipes
            pipes.forEach { p ->
                if (abs(birdX - p.x) < pipeW / 2f + 0.035f) {
                    if (birdY < p.gapY - gapH / 2f || birdY > p.gapY + gapH / 2f) {
                        dead = true
                    }
                }
            }
        }
    }

    if (dead) {
        GameOverScreen("F", "Flappy", score, NEON_YLW, onBack, ::reset)
        return
    }

    Box(
        Modifier.fillMaxSize().background(Color(0xFF071B2F))
            .pointerInput(Unit) {
                detectTapGestures {
                    if (!started) started = true
                    velY = -0.024f
                }
            }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = size.width; val sh = size.height

            // pipes
            pipes.forEach { p ->
                val px   = p.x * sw
                val topH = (p.gapY - gapH / 2f) * sh
                val botY = (p.gapY + gapH / 2f) * sh
                // top pipe
                drawRoundRect(NEON_GRN,
                    Offset(px - pipeW / 2f * sw, 0f),
                    Size(pipeW * sw, topH),
                    CornerRadius(6f))
                drawRoundRect(NEON_GRN.copy(alpha = 0.4f),
                    Offset(px - pipeW / 2f * sw - 4f, topH - 14f),
                    Size(pipeW * sw + 8f, 14f),
                    CornerRadius(4f))
                // bottom pipe
                drawRoundRect(NEON_GRN,
                    Offset(px - pipeW / 2f * sw, botY),
                    Size(pipeW * sw, sh - botY),
                    CornerRadius(6f))
                drawRoundRect(NEON_GRN.copy(alpha = 0.4f),
                    Offset(px - pipeW / 2f * sw - 4f, botY),
                    Size(pipeW * sw + 8f, 14f),
                    CornerRadius(4f))
            }

            // bird
            val bx = birdX * sw
            val by2 = birdY * sh
            drawCircle(NEON_YLW, 13f, Offset(bx, by2))
            drawCircle(Color(0xFFFFAA00), 13f, Offset(bx, by2), style = androidx.compose.ui.graphics.drawscope.Stroke(3f))
            drawCircle(Color.Black, 4f, Offset(bx + 5f, by2 - 3f))
            // wing
            val path = Path().apply {
                moveTo(bx - 5f, by2)
                lineTo(bx - 14f, by2 - 8f)
                lineTo(bx - 2f, by2 + 4f)
                close()
            }
            drawPath(path, Color(0xFFFFAA00))

            // ground
            drawRect(NEON_GRN.copy(alpha = 0.25f), Offset(0f, sh * 0.95f), Size(sw, sh * 0.05f))
        }

        Text(score.toString(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 6.dp),
            color = NEON_YLW, fontSize = 22.sp, fontWeight = FontWeight.Black)

        if (!started) {
            Box(Modifier.align(Alignment.Center)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)) {
                Text("TAP TO FLY", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
        }

        Text("< back", Modifier.align(Alignment.TopStart).padding(4.dp).clickable { onBack() }, GRAY, 9.sp)
    }
}

// ==================== SPACE SHOOTER ====================
data class Bullet(val id: Int, val x: Float, val y: Float)
data class Asteroid(val id: Int, val x: Float, val y: Float, val radius: Float, val color: Color)
data class Explosion(val x: Float, val y: Float, val born: Long)

@Composable
fun SpaceGame(onBack: () -> Unit) {
    var shipX      by remember { mutableStateOf(0.5f) }
    var score      by remember { mutableStateOf(0) }
    var lives      by remember { mutableStateOf(3) }
    var dead       by remember { mutableStateOf(false) }
    var bullets    by remember { mutableStateOf(listOf<Bullet>()) }
    var asteroids  by remember { mutableStateOf(listOf<Asteroid>()) }
    var explosions by remember { mutableStateOf(listOf<Explosion>()) }
    var bulletId   by remember { mutableStateOf(0) }
    var asteroidId by remember { mutableStateOf(0) }
    var shootTimer by remember { mutableStateOf(0) }
    var frame      by remember { mutableStateOf(0) }

    fun reset() {
        dead = false; score = 0; lives = 3; frame = 0; shootTimer = 0
        bullets = listOf(); asteroids = listOf(); explosions = listOf()
        shipX = 0.5f
    }

    LaunchedEffect(Unit) {
        while (!dead) {
            delay(30)
            frame++

            // auto-shoot
            shootTimer++
            if (shootTimer >= 18) {
                shootTimer = 0
                bullets = bullets + Bullet(bulletId++, shipX, 0.83f)
            }

            // move bullets
            bullets = bullets.map { it.copy(y = it.y - 0.055f) }.filter { it.y > 0f }

            // spawn asteroids
            val spawnChance = if (frame < 100) 0.03f else if (frame < 300) 0.05f else 0.07f
            if (Random.nextFloat() < spawnChance) {
                asteroids = asteroids + Asteroid(
                    id     = asteroidId++,
                    x      = Random.nextFloat() * 0.8f + 0.1f,
                    y      = -0.06f,
                    radius = Random.nextFloat() * 0.055f + 0.035f,
                    color  = NEON_COLORS.random()
                )
            }

            // move asteroids
            asteroids = asteroids.map { it.copy(y = it.y + 0.018f) }

            // bullet hits asteroid
            val hitBullets   = mutableSetOf<Int>()
            val hitAsteroids = mutableSetOf<Int>()
            bullets.forEach { b ->
                asteroids.forEach { a ->
                    val dx = b.x - a.x; val dy = b.y - a.y
                    if (sqrt(dx * dx + dy * dy) < a.radius + 0.03f) {
                        hitBullets.add(b.id); hitAsteroids.add(a.id)
                        score += 10
                        explosions = explosions + Explosion(a.x, a.y, System.currentTimeMillis())
                    }
                }
            }
            bullets   = bullets.filter { it.id !in hitBullets }
            asteroids = asteroids.filter { it.id !in hitAsteroids }

            // asteroid hits ship
            val shipHit = asteroids.filter { a ->
                val dx = a.x - shipX; val dy = a.y - 0.87f
                sqrt(dx * dx + dy * dy) < a.radius + 0.07f
            }
            if (shipHit.isNotEmpty()) {
                asteroids = asteroids.filter { it.id !in shipHit.map { h -> h.id }.toSet() }
                lives -= 1
                if (lives <= 0) { dead = true; break }
            }

            asteroids  = asteroids.filter { it.y < 1.1f }
            val nowMs2 = System.currentTimeMillis()
            explosions = explosions.filter { nowMs2 - it.born < 350 }
        }
    }

    if (dead) {
        GameOverScreen("X", "Space", score, NEON_BLU, onBack, ::reset)
        return
    }

    Box(
        Modifier.fillMaxSize().background(BG)
            .pointerInput(Unit) {
                detectDragGestures { _, d ->
                    shipX = (shipX + d.x / size.width).coerceIn(0.10f, 0.90f)
                }
            }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = size.width; val sh = size.height

            // stars
            repeat(25) { i ->
                val sx = (i * 137.5f) % sw
                val sy = (i * 79.3f + frame * 0.4f) % sh
                drawCircle(Color.White.copy(alpha = 0.25f + (i % 3) * 0.1f), 1.5f, Offset(sx, sy))
            }

            // bullets
            bullets.forEach { b ->
                drawRect(NEON_YLW, Offset(b.x * sw - 2f, b.y * sh - 10f), Size(4f, 12f))
                drawRect(NEON_YLW.copy(alpha = 0.3f), Offset(b.x * sw - 3f, b.y * sh - 10f), Size(6f, 14f))
            }

            // asteroids
            asteroids.forEach { a ->
                drawCircle(a.color, a.radius * sw, Offset(a.x * sw, a.y * sh))
                drawCircle(a.color.copy(alpha = 0.25f), a.radius * sw + 5f, Offset(a.x * sw, a.y * sh))
            }

            // explosions
            val nowMs = System.currentTimeMillis()
            explosions.forEach { ex ->
                val prog = (nowMs - ex.born) / 350f
                drawCircle(NEON_ORG.copy(alpha = (1f - prog) * 0.9f),
                    (18f + prog * 28f), Offset(ex.x * sw, ex.y * sh))
                drawCircle(NEON_YLW.copy(alpha = (1f - prog) * 0.6f),
                    (10f + prog * 14f), Offset(ex.x * sw, ex.y * sh))
            }

            // ship
            val sx = shipX * sw; val sy = 0.87f * sh
            val shipPath = Path().apply {
                moveTo(sx, sy - 20f)
                lineTo(sx - 13f, sy + 12f)
                lineTo(sx, sy + 6f)
                lineTo(sx + 13f, sy + 12f)
                close()
            }
            drawPath(shipPath, NEON_BLU)
            drawPath(shipPath, NEON_BLU.copy(alpha = 0.35f))
            // thruster flame
            val flamePath = Path().apply {
                moveTo(sx - 6f, sy + 6f)
                lineTo(sx, sy + 20f)
                lineTo(sx + 6f, sy + 6f)
                close()
            }
            drawPath(flamePath, NEON_ORG.copy(alpha = 0.8f))
        }

        // score
        Text(score.toString(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp),
            color = NEON_YLW, fontSize = 18.sp, fontWeight = FontWeight.Black)

        // lives
        Row(modifier = Modifier.align(Alignment.TopEnd).padding(top = 4.dp, end = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            repeat(3) { i ->
                Text(if (i < lives) "▲" else "·",
                    color = if (i < lives) NEON_BLU else GRAY, fontSize = 11.sp)
            }
        }

        Text("drag ship", modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp),
            color = GRAY, fontSize = 9.sp)
        Text("< back", Modifier.align(Alignment.TopStart).padding(4.dp).clickable { onBack() }, GRAY, 9.sp)
    }
}

// ==================== DODGE ====================
data class DodgeBall(val id: Int, val x: Float, val y: Float, val vx: Float, val vy: Float, val color: Color, val radius: Float)

@Composable
fun DodgeGame(onBack: () -> Unit) {
    var playerX by remember { mutableStateOf(0.5f) }
    var playerY by remember { mutableStateOf(0.72f) }
    var balls   by remember { mutableStateOf(listOf<DodgeBall>()) }
    var score   by remember { mutableStateOf(0) }
    var dead    by remember { mutableStateOf(false) }
    var ballId  by remember { mutableStateOf(0) }
    var frame   by remember { mutableStateOf(0) }

    fun reset() {
        dead = false; score = 0; frame = 0
        balls = listOf(); playerX = 0.5f; playerY = 0.72f
    }

    LaunchedEffect(Unit) {
        while (!dead) {
            delay(18)
            frame++
            score = frame / 12

            // spawn — gets faster over time
            val spawnEvery = maxOf(18, 55 - frame / 40)
            if (frame % spawnEvery == 0) {
                val speed = 0.012f + frame / 8000f
                balls = balls + DodgeBall(
                    id     = ballId++,
                    x      = Random.nextFloat() * 0.8f + 0.1f,
                    y      = -0.05f,
                    vx     = (Random.nextFloat() - 0.5f) * 0.010f,
                    vy     = speed + Random.nextFloat() * 0.008f,
                    color  = NEON_COLORS.random(),
                    radius = Random.nextFloat() * 0.03f + 0.03f
                )
            }

            // move
            balls = balls.map {
                var nx = it.x + it.vx; var nvx = it.vx
                if (nx < 0.04f || nx > 0.96f) { nvx = -nvx; nx = it.x + nvx }
                it.copy(x = nx, y = it.y + it.vy, vx = nvx)
            }.filter { it.y < 1.12f }

            // hit player
            val hit = balls.any { b ->
                val dx = b.x - playerX; val dy = b.y - playerY
                sqrt(dx * dx + dy * dy) < b.radius + 0.07f
            }
            if (hit) { dead = true; break }
        }
    }

    if (dead) {
        GameOverScreen("D", "Dodge", score, NEON_RED, onBack, ::reset)
        return
    }

    Box(
        Modifier.fillMaxSize().background(BG)
            .pointerInput(Unit) {
                detectDragGestures { _, d ->
                    playerX = (playerX + d.x / size.width).coerceIn(0.07f, 0.93f)
                    playerY = (playerY + d.y / size.height).coerceIn(0.07f, 0.93f)
                }
            }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = size.width; val sh = size.height

            // subtle grid
            for (i in 0..10) {
                drawLine(NEON_BLU.copy(alpha = 0.06f),
                    Offset(i * sw / 10f, 0f), Offset(i * sw / 10f, sh), 1f)
                drawLine(NEON_BLU.copy(alpha = 0.06f),
                    Offset(0f, i * sh / 10f), Offset(sw, i * sh / 10f), 1f)
            }

            // balls
            balls.forEach { b ->
                drawCircle(b.color, b.radius * sw, Offset(b.x * sw, b.y * sh))
                drawCircle(b.color.copy(alpha = 0.3f), b.radius * sw + 5f, Offset(b.x * sw, b.y * sh))
            }

            // player — glowing diamond
            val px = playerX * sw; val py = playerY * sh; val r = 15f
            val diamond = Path().apply {
                moveTo(px, py - r); lineTo(px + r, py)
                lineTo(px, py + r); lineTo(px - r, py); close()
            }
            drawPath(diamond, NEON_GRN)
            drawCircle(NEON_GRN.copy(alpha = 0.25f), r + 7f, Offset(px, py))
        }

        Text(score.toString(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp),
            color = NEON_YLW, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Text("drag to dodge",
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp),
            color = GRAY, fontSize = 9.sp)
        Text("< back", Modifier.align(Alignment.TopStart).padding(4.dp).clickable { onBack() }, GRAY, 9.sp)
    }
}

// ==================== SNAKE ====================
data class Pt(val x: Int, val y: Int)
enum class Dir { UP, DOWN, LEFT, RIGHT }
const val COLS = 14
const val ROWS = 14

@Composable
fun SnakeGame(onBack: () -> Unit) {
    var snake   by remember { mutableStateOf(listOf(Pt(7, 7), Pt(6, 7), Pt(5, 7))) }
    var dir     by remember { mutableStateOf(Dir.RIGHT) }
    var nextDir by remember { mutableStateOf(Dir.RIGHT) }
    var food    by remember { mutableStateOf(Pt(10, 7)) }
    var score   by remember { mutableStateOf(0) }
    var alive   by remember { mutableStateOf(true) }
    var dead    by remember { mutableStateOf(false) }

    fun spawnFood(s: List<Pt>): Pt {
        var p: Pt
        do { p = Pt(Random.nextInt(COLS), Random.nextInt(ROWS)) } while (s.contains(p))
        return p
    }

    LaunchedEffect(Unit) {
        while (alive) {
            delay(200L)
            dir = nextDir
            val h = snake.first()
            val nh = when (dir) {
                Dir.UP    -> Pt(h.x, (h.y - 1 + ROWS) % ROWS)
                Dir.DOWN  -> Pt(h.x, (h.y + 1) % ROWS)
                Dir.LEFT  -> Pt((h.x - 1 + COLS) % COLS, h.y)
                Dir.RIGHT -> Pt((h.x + 1) % COLS, h.y)
            }
            if (snake.contains(nh)) { alive = false; dead = true; break }
            val ate = nh == food
            snake = if (ate) listOf(nh) + snake else listOf(nh) + snake.dropLast(1)
            if (ate) { score += 10; food = spawnFood(snake) }
        }
    }

    if (dead) {
        GameOverScreen("S", "Snake", score, NEON_GRN, onBack) {
            dead = false; alive = true; score = 0
            snake = listOf(Pt(7, 7), Pt(6, 7), Pt(5, 7))
            dir = Dir.RIGHT; nextDir = Dir.RIGHT; food = Pt(10, 7)
        }
        return
    }

    Box(Modifier.fillMaxSize().background(BG)
        .pointerInput(Unit) {
            detectDragGestures { _, d ->
                val dx = d.x; val dy = d.y
                if (abs(dx) > abs(dy)) {
                    if (dx > 0 && dir != Dir.LEFT)  nextDir = Dir.RIGHT
                    if (dx < 0 && dir != Dir.RIGHT) nextDir = Dir.LEFT
                } else {
                    if (dy > 0 && dir != Dir.UP)   nextDir = Dir.DOWN
                    if (dy < 0 && dir != Dir.DOWN) nextDir = Dir.UP
                }
            }
        }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val cw = size.width / COLS; val ch = size.height / ROWS
            for (r in 0 until ROWS) for (c in 0 until COLS) {
                drawRect(if ((r + c) % 2 == 0) Color(0xFF1A1A2E) else BG, Offset(c * cw, r * ch), Size(cw, ch))
            }
            drawCircle(NEON_RED, cw * 0.4f, Offset(food.x * cw + cw / 2, food.y * ch + ch / 2))
            snake.forEachIndexed { i, p ->
                val col = if (i == 0) NEON_GRN else Color(0xFF00897B)
                val pad = if (i == 0) 1.5f else 2.5f
                drawRoundRect(col, Offset(p.x * cw + pad, p.y * ch + pad), Size(cw - pad * 2, ch - pad * 2), CornerRadius(4f))
            }
        }
        Text(score.toString(), modifier = Modifier.align(Alignment.TopCenter).padding(top = 3.dp),
            color = NEON_YLW, fontSize = 13.sp, fontWeight = FontWeight.Black)
        Text("< back", Modifier.align(Alignment.TopStart).padding(3.dp).clickable { onBack() }, GRAY, 9.sp)
    }
}

// ==================== TAP ATTACK ====================
data class TapTarget(val id: Int, val x: Float, val y: Float, val size: Float, val color: Color, val born: Long, val life: Long)

@Composable
fun TapAttackGame(onBack: () -> Unit) {
    var score   by remember { mutableStateOf(0) }
    var lives   by remember { mutableStateOf(3) }
    var targets by remember { mutableStateOf(listOf<TapTarget>()) }
    var nid     by remember { mutableStateOf(0) }
    var level   by remember { mutableStateOf(1) }
    var dead    by remember { mutableStateOf(false) }
    val life    = maxOf(600L, 1800L - level * 120L)
    val rate    = maxOf(400L, 1200L - level * 80L)
    val maxT    = minOf(2 + level / 2, 5)

    LaunchedEffect(lives) {
        if (lives <= 0) return@LaunchedEffect
        while (lives > 0) {
            delay(rate)
            if (targets.size < maxT) {
                targets = targets + TapTarget(nid++,
                    Random.nextFloat() * 0.75f + 0.05f, Random.nextFloat() * 0.65f + 0.12f,
                    Random.nextFloat() * 16f + 24f, NEON_COLORS.random(), System.currentTimeMillis(), life)
            }
        }
    }
    LaunchedEffect(lives) {
        if (lives <= 0) return@LaunchedEffect
        while (lives > 0) {
            delay(100)
            val now = System.currentTimeMillis()
            val exp = targets.filter { now - it.born > it.life }
            if (exp.isNotEmpty()) {
                targets = targets - exp.toSet(); lives -= exp.size
                if (lives <= 0) { dead = true; break }
            }
        }
    }
    LaunchedEffect(score) { level = 1 + score / 5 }

    if (dead) {
        GameOverScreen("T", "Tap Attack", score, NEON_RED, onBack) {
            dead = false; lives = 3; score = 0; targets = listOf(); level = 1
        }
        return
    }

    BoxWithConstraints(Modifier.fillMaxSize().background(BG)) {
        val w = maxWidth; val h = maxHeight
        targets.forEach { t ->
            val prog = (System.currentTimeMillis() - t.born).toFloat() / t.life
            val inf = rememberInfiniteTransition()
            val pulse by inf.animateFloat(1f, 1.15f, infiniteRepeatable(tween(300), RepeatMode.Reverse))
            Box(Modifier.offset(x = (w * t.x) - (t.size / 2).dp, y = (h * t.y) - (t.size / 2).dp)
                .size(t.size.dp).scale(pulse).clip(CircleShape)
                .background(t.color.copy(alpha = 1f - prog * 0.6f))
                .clickable { targets = targets.filter { it.id != t.id }; score++ })
        }
        Text(score.toString(), Modifier.align(Alignment.TopCenter).padding(top = 3.dp), NEON_YLW, 15.sp, fontWeight = FontWeight.Black)
        Text("LV$level", Modifier.align(Alignment.TopEnd).padding(top = 3.dp, end = 5.dp), NEON_PRP, 9.sp, fontWeight = FontWeight.Bold)
        Row(Modifier.align(Alignment.BottomCenter).padding(bottom = 5.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            repeat(3) { i -> Text(if (i < lives) "v" else "x", color = if (i < lives) NEON_RED else GRAY, fontSize = 11.sp, fontWeight = FontWeight.Black) }
        }
        Text("< back", Modifier.align(Alignment.TopStart).padding(3.dp).clickable { onBack() }, GRAY, 9.sp)
    }
}

// ==================== BRICK BREAKER ====================
@Composable
fun BrickGame(onBack: () -> Unit) {
    val bCols = 6; val bRows = 4
    val initBricks = { (0 until bRows * bCols).map { it to true } }
    var bricks by remember { mutableStateOf(initBricks()) }
    var ballX  by remember { mutableStateOf(0.5f) }
    var ballY  by remember { mutableStateOf(0.7f) }
    var velX   by remember { mutableStateOf(0.018f) }
    var velY   by remember { mutableStateOf(-0.022f) }
    var padX   by remember { mutableStateOf(0.5f) }
    var score  by remember { mutableStateOf(0) }
    var alive  by remember { mutableStateOf(true) }
    var dead   by remember { mutableStateOf(false) }
    var won    by remember { mutableStateOf(false) }
    val brickColors = listOf(NEON_RED, NEON_ORG, NEON_YLW, NEON_GRN)

    LaunchedEffect(Unit) {
        while (alive) {
            delay(16)
            ballX += velX; ballY += velY
            if (ballX < 0.05f || ballX > 0.95f) velX = -velX
            if (ballY < 0.05f) velY = -velY
            if (ballY > 0.82f && ballY < 0.88f && abs(ballX - padX) < 0.15f) velY = -abs(velY)
            if (ballY > 0.95f) { alive = false; dead = true; break }
            val bw = 1f / bCols; val bh = 0.12f
            bricks = bricks.map { (idx, isAlive) ->
                if (!isAlive) idx to false
                else {
                    val bx = (idx % bCols) * bw + bw / 2; val by2 = 0.08f + (idx / bCols) * bh + bh / 2
                    if (abs(ballX - bx) < bw / 2 && abs(ballY - by2) < bh / 2) { velY = -velY; score += 10; idx to false }
                    else idx to isAlive
                }
            }
            if (bricks.all { !it.second }) { won = true; alive = false }
        }
    }

    if (dead || won) {
        GameOverScreen(if (won) "W" else "B", if (won) "Win!" else "Brick", score, NEON_BLU, onBack) {
            dead = false; won = false; alive = true; score = 0
            ballX = 0.5f; ballY = 0.7f; velX = 0.018f; velY = -0.022f; padX = 0.5f; bricks = initBricks()
        }
        return
    }

    BoxWithConstraints(Modifier.fillMaxSize().background(BG).pointerInput(Unit) {
        detectDragGestures { _, d -> padX = (padX + d.x / 800f).coerceIn(0.15f, 0.85f) }
    }) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = size.width; val sh = size.height
            val bw = 1f / bCols; val bh = 0.12f
            bricks.forEach { (idx, isAlive) ->
                if (isAlive) {
                    val col = brickColors[(idx / bCols) % brickColors.size]
                    drawRoundRect(col, Offset((idx % bCols) * bw * sw + 2f, 0.08f * sh + (idx / bCols) * bh * sh + 2f),
                        Size(bw * sw - 4f, bh * sh - 4f), CornerRadius(4f))
                }
            }
            drawCircle(NEON_YLW, 8f, Offset(ballX * sw, ballY * sh))
            drawRoundRect(Color.White, Offset((padX - 0.14f) * sw, 0.84f * sh), Size(0.28f * sw, 10f), CornerRadius(5f))
        }
        Text(score.toString(), Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp), NEON_YLW, 12.sp, fontWeight = FontWeight.Black)
        Text("< back", Modifier.align(Alignment.TopStart).padding(3.dp).clickable { onBack() }, GRAY, 9.sp)
    }
}

// ==================== MEMORY ====================
@Composable
fun MemoryGame(onBack: () -> Unit) {
    val symbols = listOf("A","B","C","D","E","F","G","H")
    val symColors = listOf(NEON_RED, NEON_BLU, NEON_GRN, NEON_YLW, NEON_PRP, NEON_ORG, GOLD, NEON_GRN)
    val pairs = remember { (symbols + symbols).shuffled() }
    var flipped by remember { mutableStateOf(listOf<Int>()) }
    var matched by remember { mutableStateOf(setOf<Int>()) }
    var moves   by remember { mutableStateOf(0) }
    var won     by remember { mutableStateOf(false) }
    var canFlip by remember { mutableStateOf(true) }

    fun flip(idx: Int) {
        if (!canFlip || flipped.contains(idx) || matched.contains(idx)) return
        val nf = flipped + idx; flipped = nf
        if (nf.size == 2) {
            canFlip = false; moves++
            if (pairs[nf[0]] == pairs[nf[1]]) {
                matched = matched + nf[0] + nf[1]; flipped = listOf(); canFlip = true
                if (matched.size == pairs.size) won = true
            }
        }
    }

    LaunchedEffect(flipped) {
        if (flipped.size == 2 && !matched.containsAll(flipped)) { delay(700); flipped = listOf(); canFlip = true }
    }

    if (won) {
        GameOverScreen("M", "Memory", moves, NEON_PRP, onBack) {
            won = false; moves = 0; flipped = listOf(); matched = setOf(); canFlip = true
        }
        return
    }

    Box(Modifier.fillMaxSize().background(BG)) {
        Column(Modifier.fillMaxSize().padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("< back", Modifier.clickable { onBack() }, GRAY, 9.sp)
                Text("MEMORY", color = NEON_PRP, fontSize = 11.sp, fontWeight = FontWeight.Black)
                Text(moves.toString(), color = NEON_YLW, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            for (row in 0 until 4) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (col in 0 until 4) {
                        val idx = row * 4 + col
                        val show = flipped.contains(idx) || matched.contains(idx)
                        val symColor = symColors.getOrElse(symbols.indexOf(pairs[idx])) { NEON_BLU }
                        Box(Modifier.size(32.dp).background(
                            if (matched.contains(idx)) symColor.copy(alpha = 0.25f)
                            else if (flipped.contains(idx)) CARD_BG else Color(0xFF1E1E3A), RoundedCornerShape(6.dp)
                        ).clickable { flip(idx) }, Alignment.Center) {
                            if (show) Text(pairs[idx], color = symColor, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

// ==================== REACTION TIME ====================
@Composable
fun ReactionGame(onBack: () -> Unit) {
    var state    by remember { mutableStateOf("wait") }
    var startMs  by remember { mutableStateOf(0L) }
    var resultMs by remember { mutableStateOf(0L) }
    var best     by remember { mutableStateOf(9999L) }
    var attempts by remember { mutableStateOf(0) }

    LaunchedEffect(state) {
        if (state == "ready") {
            delay(Random.nextLong(1500, 4000))
            if (state == "ready") { startMs = System.currentTimeMillis(); state = "go" }
        }
    }

    Box(Modifier.fillMaxSize()
        .background(when (state) { "ready" -> Color(0xFF1A0A0A); "go" -> Color(0xFF0A1A0A); else -> BG })
        .clickable {
            when (state) {
                "wait"   -> state = "ready"
                "ready"  -> state = "wait"
                "go"     -> { val ms = System.currentTimeMillis() - startMs; resultMs = ms; if (ms < best) best = ms; attempts++; state = "result" }
                "result" -> state = "ready"
            }
        }, contentAlignment = Alignment.Center
    ) {
        when (state) {
            "wait" -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("[ ]", color = NEON_ORG, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text("REACTION", color = NEON_ORG, fontSize = 16.sp, fontWeight = FontWeight.Black)
                Text("TIME", color = NEON_YLW, fontSize = 14.sp, fontWeight = FontWeight.Black)
                Text("tap to start", color = GRAY, fontSize = 11.sp)
                if (attempts > 0) Text("${best}ms", color = NEON_GRN, fontSize = 11.sp)
            }
            "ready" -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("[ RED ]", color = NEON_RED, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("wait...", color = NEON_RED, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("not yet!", color = GRAY, fontSize = 10.sp)
            }
            "go" -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("[ GO ]", color = NEON_GRN, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text("TAP NOW!", color = NEON_GRN, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
            "result" -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val rating = when { resultMs < 200 -> "LIGHTNING!"; resultMs < 300 -> "GREAT!"; resultMs < 450 -> "good"; else -> "sleeping?" }
                Text(rating, color = NEON_YLW, fontSize = 14.sp, fontWeight = FontWeight.Black)
                Text("${resultMs}ms", color = NEON_YLW, fontSize = 22.sp, fontWeight = FontWeight.Black)
                if (resultMs == best && attempts > 1) Text("NEW BEST!", color = GOLD, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("best: ${best}ms", color = NEON_GRN, fontSize = 11.sp)
                Text("tap to retry", color = GRAY, fontSize = 10.sp)
            }
        }
        Text("< back", Modifier.align(Alignment.TopStart).padding(4.dp).clickable { onBack() }, GRAY, 9.sp)
    }
}

// ==================== GAME OVER ====================
@Composable
fun GameOverScreen(emoji: String, name: String, score: Int, color: Color, onBack: () -> Unit, onRestart: () -> Unit) {
    Box(Modifier.fillMaxSize().background(BG), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(emoji, color = color, fontSize = 26.sp, fontWeight = FontWeight.Black)
            Text(name, color = color, fontSize = 14.sp, fontWeight = FontWeight.Black)
            Text("score: $score", color = NEON_YLW, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Button(onClick = onRestart, modifier = Modifier.size(width = 100.dp, height = 34.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = color)) {
                Text("again!", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Button(onClick = onBack, modifier = Modifier.size(width = 100.dp, height = 30.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = CARD_BG)) {
                Text("menu", color = GRAY, fontSize = 12.sp)
            }
        }
    }
}
