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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
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
    var shookLastTime : Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        gameViewModel = GameViewModel()
        setUpSensor()
        setContent {
            ShiFuMiTheme {
                App(gameViewModel)
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

            var currentTime = System.currentTimeMillis()
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
                gameViewModel.symbolPlayer = gameViewModel.randomSymbol()
                gameViewModel.symbolBot = gameViewModel.randomSymbol()
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
fun App(gameViewModel: GameViewModel, navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "title_screen") {
        composable("title_screen") { TitleScreen(navController) }
        composable("difficulty_screen") { SelectDifficultyScreen(navController, gameViewModel) }
        composable("game_screen") { GameScreen(navController, gameViewModel) }
        composable("result_screen") { ResultScreen(navController, gameViewModel) }
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
                .padding(top = 20.dp),
            style = TextStyle(fontSize = 50.sp)
        )
        Button(
            onClick = {
                navController.navigate("game_screen")
            },
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            Text(text = stringResource(R.string.jouer))
        }
    }
}

@Composable
fun SelectDifficultyScreen(navController: NavController, gameViewModel: GameViewModel) {
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
                    gameViewModel.bot.setDiffculty(Difficulty.EASY)
                    navController.navigate("game_screen")
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.easy))
            }
            Button(
                onClick = {
                    gameViewModel.bot.setDiffculty(Difficulty.HARD)
                    navController.navigate("game_screen")
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.hard))
            }
            Button(
                onClick = {
                    gameViewModel.bot.setDiffculty(Difficulty.INVINCIBLE)
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
fun GameScreen(navController: NavController, gameViewModel: GameViewModel) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column (
            modifier = Modifier.align(Alignment.Center)
        ) {
            when (gameViewModel.bot.getDiffculty()) {
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
                    gameViewModel.reset()
                    navController.navigate("title_screen")
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.Menu_Home))
            }
            if (gameViewModel.nShake >= 3) {
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
fun ResultScreen(navController: NavController, gameViewModel: GameViewModel) {
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
                gameViewModel.reset()
                navController.navigate("game_screen")
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.rejouer))
        }
        Button(
            onClick = {
                gameViewModel.reset()
                navController.navigate("title_screen")
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.Menu_Home))
        }
    }
}