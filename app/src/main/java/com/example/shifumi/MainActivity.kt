package com.example.shifumi

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shifumi.ui.theme.ShiFuMiTheme

class MainActivity : ComponentActivity(), SensorEventListener {

    lateinit var sensorManager : SensorManager
    private lateinit var gameViewModel: GameViewModel
    private lateinit var bot : Bot
    private var shookLastTime : Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        gameViewModel = GameViewModel()
        bot = Bot()
        setUpSensor()
        setContent {
            ShiFuMiTheme {
                App(gameViewModel, bot)
            }
        }
    }

    private fun setUpSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also{
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            gameViewModel.velocityX = event.values[0]
            gameViewModel.velocityY = event.values[1]
            gameViewModel.velocityZ = event.values[2]

            val currentTime = System.currentTimeMillis()
            if ((gameViewModel.getVelocity() >= gameViewModel.shakeThreshold) && (currentTime - shookLastTime > 300)) {
                if(gameViewModel.nShake >= 3) {
                    gameViewModel.nShake = 0
                }
                gameViewModel.nShake++
                gameViewModel.countDownString = when (gameViewModel.nShake) {
                    0 -> "3"
                    1 -> "2"
                    2 -> "1"
                    else -> "0"
                }
                shookLastTime = currentTime
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }
}

@Composable
fun App(gameViewModel: GameViewModel, bot: Bot, navController: NavHostController = rememberNavController()) {
    var model = gameViewModel
    var botPlayer = bot
    Box (modifier = Modifier.fillMaxSize()){
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        NavHost(navController = navController, startDestination = "title_screen") {
            composable("title_screen") { TitleScreen(navController) }
            composable("move_choice_screen") { SelectMoveScreen(navController, model) }
            composable("difficulty_screen") { SelectDifficultyScreen(navController, model, bot) }
            composable("game_screen") { GameScreen(navController, model, bot) }
            composable("result_screen") { ResultScreen(navController, model, bot) }
            composable("scores_screen") { ScoresScreen(navController, model) }
        }
    }
}

@Composable
fun TitleScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.title_title_screen),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp),
            style = TextStyle(fontSize = 50.sp, fontWeight = FontWeight.Bold)
        )
        Button(
            onClick = {
                navController.navigate("move_choice_screen")
            },
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            Text(text = stringResource(R.string.select_move))
        }
        Button(
            onClick = {
                navController.navigate("scores_screen")
            },
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 100.dp)
        ) {
            Text(text = stringResource(R.string.scores))
        }
    }
}

@Composable
fun SelectMoveScreen(navController: NavController, gameViewModel: GameViewModel) {

    val images = listOf(
        R.drawable.pierre,
        R.drawable.feuille,
        R.drawable.ciseaux
    )

    var selectedImageIndex by remember { mutableStateOf<Int?>(null) }

    var isButtonEnabled = selectedImageIndex != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (
            modifier = Modifier.fillMaxWidth()
                .padding(top = 50.dp)
        ) {
            images.forEachIndexed { index, imageRes ->
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Image $index",
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(100.dp)
                        .clickable {
                            selectedImageIndex = if (selectedImageIndex == index) {
                                null
                            } else {
                                index
                            }
                        }
                        .border(
                            width = 2.dp,
                            color = if (selectedImageIndex == index) Color.Blue else Color.Gray,
                            shape = RoundedCornerShape(20.dp)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                gameViewModel.symbolPlayer = when(selectedImageIndex) {
                    0 -> Symbol.ROCK
                    1 -> Symbol.PAPER
                    else -> Symbol.SCISSORS
                }
                navController.navigate("difficulty_screen")
            },
            enabled = isButtonEnabled,
            modifier = Modifier
        ) {
            Text(text = stringResource(R.string.choixdiff))
        }
    }
}

