package com.tefillin.shalom

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

const val SERVER_URL = "https://whatsapp-watch-server.onrender.com"

val WA_GREEN   = Color(0xFF25D366)
val BG_COLOR   = Color(0xFF0A0A0A)
val CARD_COLOR = Color(0xFF1A1A1A)
val WHITE      = Color(0xFFFFFFFF)
val GRAY       = Color(0xFF8696A0)

sealed class AppScreen {
    object Loading : AppScreen()
    object QR : AppScreen()
    data class Messages(val msgs: List<MsgItem>) : AppScreen()
    data class Reply(val number: String, val name: String) : AppScreen()
}

data class MsgItem(val from: String, val number: String, val body: String, val time: String)

val QUICK_REPLIES = listOf("👍", "תודה!", "בסדר", "רגע", "לא עכשיו", "אתקשר אליך")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { WhatsAppWatchApp() }
    }
}

suspend fun fetchJson(path: String): JSONObject? = withContext(Dispatchers.IO) {
    try {
        val url = URL("$SERVER_URL$path")
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 8000
        conn.readTimeout = 8000
        val text = conn.inputStream.bufferedReader().readText()
        JSONObject(text)
    } catch (e: Exception) { null }
}

suspend fun postJson(path: String, body: JSONObject): Boolean = withContext(Dispatchers.IO) {
    try {
        val url = URL("$SERVER_URL$path")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true
        conn.connectTimeout = 8000
        conn.outputStream.write(body.toString().toByteArray())
        conn.responseCode == 200
    } catch (e: Exception) { false }
}

@Composable
fun WhatsAppWatchApp() {
    var screen by remember { mutableStateOf<AppScreen>(AppScreen.Loading) }

    LaunchedEffect(Unit) {
        while (true) {
            if (screen is AppScreen.Reply) { delay(3000); continue }
            val status = fetchJson("/status")
            if (status != null) {
                if (status.optBoolean("ready", false)) {
                    val msgs = fetchJson("/messages")
                    val list = mutableListOf<MsgItem>()
                    if (msgs != null) {
                        val arr = msgs.optJSONArray("messages")
                        if (arr != null) {
                            for (i in 0 until arr.length()) {
                                val m = arr.getJSONObject(i)
                                list.add(MsgItem(
                                    from   = m.optString("from", "?"),
                                    number = m.optString("number", ""),
                                    body   = m.optString("body", ""),
                                    time   = m.optString("time", "")
                                ))
                            }
                        }
                    }
                    screen = AppScreen.Messages(list)
                } else {
                    screen = AppScreen.QR
                }
            } else {
                screen = AppScreen.Loading
            }
            delay(3000)
        }
    }

    when (val s = screen) {
        AppScreen.Loading -> LoadingScreen()
        AppScreen.QR      -> QRScreen()
        is AppScreen.Messages -> MessagesScreen(s.msgs) { number, name ->
            screen = AppScreen.Reply(number, name)
        }
        is AppScreen.Reply -> ReplyScreen(s.number, s.name) {
            screen = AppScreen.Loading
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(Modifier.fillMaxSize().background(BG_COLOR), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(indicatorColor = WA_GREEN, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text("מתחבר לשרת...", color = GRAY, fontSize = 12.sp)
        }
    }
}

@Composable
fun QRScreen() {
    var qrBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(Unit) {
        while (qrBitmap == null) {
            val data = fetchJson("/qr")
            if (data != null && data.has("qr")) {
                val base64 = data.getString("qr").removePrefix("data:image/png;base64,")
                val bytes = Base64.decode(base64, Base64.DEFAULT)
                qrBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
            delay(2000)
        }
    }

    Box(Modifier.fillMaxSize().background(BG_COLOR), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("סרוק עם WhatsApp", color = WA_GREEN, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            val bmp = qrBitmap
            if (bmp != null) {
                Image(bitmap = bmp.asImageBitmap(), contentDescription = "QR", modifier = Modifier.size(130.dp))
            } else {
                CircularProgressIndicator(indicatorColor = WA_GREEN, modifier = Modifier.size(40.dp))
                Spacer(Modifier.height(4.dp))
                Text("טוען QR...", color = GRAY, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun MessagesScreen(msgs: List<MsgItem>, onReply: (String, String) -> Unit) {
    Box(Modifier.fillMaxSize().background(BG_COLOR)) {
        if (msgs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✅", fontSize = 28.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("מחובר!", color = WA_GREEN, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("אין הודעות חדשות", color = GRAY, fontSize = 11.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                item {
                    Text("💬 הודעות", color = WA_GREEN, fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                        textAlign = TextAlign.Center)
                }
                items(msgs) { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CARD_COLOR, shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                            .clickable { onReply(msg.number, msg.from) }
                            .padding(8.dp)
                    ) {
                        Column {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(msg.from, color = WA_GREEN, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(msg.time, color = GRAY, fontSize = 10.sp)
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(msg.body, color = WHITE, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Spacer(Modifier.height(4.dp))
                            Text("לחץ להשב", color = GRAY, fontSize = 9.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReplyScreen(number: String, name: String, onDone: () -> Unit) {
    var sent by remember { mutableStateOf(false) }

    if (sent) {
        Box(Modifier.fillMaxSize().background(BG_COLOR), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("✅", fontSize = 30.sp)
                Text("נשלח!", color = WA_GREEN, fontSize = 14.sp)
            }
        }
        LaunchedEffect(Unit) { delay(1500); onDone() }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                Text("השב ל-$name", color = WA_GREEN, fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
            items(QUICK_REPLIES) { reply ->
                Button(
                    onClick = {
                        sent = true
                        val body = JSONObject().put("number", number).put("text", reply)
                    },
                    modifier = Modifier.fillMaxWidth().height(34.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = CARD_COLOR)
                ) {
                    Text(reply, color = WHITE, fontSize = 12.sp)
                }
            }
            item {
                Button(onClick = onDone,
                    modifier = Modifier.fillMaxWidth().height(34.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF333333))
                ) {
                    Text("ביטול", color = GRAY, fontSize = 12.sp)
                }
            }
        }
    }
}
