package com.tefillin.shalom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

val BG       = Color(0xFF0A0A1A)
val NEON_RED = Color(0xFFFF2D55)
val NEON_BLU = Color(0xFF00E5FF)
val NEON_GRN = Color(0xFF00FF87)
val NEON_YLW = Color(0xFFFFE600)
val NEON_PRP = Color(0xFFBF5AF2)
val NEON_ORG = Color(0xFFFF9500)
val GOLD     = Color(0xFFFFD700)
val CARD_BG  = Color(0xFF16162A)
val GRAY     = Color(0xFF8E8E93)
val NEON_COLORS = listOf(NEON_RED, NEON_BLU, NEON_GRN, NEON_YLW, NEON_PRP, NEON_ORG)

sealed class Screen {
    object Splash   : Screen()
    object Menu     : Screen()
    object Snake    : Screen()
    object TapAtk   : Screen()
    object Brick    : Screen()
    object Memory   : Screen()
    object Reaction : Screen()
}

data class GameInfo(val emoji: String, val name: String, val color: Color, val screen: Screen)

val GAMES = listOf(
    GameInfo("S", "Snake",      NEON_GRN, Screen.Snake),
    GameInfo("T", "Tap Attack", NEON_RED, Screen.TapAtk),
    GameInfo("B", "Brick",      NEON_BLU, Screen.Brick),
    GameInfo("M", "Memory",     NEON_PRP, Screen.Memory),
    GameInfo("R", "Reaction",   NEON_ORG, Screen.Reaction),
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
            Text(
                "* GAMES *",
                color = NEON_GRN,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.scale(logoScale).graphicsLayer { rotationZ = rot }
            )
            Text("MASTER", color = NEON_BLU.copy(alpha = titleAlpha * pulse),
                fontSize = 20.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
            Text("GAMES HUB", color = NEON_RED.copy(alpha = titleAlpha * pulse),
                fontSize = 14.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(4.dp))
            Box(
                Modifier.graphicsLayer { alpha = badgeAlpha }
                    .background(GOLD.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
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
                Text(
                    "GAME HUB",
                    color = NEON_BLU,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.fillMaxWidth().scale(pulse),
                    textAlign = TextAlign.Center
                )
            }
            items(GAMES) { game ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CARD_BG, RoundedCornerShape(12.dp))
                        .clickable { onSelect(game.screen) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            Modifier.size(34.dp).background(game.color.copy(alpha = 0.18f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(game.emoji, color = game.color, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }
                        Text(game.name, color = game.color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            item {
                Text(
                    "מאסטר קוד משחקים בע\"מ",
                    color = GRAY, fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
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

    Box(
        Modifier.fillMaxSize().background(BG)
            .pointerInput(Unit) {
                detectDragGestures { _, d ->
                    val dx = d.x; val dy = d.y
                    if (abs(dx) > abs(dy)) {
                        if (dx > 0 && dir != Dir.LEFT) nextDir = Dir.RIGHT
                        if (dx < 0 && dir != Dir.RIGHT) nextDir = Dir.LEFT
                    } else {
                        if (dy > 0 && dir != Dir.UP) nextDir = Dir.DOWN
                        if (dy < 0 && dir != Dir.DOWN) nextDir = Dir.UP
                    }
                }
            }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val cw = size.width / COLS
            val ch = size.height / ROWS
            for (r in 0 until ROWS) {
                for (c in 0 until COLS) {
                    val cellColor = if ((r + c) % 2 == 0) Color(0xFF1A1A2E) else BG
                    drawRect(cellColor, Offset(c * cw, r * ch), Size(cw, ch))
                }
            }
            drawCircle(NEON_RED, cw * 0.4f, Offset(food.x * cw + cw / 2, food.y * ch + ch / 2))
            snake.forEachIndexed { i, p ->
                val col = if (i == 0) NEON_GRN else Color(0xFF00897B)
                val pad = if (i == 0) 1.5f else 2.5f
                drawRoundRect(
                    col,
                    Offset(p.x * cw + pad, p.y * ch + pad),
                    Size(cw - pad * 2, ch - pad * 2),
                    CornerRadius(4f)
                )
            }
        }
        Text(
            score.toString(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 3.dp),
            color = NEON_YLW, fontSize = 13.sp, fontWeight = FontWeight.Black
        )
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
                val t = TapTarget(
                    id = nid++,
                    x = Random.nextFloat() * 0.75f + 0.05f,
                    y = Random.nextFloat() * 0.65f + 0.12f,
                    size = Random.nextFloat() * 16f + 24f,
                    color = NEON_COLORS.random(),
                    born = System.currentTimeMillis(),
                    life = life
                )
                targets = targets + t
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
                targets = targets - exp.toSet()
                lives -= exp.size
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
            Box(
                Modifier
                    .offset(x = (w * t.x) - (t.size / 2).dp, y = (h * t.y) - (t.size / 2).dp)
                    .size(t.size.dp)
                    .scale(pulse)
                    .clip(CircleShape)
                    .background(t.color.copy(alpha = 1f - prog * 0.6f))
                    .clickable { targets = targets.filter { it.id != t.id }; score++ }
            )
        }
        Text(score.toString(), Modifier.align(Alignment.TopCenter).padding(top = 3.dp),
            NEON_YLW, 15.sp, fontWeight = FontWeight.Black)
        val lvlText = "LV" + level.toString()
        Text(lvlText, Modifier.align(Alignment.TopEnd).padding(top = 3.dp, end = 5.dp),
            NEON_PRP, 9.sp, fontWeight = FontWeight.Bold)
        Row(Modifier.align(Alignment.BottomCenter).padding(bottom = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            repeat(3) { i ->
                val heart = if (i < lives) "v" else "x"
                Text(heart, color = if (i < lives) NEON_RED else GRAY, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
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

            val bw = 1f / bCols
            val bh = 0.12f
            bricks = bricks.map { (idx, isAlive) ->
                if (!isAlive) {
                    idx to false
                } else {
                    val bc = idx % bCols
                    val br = idx / bCols
                    val bx = bc * bw + bw / 2
                    val by2 = 0.08f + br * bh + bh / 2
                    if (abs(ballX - bx) < bw / 2 && abs(ballY - by2) < bh / 2) {
                        velY = -velY; score += 10
                        idx to false
                    } else {
                        idx to isAlive
                    }
                }
            }
            if (bricks.all { !it.second }) { won = true; alive = false }
        }
    }

    if (dead || won) {
        val label = if (won) "Win!" else "Brick"
        GameOverScreen(if (won) "W" else "B", label, score, NEON_BLU, onBack) {
            dead = false; won = false; alive = true; score = 0
            ballX = 0.5f; ballY = 0.7f; velX = 0.018f; velY = -0.022f; padX = 0.5f
            bricks = initBricks()
        }
        return
    }

    BoxWithConstraints(
        Modifier.fillMaxSize().background(BG)
            .pointerInput(Unit) {
                detectDragGestures { _, d -> padX = (padX + d.x / 800f).coerceIn(0.15f, 0.85f) }
            }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = size.width; val sh = size.height
            val bw = 1f / bCols; val bh = 0.12f
            bricks.forEach { (idx, isAlive) ->
                if (isAlive) {
                    val bc = idx % bCols
                    val br = idx / bCols
                    val col = brickColors[br % brickColors.size]
                    drawRoundRect(
                        col,
                        Offset(bc * bw * sw + 2f, 0.08f * sh + br * bh * sh + 2f),
                        Size(bw * sw - 4f, bh * sh - 4f),
                        CornerRadius(4f)
                    )
                }
            }
            drawCircle(NEON_YLW, 8f, Offset(ballX * sw, ballY * sh))
            drawRoundRect(
                Color.White,
                Offset((padX - 0.14f) * sw, 0.84f * sh),
                Size(0.28f * sw, 10f),
                CornerRadius(5f)
            )
        }
        Text(score.toString(), Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp),
            NEON_YLW, 12.sp, fontWeight = FontWeight.Black)
        Text("< back", Modifier.align(Alignment.TopStart).padding(3.dp).clickable { onBack() }, GRAY, 9.sp)
    }
}

// ==================== MEMORY ====================
@Composable
fun MemoryGame(onBack: () -> Unit) {
    val symbols = listOf("A","B","C","D","E","F","G","H")
    val symColors = listOf(NEON_RED, NEON_BLU, NEON_GRN, NEON_YLW, NEON_PRP, NEON_ORG, GOLD, NEON_GRN)
    val pairs = remember { (symbols + symbols).shuffled() }
    var flipped  by remember { mutableStateOf(listOf<Int>()) }
    var matched  by remember { mutableStateOf(setOf<Int>()) }
    var moves    by remember { mutableStateOf(0) }
    var won      by remember { mutableStateOf(false) }
    var canFlip  by remember { mutableStateOf(true) }

    fun flip(idx: Int) {
        if (!canFlip || flipped.contains(idx) || matched.contains(idx)) return
        val nf = flipped + idx
        flipped = nf
        if (nf.size == 2) {
            canFlip = false; moves++
            if (pairs[nf[0]] == pairs[nf[1]]) {
                matched = matched + nf[0] + nf[1]
                flipped = listOf(); canFlip = true
                if (matched.size == pairs.size) won = true
            }
        }
    }

    LaunchedEffect(flipped) {
        if (flipped.size == 2 && !matched.containsAll(flipped)) {
            delay(700); flipped = listOf(); canFlip = true
        }
    }

    if (won) {
        GameOverScreen("M", "Memory", moves, NEON_PRP, onBack) {
            won = false; moves = 0; flipped = listOf(); matched = setOf(); canFlip = true
        }
        return
    }

    Box(Modifier.fillMaxSize().background(BG)) {
        Column(
            Modifier.fillMaxSize().padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("< back", Modifier.clickable { onBack() }, GRAY, 9.sp)
                Text("MEMORY", color = NEON_PRP, fontSize = 11.sp, fontWeight = FontWeight.Black)
                Text(moves.toString(), color = NEON_YLW, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            for (row in 0 until 4) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (col in 0 until 4) {
                        val idx = row * 4 + col
                        val isFlipped = flipped.contains(idx)
                        val isMatched = matched.contains(idx)
                        val show = isFlipped || isMatched
                        val symIdx = symbols.indexOf(pairs[idx])
                        val symColor = symColors.getOrElse(symIdx) { NEON_BLU }
                        Box(
                            Modifier.size(32.dp)
                                .background(
                                    if (isMatched) symColor.copy(alpha = 0.25f)
                                    else if (isFlipped) CARD_BG
                                    else Color(0xFF1E1E3A),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { flip(idx) },
                            contentAlignment = Alignment.Center
                        ) {
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
    var state     by remember { mutableStateOf("wait") }
    var startMs   by remember { mutableStateOf(0L) }
    var resultMs  by remember { mutableStateOf(0L) }
    var best      by remember { mutableStateOf(9999L) }
    var attempts  by remember { mutableStateOf(0) }

    LaunchedEffect(state) {
        if (state == "ready") {
            val wait = Random.nextLong(1500, 4000)
            delay(wait)
            if (state == "ready") { startMs = System.currentTimeMillis(); state = "go" }
        }
    }

    val bgColor = when (state) {
        "ready"  -> Color(0xFF1A0A0A)
        "go"     -> Color(0xFF0A1A0A)
        else     -> BG
    }

    Box(
        Modifier.fillMaxSize().background(bgColor).clickable {
            when (state) {
                "wait"   -> state = "ready"
                "ready"  -> state = "wait"
                "go"     -> {
                    val ms = System.currentTimeMillis() - startMs
                    resultMs = ms
                    if (ms < best) best = ms
                    attempts++
                    state = "result"
                }
                "result" -> state = "ready"
            }
        },
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            "wait" -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("[ ]", color = NEON_ORG, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text("REACTION", color = NEON_ORG, fontSize = 16.sp, fontWeight = FontWeight.Black)
                Text("TIME", color = NEON_YLW, fontSize = 14.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(4.dp))
                Text("tap to start", color = GRAY, fontSize = 11.sp)
                if (attempts > 0) {
                    val bestStr = best.toString() + "ms"
                    Text(bestStr, color = NEON_GRN, fontSize = 11.sp)
                }
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
                val rating = when {
                    resultMs < 200 -> "LIGHTNING!"
                    resultMs < 300 -> "GREAT!"
                    resultMs < 450 -> "good"
                    else           -> "sleeping?"
                }
                Text(rating, color = NEON_YLW, fontSize = 14.sp, fontWeight = FontWeight.Black)
                val resStr = resultMs.toString() + "ms"
                Text(resStr, color = NEON_YLW, fontSize = 22.sp, fontWeight = FontWeight.Black)
                if (resultMs == best && attempts > 1) Text("NEW BEST!", color = GOLD, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                val bestStr2 = "best: " + best.toString() + "ms"
                Text(bestStr2, color = NEON_GRN, fontSize = 11.sp)
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
            val scoreStr = "score: " + score.toString()
            Text(scoreStr, color = NEON_YLW, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = onRestart,
                modifier = Modifier.size(width = 100.dp, height = 34.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = color)
            ) {
                Text("again!", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onBack,
                modifier = Modifier.size(width = 100.dp, height = 30.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = CARD_BG)
            ) {
                Text("menu", color = GRAY, fontSize = 12.sp)
            }
        }
    }
}
