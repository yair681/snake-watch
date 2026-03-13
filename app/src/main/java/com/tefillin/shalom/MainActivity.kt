package com.tefillin.shalom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

// --- צבעים ---
val BG       = Color(0xFF0A0A1A)
val GRID_COL = Color(0xFF1A1A2E)
val HEAD_COL = Color(0xFF00E676)
val BODY_COL = Color(0xFF00897B)
val FOOD_COL = Color(0xFFFF5252)
val TEXT_COL = Color(0xFFFFFFFF)
val SCORE_COL= Color(0xFFFFD700)

// --- קונסטנטות ---
const val COLS = 14
const val ROWS = 14
const val TICK_MS = 200L

enum class Dir { UP, DOWN, LEFT, RIGHT }

data class Pt(val x: Int, val y: Int)

sealed class Screen { object Menu : Screen(); object Play : Screen(); object Over : Screen() }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SnakeApp() }
    }
}

@Composable
fun SnakeApp() {
    var screen by remember { mutableStateOf<Screen>(Screen.Menu) }
    var finalScore by remember { mutableStateOf(0) }

    when (screen) {
        Screen.Menu -> MenuScreen { screen = Screen.Play }
        Screen.Play -> GameScreen(
            onGameOver = { score ->
                finalScore = score
                screen = Screen.Over
            }
        )
        Screen.Over -> GameOverScreen(finalScore) {
            screen = Screen.Play
        }
    }
}

// ===================== מסך תפריט =====================
@Composable
fun MenuScreen(onStart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BG),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("🐍", fontSize = 36.sp)
            Text("SNAKE", color = HEAD_COL, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("החלק להזזה", color = TEXT_COL.copy(alpha = 0.6f), fontSize = 11.sp)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onStart,
                modifier = Modifier.size(width = 100.dp, height = 36.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = HEAD_COL)
            ) {
                Text("שחק!", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ===================== מסך משחק =====================
@Composable
fun GameScreen(onGameOver: (Int) -> Unit) {
    // מצב המשחק
    var snake by remember { mutableStateOf(listOf(Pt(7, 7), Pt(6, 7), Pt(5, 7))) }
    var dir   by remember { mutableStateOf(Dir.RIGHT) }
    var nextDir by remember { mutableStateOf(Dir.RIGHT) }
    var food  by remember { mutableStateOf(Pt(10, 7)) }
    var score by remember { mutableStateOf(0) }
    var alive by remember { mutableStateOf(true) }

    fun spawnFood(s: List<Pt>): Pt {
        var p: Pt
        do { p = Pt(Random.nextInt(COLS), Random.nextInt(ROWS)) } while (s.contains(p))
        return p
    }

    // לולאת המשחק
    LaunchedEffect(alive) {
        while (alive) {
            delay(TICK_MS)
            dir = nextDir
            val head = snake.first()
            val newHead = when (dir) {
                Dir.UP    -> Pt(head.x, (head.y - 1 + ROWS) % ROWS)
                Dir.DOWN  -> Pt(head.x, (head.y + 1) % ROWS)
                Dir.LEFT  -> Pt((head.x - 1 + COLS) % COLS, head.y)
                Dir.RIGHT -> Pt((head.x + 1) % COLS, head.y)
            }
            // פגיעה בעצמו
            if (snake.contains(newHead)) {
                alive = false
                onGameOver(score)
                break
            }
            val atFood = newHead == food
            val newSnake = if (atFood) {
                listOf(newHead) + snake
            } else {
                listOf(newHead) + snake.dropLast(1)
            }
            if (atFood) {
                score += 10
                food = spawnFood(newSnake)
            }
            snake = newSnake
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BG)
            .pointerInput(Unit) {
                detectDragGestures { _, drag ->
                    val dx = drag.x; val dy = drag.y
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
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cellW = w / COLS
            val cellH = h / ROWS

            // רשת
            for (r in 0 until ROWS) for (c in 0 until COLS) {
                drawRect(
                    color = if ((r + c) % 2 == 0) GRID_COL else BG,
                    topLeft = Offset(c * cellW, r * cellH),
                    size = Size(cellW, cellH)
                )
            }

            // אוכל (עיגול אדום)
            val fx = food.x * cellW + cellW / 2
            val fy = food.y * cellH + cellH / 2
            drawCircle(FOOD_COL, radius = cellW * 0.4f, center = Offset(fx, fy))

            // נחש
            snake.forEachIndexed { i, pt ->
                val col = if (i == 0) HEAD_COL else BODY_COL
                val pad = if (i == 0) 1.5f else 2.5f
                drawRoundRect(
                    color = col,
                    topLeft = Offset(pt.x * cellW + pad, pt.y * cellH + pad),
                    size = Size(cellW - pad * 2, cellH - pad * 2),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
                )
            }
        }

        // ניקוד
        Text(
            "$score",
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp),
            color = SCORE_COL,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ===================== מסך סיום =====================
@Composable
fun GameOverScreen(score: Int, onRestart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BG),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("💀", fontSize = 30.sp)
            Text("נגמר!", color = FOOD_COL, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("ניקוד: $score", color = SCORE_COL, fontSize = 16.sp)
            Spacer(Modifier.height(6.dp))
            Button(
                onClick = onRestart,
                modifier = Modifier.size(width = 100.dp, height = 36.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = HEAD_COL)
            ) {
                Text("שוב!", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
