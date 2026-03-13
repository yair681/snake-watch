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

val BG         = Color(0xFF0A0A1A)
val NEON_RED   = Color(0xFFFF2D55)
val NEON_BLU   = Color(0xFF00E5FF)
val NEON_GRN   = Color(0xFF00FF87)
val NEON_YLW   = Color(0xFFFFE600)
val NEON_PRP   = Color(0xFFBF5AF2)
val NEON_ORG   = Color(0xFFFF9500)
val GOLD       = Color(0xFFFFD700)
val CARD_BG    = Color(0xFF16162A)
val GRAY       = Color(0xFF8E8E93)
val NEON_COLORS = listOf(NEON_RED, NEON_BLU, NEON_GRN, NEON_YLW, NEON_PRP, NEON_ORG)

sealed class Screen {
    object Splash    : Screen()
    object Menu      : Screen()
    object Snake     : Screen()
    object TapAtk    : Screen()
    object Brick     : Screen()
    object Memory    : Screen()
    object Reaction  : Screen()
    object Flappy    : Screen()
    object Space     : Screen()
    object Dodge     : Screen()
    object Game2048  : Screen()
    object Simon     : Screen()
    object Mole      : Screen()
    object HiLo      : Screen()
    object Mines     : Screen()
    object Pong      : Screen()
    object ColorRush : Screen()
    object NumBomb   : Screen()
}

data class GameInfo(val letter: String, val name: String, val color: Color, val screen: Screen)