@Composable
fun SelectDifficultyScreen(navController: NavController, gameViewModel: GameViewModel, bot: Bot) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.select_difficulty),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp),
            style = TextStyle(fontSize = 50.sp)
        )
        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Button(
                onClick = {
                    bot.setDiffculty(Difficulty.EASY)
                    gameViewModel.symbolBot = bot.play(gameViewModel.symbolPlayer, bot.lastWinState)
                    navController.navigate("game_screen")
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.easy))
            }
            Button(
                onClick = {
                    bot.setDiffculty(Difficulty.HARD)
                    gameViewModel.symbolBot = bot.play(gameViewModel.symbolPlayer, bot.lastWinState)
                    navController.navigate("game_screen")
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.hard))
            }
            Button(
                onClick = {
                    bot.setDiffculty(Difficulty.INVINCIBLE)
                    gameViewModel.symbolBot = bot.play(gameViewModel.symbolPlayer, bot.lastWinState)
                    navController.navigate("game_screen")
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.invincible))
            }
        }
    }
}

@Composable
fun GameScreen(navController: NavController, gameViewModel: GameViewModel, bot: Bot) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column (
            modifier = Modifier.align(Alignment.Center)
        ) {
            when (bot.difficulty) {
                Difficulty.EASY -> Text("Difficulty: " + stringResource(R.string.easy))
                Difficulty.HARD -> Text("Difficulty: " + stringResource(R.string.hard))
                Difficulty.INVINCIBLE -> Text("Difficulty: " + stringResource(R.string.invincible))
            }
            Text(
                text = stringResource(R.string.shake),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = TextStyle(fontSize = 50.sp)
            )
            Text(
                text = gameViewModel.countDownString,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = TextStyle(fontSize = 25.sp)
            )
            Button(
                onClick = {
                    navController.navigate("title_screen")
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.Menu_Home))
            }
            if (gameViewModel.nShake >= 3) {
                val lastPlayerSymbol = gameViewModel.symbolPlayer
                val lastBotSymbol = gameViewModel.symbolBot
                gameViewModel.symbolPlayer = gameViewModel.randomSymbol()
                gameViewModel.symbolBot = bot.play(
                    gameViewModel.symbolPlayer,
                    gameViewModel.getWinner(lastPlayerSymbol, lastBotSymbol)
                )
                navController.navigate("result_screen")
            }
        }
    }
}

@Composable
fun SymbolImage(modifier: Modifier = Modifier, symbol: Symbol) {
    val path = when (symbol) {
        Symbol.SCISSORS -> painterResource(id = R.drawable.ciseaux)
        Symbol.PAPER -> painterResource(id = R.drawable.feuille)
        else -> painterResource(id = R.drawable.pierre)
    }

    Image(
        modifier = Modifier.fillMaxSize(0.5f),
        painter = path,
        contentDescription = ""
    )
}


@Composable
fun ResultScreen(navController: NavController, gameViewModel: GameViewModel, bot: Bot) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        SymbolImage(modifier = Modifier, symbol = gameViewModel.symbolBot)
        Text(text = "bot", modifier = Modifier.align(Alignment.CenterHorizontally), style = TextStyle(fontSize = 25.sp))
        Text(text = "VS", modifier = Modifier.align(Alignment.CenterHorizontally), style = TextStyle(fontSize = 25.sp))
        Text(text = "you", modifier = Modifier.align(Alignment.CenterHorizontally), style = TextStyle(fontSize = 25.sp))
        SymbolImage(modifier = Modifier, symbol = gameViewModel.symbolPlayer)
        Spacer(modifier = Modifier.height(15.dp))
        Button(
            onClick = {
                gameViewModel.nShake = 0
                gameViewModel.countDownString = "3"
                navController.navigate("move_choice_screen")
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.rejouer))
        }
        Button(
            onClick = {
                when (gameViewModel.getWinner(gameViewModel.symbolPlayer, gameViewModel.symbolBot)) {
                    WinState.PLAYER1 -> gameViewModel.scoreJoueur += 1
                    WinState.PLAYER2 -> gameViewModel.scoreOrdi += 1
                    else -> null
                }
                navController.navigate("title_screen")
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.Menu_Home))
        }
        bot.lastMove = gameViewModel.symbolBot
    }
}

@Composable
fun ScoresScreen(navController: NavController, gameViewModel: GameViewModel) {
    var scoreJoueur = gameViewModel.scoreJoueur
    var scoreOrdi = gameViewModel.scoreOrdi
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Shifoumi", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))

        // Affichage des scores
        Row {
            Text(text = "Vous: $scoreJoueur", modifier = Modifier.padding(end = 16.dp))
            Text(text = "Ordinateur: $scoreOrdi")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                navController.navigate("title_screen")
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.Menu_Home))
        }
    }
}