val GAMES = listOf(
    GameInfo("נ",  "נחש",          NEON_GRN, Screen.Snake),
    GameInfo("ה",  "הקש",          NEON_RED, Screen.TapAtk),
    GameInfo("לב", "לבנים",        NEON_BLU, Screen.Brick),
    GameInfo("ז",  "זיכרון",       NEON_PRP, Screen.Memory),
    GameInfo("ת",  "תגובה",        NEON_ORG, Screen.Reaction),
    GameInfo("צ",  "ציפור קופצת", NEON_YLW, Screen.Flappy),
    GameInfo("ח",  "חללית",        NEON_BLU, Screen.Space),
    GameInfo("ה",  "הימנע",        NEON_RED, Screen.Dodge),
    GameInfo("20", "2048",         NEON_GRN, Screen.Game2048),
    GameInfo("ס",  "סיימון",       NEON_PRP, Screen.Simon),
    GameInfo("ח",  "חפרפרת",      NEON_ORG, Screen.Mole),
    GameInfo("ג",  "גבוה/נמוך",   NEON_YLW, Screen.HiLo),
    GameInfo("מ",  "מוקשים",      NEON_RED, Screen.Mines),
    GameInfo("פ",  "פינג-פונג",   NEON_BLU, Screen.Pong),
    GameInfo("צ",  "צבע מהיר",    NEON_GRN, Screen.ColorRush),
    GameInfo("פ",  "פצצת מספרים", NEON_PRP, Screen.NumBomb),
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
        Screen.Splash    -> SplashScreen   { screen = Screen.Menu }
        Screen.Menu      -> MenuScreen     { screen = it }
        Screen.Snake     -> SnakeGame      { screen = Screen.Menu }
        Screen.TapAtk    -> TapAttackGame  { screen = Screen.Menu }
        Screen.Brick     -> BrickGame      { screen = Screen.Menu }
        Screen.Memory    -> MemoryGame     { screen = Screen.Menu }
        Screen.Reaction  -> ReactionGame   { screen = Screen.Menu }
        Screen.Flappy    -> FlappyGame     { screen = Screen.Menu }
        Screen.Space     -> SpaceGame      { screen = Screen.Menu }
        Screen.Dodge     -> DodgeGame      { screen = Screen.Menu }
        Screen.Game2048  -> Game2048       { screen = Screen.Menu }
        Screen.Simon     -> SimonGame      { screen = Screen.Menu }
        Screen.Mole      -> MoleGame       { screen = Screen.Menu }
        Screen.HiLo      -> HiLoGame       { screen = Screen.Menu }
        Screen.Mines     -> MineGame       { screen = Screen.Menu }
        Screen.Pong      -> PongGame       { screen = Screen.Menu }
        Screen.ColorRush -> ColorRushGame  { screen = Screen.Menu }
        Screen.NumBomb   -> NumBombGame    { screen = Screen.Menu }
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
            Text("* 16 משחקים *", color = NEON_GRN, fontSize = 12.sp, fontWeight = FontWeight.Black,
                modifier = Modifier.scale(logoScale).graphicsLayer { rotationZ = rot })
            Text("מאסטר", color = NEON_BLU.copy(alpha = titleAlpha * pulse),
                fontSize = 20.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
            Text("גיים האב", color = NEON_RED.copy(alpha = titleAlpha * pulse),
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
                Text("גיים האב", color = NEON_BLU, fontSize = 13.sp, fontWeight = FontWeight.Black,
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
                            Text(game.letter, color = game.color, fontSize = 14.sp, fontWeight = FontWeight.Black)
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

// ==================== GAME OVER ====================
@Composable
fun GameOverScreen(emoji: String, name: String, score: Int, scoreLabel: String, color: Color, onBack: () -> Unit, onRestart: () -> Unit) {
    Box(Modifier.fillMaxSize().background(BG), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(emoji, color = color, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text(name, color = color, fontSize = 12.sp, fontWeight = FontWeight.Black)
            Text("המשחק הסתיים", color = GRAY, fontSize = 10.sp)
            Text(scoreLabel + ": " + score.toString(), color = NEON_YLW, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Button(onClick = onRestart, modifier = Modifier.size(width = 100.dp, height = 34.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = color)) {
                Text("שוב!", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Button(onClick = onBack, modifier = Modifier.size(width = 100.dp, height = 30.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = CARD_BG)) {
                Text("תפריט", color = GRAY, fontSize = 12.sp)
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
        GameOverScreen("נ", "נחש", score, "ניקוד", NEON_GRN, onBack) {
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
        Text("חזרה", Modifier.align(Alignment.TopStart).padding(3.dp).clickable { onBack() }, GRAY, 9.sp)
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
        GameOverScreen("ה", "הקש!", score, "ניקוד", NEON_RED, onBack) {
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
        Text("רמה " + level.toString(), Modifier.align(Alignment.TopEnd).padding(top = 3.dp, end = 5.dp), NEON_PRP, 9.sp, fontWeight = FontWeight.Bold)
        Row(Modifier.align(Alignment.BottomCenter).padding(bottom = 5.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            repeat(3) { i -> Text(if (i < lives) "v" else "x", color = if (i < lives) NEON_RED else GRAY, fontSize = 11.sp, fontWeight = FontWeight.Black) }
        }
        Text("חזרה", Modifier.align(Alignment.TopStart).padding(3.dp).clickable { onBack() }, GRAY, 9.sp)
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
        GameOverScreen(if (won) "W" else "לב", if (won) "ניצחת!" else "לבנים", score, "ניקוד", NEON_BLU, onBack) {
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
        Text("חזרה", Modifier.align(Alignment.TopStart).padding(3.dp).clickable { onBack() }, GRAY, 9.sp)
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
        GameOverScreen("ז", "זיכרון", moves, "מהלכים", NEON_PRP, onBack) {
            won = false; moves = 0; flipped = listOf(); matched = setOf(); canFlip = true
        }
        return
    }

    Box(Modifier.fillMaxSize().background(BG)) {
        Column(Modifier.fillMaxSize().padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("חזרה", Modifier.clickable { onBack() }, GRAY, 9.sp)
                Text("זיכרון", color = NEON_PRP, fontSize = 11.sp, fontWeight = FontWeight.Black)
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
                Text("תגובה", color = NEON_ORG, fontSize = 16.sp, fontWeight = FontWeight.Black)
                Text("הקש להתחיל", color = GRAY, fontSize = 11.sp)
                if (attempts > 0) Text(best.toString() + "ms", color = NEON_GRN, fontSize = 11.sp)
            }
            "ready" -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("[ אדום ]", color = NEON_RED, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("המתן...", color = NEON_RED, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("עוד לא!", color = GRAY, fontSize = 10.sp)
            }
            "go" -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("[ קדימה ]", color = NEON_GRN, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("הקש עכשיו!", color = NEON_GRN, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
            "result" -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val rating = when { resultMs < 200 -> "ברק!"; resultMs < 300 -> "מעולה!"; resultMs < 450 -> "טוב"; else -> "ישנת?" }
                Text(rating, color = NEON_YLW, fontSize = 14.sp, fontWeight = FontWeight.Black)
                Text(resultMs.toString() + "ms", color = NEON_YLW, fontSize = 22.sp, fontWeight = FontWeight.Black)
                if (resultMs == best && attempts > 1) Text("שיא חדש!", color = GOLD, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("שיא: " + best.toString() + "ms", color = NEON_GRN, fontSize = 11.sp)
                Text("הקש לנסות שוב", color = GRAY, fontSize = 10.sp)
            }
        }
        Text("חזרה", Modifier.align(Alignment.TopStart).padding(4.dp).clickable { onBack() }, GRAY, 9.sp)
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
            pipes.forEach { p ->
                if (p.x < birdX + 0.01f && p.x > birdX - 0.01f) score++
            }
            if (birdY >= 0.95f || birdY <= 0.05f) { dead = true; break }
            pipes.forEach { p ->
                if (abs(birdX - p.x) < pipeW / 2f + 0.035f) {
                    if (birdY < p.gapY - gapH / 2f || birdY > p.gapY + gapH / 2f) dead = true
                }
            }
        }
    }

    if (dead) {
        GameOverScreen("צ", "ציפור קופצת", score, "ניקוד", NEON_YLW, onBack, ::reset)
        return
    }

    Box(Modifier.fillMaxSize().background(Color(0xFF071B2F))
        .pointerInput(Unit) {
            detectTapGestures {
                if (!started) started = true
                velY = -0.024f
            }
        }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = size.width; val sh = size.height
            pipes.forEach { p ->
                val px   = p.x * sw
                val topH = (p.gapY - gapH / 2f) * sh
                val botY = (p.gapY + gapH / 2f) * sh
                drawRoundRect(NEON_GRN, Offset(px - pipeW / 2f * sw, 0f), Size(pipeW * sw, topH), CornerRadius(6f))
                drawRoundRect(NEON_GRN.copy(alpha = 0.4f), Offset(px - pipeW / 2f * sw - 4f, topH - 14f), Size(pipeW * sw + 8f, 14f), CornerRadius(4f))
                drawRoundRect(NEON_GRN, Offset(px - pipeW / 2f * sw, botY), Size(pipeW * sw, sh - botY), CornerRadius(6f))
                drawRoundRect(NEON_GRN.copy(alpha = 0.4f), Offset(px - pipeW / 2f * sw - 4f, botY), Size(pipeW * sw + 8f, 14f), CornerRadius(4f))
            }
            val bx = birdX * sw; val by2 = birdY * sh
            drawCircle(NEON_YLW, 13f, Offset(bx, by2))
            drawCircle(Color(0xFFFFAA00), 13f, Offset(bx, by2), style = androidx.compose.ui.graphics.drawscope.Stroke(3f))
            drawCircle(Color.Black, 4f, Offset(bx + 5f, by2 - 3f))
            val path = Path().apply { moveTo(bx - 5f, by2); lineTo(bx - 14f, by2 - 8f); lineTo(bx - 2f, by2 + 4f); close() }
            drawPath(path, Color(0xFFFFAA00))
            drawRect(NEON_GRN.copy(alpha = 0.25f), Offset(0f, sh * 0.95f), Size(sw, sh * 0.05f))
        }
        Text(score.toString(), modifier = Modifier.align(Alignment.TopCenter).padding(top = 6.dp),
            color = NEON_YLW, fontSize = 22.sp, fontWeight = FontWeight.Black)
        if (!started) {
            Box(Modifier.align(Alignment.Center)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)) {
                Text("הקש לעוף", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
        }
        Text("חזרה", Modifier.align(Alignment.TopStart).padding(4.dp).clickable { onBack() }, GRAY, 9.sp)
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
            delay(30); frame++
            shootTimer++
            if (shootTimer >= 18) { shootTimer = 0; bullets = bullets + Bullet(bulletId++, shipX, 0.83f) }
            bullets = bullets.map { it.copy(y = it.y - 0.055f) }.filter { it.y > 0f }
            val spawnChance = if (frame < 100) 0.03f else if (frame < 300) 0.05f else 0.07f
            if (Random.nextFloat() < spawnChance) {
                asteroids = asteroids + Asteroid(asteroidId++,
                    Random.nextFloat() * 0.8f + 0.1f, -0.06f,
                    Random.nextFloat() * 0.055f + 0.035f, NEON_COLORS.random())
            }
            asteroids = asteroids.map { it.copy(y = it.y + 0.018f) }
            val hitBullets = mutableSetOf<Int>(); val hitAsteroids = mutableSetOf<Int>()
            bullets.forEach { b ->
                asteroids.forEach { a ->
                    val dx = b.x - a.x; val dy = b.y - a.y
                    if (sqrt(dx * dx + dy * dy) < a.radius + 0.03f) {
                        hitBullets.add(b.id); hitAsteroids.add(a.id); score += 10
                        explosions = explosions + Explosion(a.x, a.y, System.currentTimeMillis())
                    }
                }
            }
            bullets = bullets.filter { it.id !in hitBullets }
            asteroids = asteroids.filter { it.id !in hitAsteroids }
            val shipHit = asteroids.filter { a ->
                val dx = a.x - shipX; val dy = a.y - 0.87f
                sqrt(dx * dx + dy * dy) < a.radius + 0.07f
            }
            if (shipHit.isNotEmpty()) {
                asteroids = asteroids.filter { it.id !in shipHit.map { h -> h.id }.toSet() }
                lives -= 1
                if (lives <= 0) { dead = true; break }
            }
            asteroids = asteroids.filter { it.y < 1.1f }
            val nowMs2 = System.currentTimeMillis()
            explosions = explosions.filter { nowMs2 - it.born < 350 }
        }
    }

    if (dead) {
        GameOverScreen("ח", "חללית", score, "ניקוד", NEON_BLU, onBack, ::reset)
        return
    }

    Box(Modifier.fillMaxSize().background(BG)
        .pointerInput(Unit) {
            detectDragGestures { _, d -> shipX = (shipX + d.x / size.width).coerceIn(0.10f, 0.90f) }
        }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = size.width; val sh = size.height
            repeat(25) { i ->
                val sx = (i * 137.5f) % sw
                val sy = (i * 79.3f + frame * 0.4f) % sh
                drawCircle(Color.White.copy(alpha = 0.25f + (i % 3) * 0.1f), 1.5f, Offset(sx, sy))
            }
            bullets.forEach { b ->
                drawRect(NEON_YLW, Offset(b.x * sw - 2f, b.y * sh - 10f), Size(4f, 12f))
            }
            asteroids.forEach { a ->
                drawCircle(a.color, a.radius * sw, Offset(a.x * sw, a.y * sh))
                drawCircle(a.color.copy(alpha = 0.25f), a.radius * sw + 5f, Offset(a.x * sw, a.y * sh))
            }
            val nowMs = System.currentTimeMillis()
            explosions.forEach { ex ->
                val prog = (nowMs - ex.born) / 350f
                drawCircle(NEON_ORG.copy(alpha = (1f - prog) * 0.9f), (18f + prog * 28f), Offset(ex.x * sw, ex.y * sh))
                drawCircle(NEON_YLW.copy(alpha = (1f - prog) * 0.6f), (10f + prog * 14f), Offset(ex.x * sw, ex.y * sh))
            }
            val sx2 = shipX * sw; val sy2 = 0.87f * sh
            val shipPath = Path().apply {
                moveTo(sx2, sy2 - 20f); lineTo(sx2 - 13f, sy2 + 12f)
                lineTo(sx2, sy2 + 6f); lineTo(sx2 + 13f, sy2 + 12f); close()
            }
            drawPath(shipPath, NEON_BLU)
            val flamePath = Path().apply {
                moveTo(sx2 - 6f, sy2 + 6f); lineTo(sx2, sy2 + 20f); lineTo(sx2 + 6f, sy2 + 6f); close()
            }
            drawPath(flamePath, NEON_ORG.copy(alpha = 0.8f))
        }
        Text(score.toString(), modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp),
            color = NEON_YLW, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Row(modifier = Modifier.align(Alignment.TopEnd).padding(top = 4.dp, end = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            repeat(3) { i -> Text(if (i < lives) "v" else ".", color = if (i < lives) NEON_BLU else GRAY, fontSize = 11.sp) }
        }
        Text("חזרה", Modifier.align(Alignment.TopStart).padding(4.dp).clickable { onBack() }, GRAY, 9.sp)
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

    fun reset() { dead = false; score = 0; frame = 0; balls = listOf(); playerX = 0.5f; playerY = 0.72f }

    LaunchedEffect(Unit) {
        while (!dead) {
            delay(18); frame++; score = frame / 12
            val spawnEvery = maxOf(18, 55 - frame / 40)
            if (frame % spawnEvery == 0) {
                val speed = 0.012f + frame / 8000f
                balls = balls + DodgeBall(ballId++,
                    Random.nextFloat() * 0.8f + 0.1f, -0.05f,
                    (Random.nextFloat() - 0.5f) * 0.010f,
                    speed + Random.nextFloat() * 0.008f,
                    NEON_COLORS.random(), Random.nextFloat() * 0.03f + 0.03f)
            }
            balls = balls.map {
                var nx = it.x + it.vx; var nvx = it.vx
                if (nx < 0.04f || nx > 0.96f) { nvx = -nvx; nx = it.x + nvx }
                it.copy(x = nx, y = it.y + it.vy, vx = nvx)
            }.filter { it.y < 1.12f }
            val hit = balls.any { b ->
                val dx = b.x - playerX; val dy = b.y - playerY
                sqrt(dx * dx + dy * dy) < b.radius + 0.07f
            }
            if (hit) { dead = true; break }
        }
    }

    if (dead) {
        GameOverScreen("ה", "הימנע", score, "ניקוד", NEON_RED, onBack, ::reset)
        return
    }

    Box(Modifier.fillMaxSize().background(BG)
        .pointerInput(Unit) {
            detectDragGestures { _, d ->
                playerX = (playerX + d.x / size.width).coerceIn(0.07f, 0.93f)
                playerY = (playerY + d.y / size.height).coerceIn(0.07f, 0.93f)
            }
        }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = size.width; val sh = size.height
            balls.forEach { b ->
                drawCircle(b.color, b.radius * sw, Offset(b.x * sw, b.y * sh))
                drawCircle(b.color.copy(alpha = 0.3f), b.radius * sw + 5f, Offset(b.x * sw, b.y * sh))
            }
            val px = playerX * sw; val py = playerY * sh; val r = 15f
            val diamond = Path().apply {
                moveTo(px, py - r); lineTo(px + r, py); lineTo(px, py + r); lineTo(px - r, py); close()
            }
            drawPath(diamond, NEON_GRN)
            drawCircle(NEON_GRN.copy(alpha = 0.25f), r + 7f, Offset(px, py))
        }
        Text(score.toString(), modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp),
            color = NEON_YLW, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Text("חזרה", Modifier.align(Alignment.TopStart).padding(4.dp).clickable { onBack() }, GRAY, 9.sp)
    }
}

// ==================== 2048 ====================
@Composable
fun Game2048(onBack: () -> Unit) {
    fun emptyBoard(): Array<IntArray> = Array(4) { IntArray(4) }
    fun addTile(b: Array<IntArray>) {
        val empty = mutableListOf<Pair<Int,Int>>()
        for (r in 0..3) for (c in 0..3) if (b[r][c] == 0) empty.add(r to c)
        if (empty.isEmpty()) return
        val (r, c) = empty.random()
        b[r][c] = if (Random.nextFloat() < 0.85f) 2 else 4
    }
    fun initBoard(): Array<IntArray> {
        val b = emptyBoard(); addTile(b); addTile(b); return b
    }
    fun slideLeft(row: IntArray): IntArray {
        val filtered = row.filter { it != 0 }.toMutableList()
        var i = 0
        while (i < filtered.size - 1) {
            if (filtered[i] == filtered[i + 1]) { filtered[i] *= 2; filtered.removeAt(i + 1) }
            i++
        }
        while (filtered.size < 4) filtered.add(0)
        return filtered.toIntArray()
    }
    fun moveLeft(b: Array<IntArray>): Array<IntArray> = Array(4) { slideLeft(b[it]) }
    fun rotateRight(b: Array<IntArray>): Array<IntArray> = Array(4) { r -> IntArray(4) { c -> b[3 - c][r] } }
    fun rotateLeft(b: Array<IntArray>): Array<IntArray> = Array(4) { r -> IntArray(4) { c -> b[c][3 - r] } }
    fun moveRight(b: Array<IntArray>): Array<IntArray> = rotateRight(rotateRight(moveLeft(rotateRight(rotateRight(b)))))
    fun moveUp(b: Array<IntArray>): Array<IntArray> = rotateLeft(moveLeft(rotateRight(b)))
    fun moveDown(b: Array<IntArray>): Array<IntArray> = rotateRight(moveLeft(rotateLeft(b)))
    fun calcScore(b: Array<IntArray>) = b.sumOf { it.sum() }
    fun arrEqual(a: Array<IntArray>, b2: Array<IntArray>) = a.indices.all { r -> a[r].contentEquals(b2[r]) }

    var board by remember { mutableStateOf(initBoard()) }
    var score by remember { mutableStateOf(0) }
    var dead  by remember { mutableStateOf(false) }

    val tileColors = mapOf(
        0 to Color(0xFF1A1A2E), 2 to Color(0xFF1E3A2E), 4 to Color(0xFF1E3A1A),
        8 to Color(0xFF2E2A00), 16 to Color(0xFF3A1A00), 32 to Color(0xFF3A0010),
        64 to Color(0xFF2A0040), 128 to Color(0xFF002040), 256 to Color(0xFF003A30),
        512 to Color(0xFF303000), 1024 to Color(0xFF400020), 2048 to Color(0xFF400040)
    )
    val tileText = mapOf(
        0 to Color.Transparent, 2 to NEON_GRN, 4 to NEON_GRN, 8 to NEON_YLW,
        16 to NEON_ORG, 32 to NEON_RED, 64 to NEON_PRP, 128 to NEON_BLU,
        256 to NEON_GRN, 512 to NEON_YLW, 1024 to NEON_ORG, 2048 to GOLD
    )

    if (dead) {
        GameOverScreen("20", "2048", score, "ניקוד", NEON_GRN, onBack) {
            dead = false; board = initBoard(); score = 0
        }
        return
    }

    Box(Modifier.fillMaxSize().background(BG)
        .pointerInput(Unit) {
            detectDragGestures { _, d ->
                val dx = d.x; val dy = d.y
                val moved: Array<IntArray>
                if (abs(dx) > abs(dy)) {
                    moved = if (dx > 0) moveRight(board) else moveLeft(board)
                } else {
                    moved = if (dy > 0) moveDown(board) else moveUp(board)
                }
                if (!arrEqual(board, moved)) {
                    addTile(moved); board = moved; score = calcScore(moved)
                    val empty2 = (0..3).any { r -> (0..3).any { c -> moved[r][c] == 0 } }
                    if (!empty2) dead = true
                }
            }
        }
    ) {
        Column(Modifier.fillMaxSize().padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("חזרה", Modifier.clickable { onBack() }, GRAY, 9.sp)
                Text("2048", color = NEON_GRN, fontSize = 13.sp, fontWeight = FontWeight.Black)
                Text(score.toString(), color = NEON_YLW, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            for (r in 0..3) {
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    for (c in 0..3) {
                        val v = board[r][c]
                        Box(Modifier.size(40.dp).background(tileColors.getOrDefault(v, Color(0xFF1A0040)), RoundedCornerShape(4.dp)),
                            Alignment.Center) {
                            if (v > 0) Text(v.toString(), color = tileText.getOrDefault(v, NEON_BLU),
                                fontSize = if (v < 100) 13.sp else if (v < 1000) 10.sp else 8.sp,
                                fontWeight = FontWeight.Black)
                        }
                    }
                }
                Spacer(Modifier.height(3.dp))
            }
        }
    }
}

// ==================== SIMON SAYS ====================
@Composable
fun SimonGame(onBack: () -> Unit) {
    val simonColors = listOf(NEON_GRN, NEON_RED, NEON_YLW, NEON_BLU)
    val colorNames  = listOf("ירוק", "אדום", "צהוב", "כחול")
    var sequence by remember { mutableStateOf(listOf<Int>()) }
    var playerSeq by remember { mutableStateOf(listOf<Int>()) }
    var showing  by remember { mutableStateOf(false) }
    var showIdx  by remember { mutableStateOf(-1) }
    var canInput by remember { mutableStateOf(false) }
    var failed   by remember { mutableStateOf(false) }
    var round    by remember { mutableStateOf(0) }
    var started  by remember { mutableStateOf(false) }

    suspend fun playSequence() {
        showing = true; canInput = false
        delay(500)
        sequence.forEachIndexed { _, colorIdx ->
            showIdx = colorIdx; delay(600); showIdx = -1; delay(200)
        }
        showing = false; canInput = true
    }

    LaunchedEffect(round) {
        if (!started || failed) return@LaunchedEffect
        val newSeq = sequence + Random.nextInt(4)
        sequence = newSeq; playerSeq = listOf()
        playSequence()
    }

    fun onColorTap(idx: Int) {
        if (!canInput) return
        val newPS = playerSeq + idx; playerSeq = newPS
        val pos = newPS.size - 1
        if (sequence[pos] != idx) { failed = true; canInput = false; return }
        if (newPS.size == sequence.size) { canInput = false; round++ }
    }

    if (failed) {
        GameOverScreen("ס", "סיימון", sequence.size - 1, "רמה", NEON_PRP, onBack) {
            failed = false; sequence = listOf(); playerSeq = listOf()
            showing = false; showIdx = -1; canInput = false; round = 0; started = false
        }
        return
    }

    Box(Modifier.fillMaxSize().background(BG), Alignment.Center) {
        if (!started) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("סיימון", color = NEON_PRP, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text("חזור על הרצף", color = GRAY, fontSize = 11.sp)
                Button(onClick = { started = true; round = 1 },
                    colors = ButtonDefaults.buttonColors(backgroundColor = NEON_PRP)) {
                    Text("התחל", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("רמה " + (sequence.size).toString(), color = NEON_YLW, fontSize = 12.sp, fontWeight = FontWeight.Black)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(0, 1).forEach { idx ->
                        val lit = showIdx == idx || (canInput && playerSeq.lastOrNull() == idx)
                        Box(Modifier.size(60.dp).background(
                            if (lit) simonColors[idx] else simonColors[idx].copy(alpha = 0.25f),
                            RoundedCornerShape(8.dp)
                        ).clickable { onColorTap(idx) }, Alignment.Center) {
                            Text(colorNames[idx], color = if (lit) Color.Black else simonColors[idx], fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(2, 3).forEach { idx ->
                        val lit = showIdx == idx || (canInput && playerSeq.lastOrNull() == idx)
                        Box(Modifier.size(60.dp).background(
                            if (lit) simonColors[idx] else simonColors[idx].copy(alpha = 0.25f),
                            RoundedCornerShape(8.dp)
                        ).clickable { onColorTap(idx) }, Alignment.Center) {
                            Text(colorNames[idx], color = if (lit) Color.Black else simonColors[idx], fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                if (showing) Text("צפה...", color = GRAY, fontSize = 10.sp)
                else if (canInput) Text("תורך!", color = NEON_GRN, fontSize = 10.sp)
            }
        }
        Text("חזרה", Modifier.align(Alignment.TopStart).padding(4.dp).clickable { onBack() }, GRAY, 9.sp)
    }
}

// ==================== WHACK A MOLE ====================
data class Mole(val id: Int, val pos: Int, val born: Long, val life: Long)

@Composable
fun MoleGame(onBack: () -> Unit) {
    var moles   by remember { mutableStateOf(listOf<Mole>()) }
    var score   by remember { mutableStateOf(0) }
    var lives   by remember { mutableStateOf(3) }
    var moleId  by remember { mutableStateOf(0) }
    var dead    by remember { mutableStateOf(false) }
    var level   by remember { mutableStateOf(1) }
    val life    = maxOf(700L, 2000L - level * 150L)
    val rate    = maxOf(500L, 1500L - level * 100L)

    LaunchedEffect(lives) {
        if (lives <= 0) return@LaunchedEffect
        while (lives > 0) {
            delay(rate)
            val occupied = moles.map { it.pos }.toSet()
            val free = (0..8).filter { it !in occupied }
            if (free.isNotEmpty()) {
                moles = moles + Mole(moleId++, free.random(), System.currentTimeMillis(), life)
            }
        }
    }
    LaunchedEffect(lives) {
        if (lives <= 0) return@LaunchedEffect
        while (lives > 0) {
            delay(80)
            val now = System.currentTimeMillis()
            val exp = moles.filter { now - it.born > it.life }
            if (exp.isNotEmpty()) { moles = moles - exp.toSet(); lives -= exp.size; if (lives <= 0) { dead = true; break } }
        }
    }
    LaunchedEffect(score) { level = 1 + score / 5 }

    if (dead) {
        GameOverScreen("ח", "חפרפרת", score, "ניקוד", NEON_ORG, onBack) {
            dead = false; lives = 3; score = 0; moles = listOf(); level = 1
        }
        return
    }

    Box(Modifier.fillMaxSize().background(BG)) {
        Column(Modifier.fillMaxSize().padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("חזרה", Modifier.clickable { onBack() }, GRAY, 9.sp)
                Text("חפרפרת", color = NEON_ORG, fontSize = 11.sp, fontWeight = FontWeight.Black)
                Text(score.toString(), color = NEON_YLW, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(6.dp))
            for (row in 0..2) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    for (col in 0..2) {
                        val pos = row * 3 + col
                        val mole = moles.find { it.pos == pos }
                        val prog = mole?.let { (System.currentTimeMillis() - it.born).toFloat() / it.life } ?: 0f
                        Box(Modifier.size(52.dp)
                            .background(if (mole != null) NEON_ORG.copy(alpha = 1f - prog * 0.5f) else Color(0xFF1A1A2E), RoundedCornerShape(50))
                            .clickable {
                                if (mole != null) { moles = moles.filter { it.id != mole.id }; score++ }
                            }, Alignment.Center) {
                            if (mole != null) Text("^_^", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            else Text("( )", color = Color(0xFF333355), fontSize = 12.sp)
                        }
                    }
                }
                Spacer(Modifier.height(5.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(3) { i -> Text(if (i < lives) "v" else "x", color = if (i < lives) NEON_ORG else GRAY, fontSize = 12.sp) }
            }
        }
    }
}

// ==================== HIGH OR LOW ====================
@Composable
fun HiLoGame(onBack: () -> Unit) {
    val suits  = listOf("club", "diam", "heart", "spade")
    val values = listOf("A","2","3","4","5","6","7","8","9","10","J","Q","K")
    val suitColors = mapOf("club" to NEON_GRN, "diam" to NEON_BLU, "heart" to NEON_RED, "spade" to NEON_PRP)

    fun randCard() = Random.nextInt(13)
    fun randSuit() = suits.random()

    var curVal  by remember { mutableStateOf(randCard()) }
    var curSuit by remember { mutableStateOf(randSuit()) }
    var nextVal by remember { mutableStateOf(-1) }
    var nextSuit by remember { mutableStateOf("") }
    var score   by remember { mutableStateOf(0) }
    var lives   by remember { mutableStateOf(3) }
    var dead    by remember { mutableStateOf(false) }
    var result  by remember { mutableStateOf("") }

    fun guess(high: Boolean) {
        val nv = randCard(); val ns = randSuit()
        nextVal = nv; nextSuit = ns
        val correct = if (high) nv > curVal else nv < curVal
        if (nv == curVal) { result = "שוויון!"; curVal = nv; curSuit = ns; nextVal = -1; return }
        if (correct) { score++; result = "נכון!" } else { lives--; result = "טעות!"; if (lives <= 0) { dead = true; return } }
        curVal = nv; curSuit = ns; nextVal = -1
    }

    if (dead) {
        GameOverScreen("ג", "גבוה/נמוך", score, "ניקוד", NEON_YLW, onBack) {
            dead = false; score = 0; lives = 3; curVal = randCard(); curSuit = randSuit()
            nextVal = -1; result = ""
        }
        return
    }

    Box(Modifier.fillMaxSize().background(BG), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("גבוה / נמוך", color = NEON_YLW, fontSize = 13.sp, fontWeight = FontWeight.Black)
            Box(Modifier.size(70.dp).background(CARD_BG, RoundedCornerShape(10.dp)), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(values[curVal], color = suitColors[curSuit] ?: NEON_BLU, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Text(curSuit, color = (suitColors[curSuit] ?: NEON_BLU).copy(alpha = 0.6f), fontSize = 9.sp)
                }
            }
            if (result.isNotEmpty()) Text(result, color = if (result.startsWith("נכ")) NEON_GRN else NEON_RED, fontSize = 13.sp, fontWeight = FontWeight.Black)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { guess(false) }, Modifier.size(width = 70.dp, height = 34.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = NEON_BLU)) {
                    Text("נמוך", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(onClick = { guess(true) }, Modifier.size(width = 70.dp, height = 34.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = NEON_RED)) {
                    Text("גבוה", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("ניקוד: " + score.toString(), color = NEON_YLW, fontSize = 10.sp)
                repeat(3) { i -> Text(if (i < lives) "v" else "x", color = if (i < lives) NEON_GRN else GRAY, fontSize = 10.sp) }
            }
        }
        Text("חזרה", Modifier.align(Alignment.TopStart).padding(4.dp).clickable { onBack() }, GRAY, 9.sp)
    }
}

// ==================== MINESWEEPER ====================
@Composable
fun MineGame(onBack: () -> Unit) {
    val GS = 8; val MINES_COUNT = 9

    fun buildBoard(): Pair<Array<BooleanArray>, Array<IntArray>> {
        val mines = Array(GS) { BooleanArray(GS) }
        var placed = 0
        while (placed < MINES_COUNT) {
            val r = Random.nextInt(GS); val c = Random.nextInt(GS)
            if (!mines[r][c]) { mines[r][c] = true; placed++ }
        }
        val nums = Array(GS) { r -> IntArray(GS) { c ->
            if (mines[r][c]) -1
            else (-1..1).sumOf { dr -> (-1..1).sumOf { dc ->
                val nr = r + dr; val nc = c + dc
                if (nr in 0 until GS && nc in 0 until GS && mines[nr][nc]) 1 else 0
            } }
        } }
        return mines to nums
    }

    var (mines, nums) = remember { buildBoard() }
    var revealed by remember { mutableStateOf(Array(GS) { BooleanArray(GS) }) }
    var flagged  by remember { mutableStateOf(Array(GS) { BooleanArray(GS) }) }
    var dead     by remember { mutableStateOf(false) }
    var won      by remember { mutableStateOf(false) }
    var flagMode by remember { mutableStateOf(false) }
    var score    by remember { mutableStateOf(0) }

    fun revealFlood(r: Int, c: Int, rev: Array<BooleanArray>) {
        if (r !in 0 until GS || c !in 0 until GS || rev[r][c]) return
        rev[r][c] = true
        if (nums[r][c] == 0) (-1..1).forEach { dr -> (-1..1).forEach { dc -> revealFlood(r + dr, c + dc, rev) } }
    }

    fun onTap(r: Int, c: Int) {
        if (dead || won) return
        if (flagMode) {
            val nf = flagged.map { it.clone() }.toTypedArray(); nf[r][c] = !nf[r][c]; flagged = nf; return
        }
        if (flagged[r][c] || revealed[r][c]) return
        val nr = revealed.map { it.clone() }.toTypedArray()
        if (mines[r][c]) { nr[r][c] = true; revealed = nr; dead = true; return }
        revealFlood(r, c, nr); revealed = nr
        score = (0 until GS).sumOf { row -> (0 until GS).count { col -> nr[row][col] && !mines[row][col] } }
        val safe = GS * GS - MINES_COUNT
        if (score >= safe) won = true
    }

    fun reset() {
        val (m2, n2) = buildBoard(); mines = m2; nums = n2
        revealed = Array(GS) { BooleanArray(GS) }; flagged = Array(GS) { BooleanArray(GS) }
        dead = false; won = false; score = 0; flagMode = false
    }

    val numColors = listOf(Color.Transparent, NEON_BLU, NEON_GRN, NEON_RED, NEON_PRP, NEON_ORG, NEON_BLU, GRAY, GRAY)

    if (dead || won) {
        GameOverScreen("מ", if (won) "ניצחת!" else "מוקשים", score, "תאים", if (won) NEON_GRN else NEON_RED, onBack, ::reset)
        return
    }

    Box(Modifier.fillMaxSize().background(BG)) {
        Column(Modifier.fillMaxSize().padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("חזרה", Modifier.clickable { onBack() }, GRAY, 9.sp)
                Text("מוקשים", color = NEON_RED, fontSize = 10.sp, fontWeight = FontWeight.Black)
                Box(Modifier.background(if (flagMode) NEON_YLW.copy(0.2f) else Color.Transparent, RoundedCornerShape(4.dp))
                    .clickable { flagMode = !flagMode }.padding(4.dp)) {
                    Text(if (flagMode) "דגל:כן" else "דגל:לא", color = if (flagMode) NEON_YLW else GRAY, fontSize = 9.sp)
                }
            }
            Spacer(Modifier.height(2.dp))
            for (r in 0 until GS) {
                Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                    for (c in 0 until GS) {
                        val isRev = revealed[r][c]; val isFlagged = flagged[r][c]
                        val v = nums[r][c]
                        val bg = when {
                            isRev && mines[r][c] -> NEON_RED.copy(alpha = 0.5f)
                            isRev -> Color(0xFF1E2030)
                            isFlagged -> NEON_YLW.copy(alpha = 0.3f)
                            else -> Color(0xFF2A2A40)
                        }
                        Box(Modifier.size(22.dp).background(bg, RoundedCornerShape(2.dp))
                            .clickable { onTap(r, c) }, Alignment.Center) {
                            when {
                                isRev && mines[r][c] -> Text("*", color = NEON_RED, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                isRev && v > 0 -> Text(v.toString(), color = numColors.getOrElse(v) { GRAY }, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                isFlagged -> Text("!", color = NEON_YLW, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(1.dp))
            }
        }
    }
}

// ==================== PONG ====================
@Composable
fun PongGame(onBack: () -> Unit) {
    var ballX  by remember { mutableStateOf(0.5f) }
    var ballY  by remember { mutableStateOf(0.5f) }
    var velX   by remember { mutableStateOf(0.020f) }
    var velY   by remember { mutableStateOf(0.015f) }
    var padY   by remember { mutableStateOf(0.5f) }
    var aiY    by remember { mutableStateOf(0.5f) }
    var scoreP by remember { mutableStateOf(0) }
    var scoreA by remember { mutableStateOf(0) }
    var dead   by remember { mutableStateOf(false) }
    val padH = 0.18f; val padW = 0.05f

    fun reset() {
        ballX = 0.5f; ballY = 0.5f; velX = 0.020f; velY = 0.015f
        padY = 0.5f; aiY = 0.5f; scoreP = 0; scoreA = 0; dead = false
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            ballX += velX; ballY += velY
            if (ballY < 0.04f || ballY > 0.96f) velY = -velY
            aiY = (aiY + (ballY - aiY) * 0.04f).coerceIn(padH / 2, 1f - padH / 2)
            if (ballX < padW + 0.03f && abs(ballY - padY) < padH / 2) { velX = abs(velX); velX *= 1.02f }
            if (ballX > 1f - padW - 0.03f && abs(ballY - aiY) < padH / 2) { velX = -abs(velX) }
            if (ballX < 0.01f) { scoreA++; ballX = 0.5f; ballY = 0.5f; if (scoreA >= 5) { dead = true } }
            if (ballX > 0.99f) { scoreP++; ballX = 0.5f; ballY = 0.5f; if (scoreP >= 5) { dead = true } }
        }
    }

    if (dead) {
        GameOverScreen("פ", "פינג-פונג", scoreP, "נקודות", NEON_BLU, onBack, ::reset)
        return
    }

    Box(Modifier.fillMaxSize().background(BG)
        .pointerInput(Unit) {
            detectDragGestures { _, d -> padY = (padY + d.y / size.height).coerceIn(padH / 2, 1f - padH / 2) }
        }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = size.width; val sh = size.height
            drawLine(GRAY.copy(alpha = 0.3f), Offset(sw / 2, 0f), Offset(sw / 2, sh), 1f)
            drawRoundRect(NEON_GRN, Offset(padW * sw - 4f, (padY - padH / 2) * sh), Size(8f, padH * sh), CornerRadius(4f))
            drawRoundRect(NEON_RED, Offset((1f - padW) * sw - 4f, (aiY - padH / 2) * sh), Size(8f, padH * sh), CornerRadius(4f))
            drawCircle(NEON_YLW, 8f, Offset(ballX * sw, ballY * sh))
        }
        Row(Modifier.align(Alignment.TopCenter).padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Text(scoreP.toString(), color = NEON_GRN, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(scoreA.toString(), color = NEON_RED, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
        Text("גרור למעלה/מטה", Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp), GRAY, 9.sp)
        Text("חזרה", Modifier.align(Alignment.TopStart).padding(4.dp).clickable { onBack() }, GRAY, 9.sp)
    }
}

// ==================== COLOR RUSH ====================
@Composable
fun ColorRushGame(onBack: () -> Unit) {
    val colorPairs = listOf(
        NEON_RED to "אדום", NEON_BLU to "כחול", NEON_GRN to "ירוק",
        NEON_YLW to "צהוב", NEON_PRP to "סגול", NEON_ORG to "כתום"
    )
    var score    by remember { mutableStateOf(0) }
    var lives    by remember { mutableStateOf(3) }
    var dead     by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(30) }
    var targetColor by remember { mutableStateOf(0) }
    var wordColor   by remember { mutableStateOf(1) }
    var wordIdx     by remember { mutableStateOf(2) }

    fun next() {
        targetColor = Random.nextInt(6)
        do { wordColor = Random.nextInt(6) } while (wordColor == targetColor && Random.nextBoolean())
        wordIdx = Random.nextInt(6)
    }

    LaunchedEffect(Unit) {
        next()
        while (timeLeft > 0 && !dead) { delay(1000); timeLeft-- }
        if (!dead) dead = true
    }

    if (dead) {
        GameOverScreen("צ", "צבע מהיר", score, "ניקוד", NEON_GRN, onBack) {
            dead = false; score = 0; lives = 3; timeLeft = 30; next()
        }
        return
    }

    Box(Modifier.fillMaxSize().background(BG), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), Arrangement.SpaceBetween) {
                Text(timeLeft.toString() + "s", color = if (timeLeft < 10) NEON_RED else NEON_YLW, fontSize = 14.sp, fontWeight = FontWeight.Black)
                Text(score.toString(), color = NEON_GRN, fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
            Text("הצבע של המילה:", color = GRAY, fontSize = 10.sp)
            Text(colorPairs[wordIdx].second, color = colorPairs[wordColor].first, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                colorPairs.take(3).forEachIndexed { idx, (col, _) ->
                    Box(Modifier.size(40.dp).background(col, RoundedCornerShape(8.dp))
                        .clickable {
                            if (idx == wordColor) { score++; next() }
                            else { lives--; next(); if (lives <= 0) dead = true }
                        })
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                colorPairs.drop(3).forEachIndexed { i, (col, _) ->
                    val idx = i + 3
                    Box(Modifier.size(40.dp).background(col, RoundedCornerShape(8.dp))
                        .clickable {
                            if (idx == wordColor) { score++; next() }
                            else { lives--; next(); if (lives <= 0) dead = true }
                        })
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(3) { i -> Text(if (i < lives) "v" else "x", color = if (i < lives) NEON_GRN else GRAY, fontSize = 11.sp) }
            }
        }
        Text("חזרה", Modifier.align(Alignment.TopStart).padding(4.dp).clickable { onBack() }, GRAY, 9.sp)
    }
}

// ==================== NUMBER BOMB ====================
@Composable
fun NumBombGame(onBack: () -> Unit) {
    data class NumTile(val id: Int, val value: Int, val x: Float, val y: Float, val born: Long)

    var tiles  by remember { mutableStateOf(listOf<NumTile>()) }
    var score  by remember { mutableStateOf(0) }
    var lives  by remember { mutableStateOf(3) }
    var dead   by remember { mutableStateOf(false) }
    var tileId by remember { mutableStateOf(0) }
    var frame  by remember { mutableStateOf(0) }
    var targetEven by remember { mutableStateOf(true) }

    fun reset() {
        dead = false; score = 0; lives = 3; tiles = listOf()
        frame = 0; targetEven = true
    }

    LaunchedEffect(Unit) {
        while (!dead) {
            delay(20); frame++
            tiles = tiles.map { it.copy(y = it.y + 0.007f) }.filter { it.y < 1.05f }
            val spawnEvery = maxOf(20, 60 - frame / 60)
            if (frame % spawnEvery == 0) {
                tiles = tiles + NumTile(tileId++, Random.nextInt(1, 20),
                    Random.nextFloat() * 0.75f + 0.05f, 0f, System.currentTimeMillis())
            }
            val missed = tiles.filter { it.y > 1.0f && ((it.value % 2 == 0) == targetEven) }
            if (missed.isNotEmpty()) { lives -= missed.size; if (lives <= 0) { dead = true; break } }
        }
    }

    if (dead) {
        GameOverScreen("פ", "פצצת מספרים", score, "ניקוד", NEON_PRP, onBack, ::reset)
        return
    }

    BoxWithConstraints(Modifier.fillMaxSize().background(BG)) {
        val w = maxWidth; val h = maxHeight

        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(4.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("חזרה", Modifier.clickable { onBack() }, GRAY, 9.sp)
                Text("תפוס " + (if (targetEven) "זוגי" else "אי-זוגי"), color = NEON_PRP, fontSize = 11.sp, fontWeight = FontWeight.Black)
                Text(score.toString(), color = NEON_YLW, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        tiles.forEach { t ->
            val isTarget = (t.value % 2 == 0) == targetEven
            Box(Modifier.offset(x = (w * t.x) - 20.dp, y = (h * t.y) - 16.dp)
                .size(40.dp, 32.dp)
                .background(if (isTarget) NEON_PRP.copy(alpha = 0.25f) else NEON_RED.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                .clickable {
                    if (isTarget) { tiles = tiles.filter { it.id != t.id }; score++ }
                    else { lives--; tiles = tiles.filter { it.id != t.id }; if (lives <= 0) dead = true }
                }, Alignment.Center) {
                Text(t.value.toString(), color = if (isTarget) NEON_PRP else NEON_RED.copy(alpha = 0.6f),
                    fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
        }

        Row(Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(3) { i -> Text(if (i < lives) "v" else "x", color = if (i < lives) NEON_PRP else GRAY, fontSize = 12.sp) }
        }
    }
}
