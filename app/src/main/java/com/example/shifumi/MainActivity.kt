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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shifumi.ui.theme.ShiFuMiTheme
import java.text.DecimalFormat
import kotlin.properties.Delegates
import kotlin.random.Random

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
        composable("game_screen") { GameScreen(navController, gameViewModel) } // ✅ Passer le ViewModel
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
fun GameScreen(navController: NavController, gameViewModel: GameViewModel) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column (
            modifier = Modifier.align(Alignment.Center)
        ) {
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
        modifier = Modifier,
        painter = path,
        contentDescription = ""
    )
}


@Composable
fun ResultScreen(navController: NavController, gameViewModel: GameViewModel) {
    Column (
        modifier = Modifier
    ) {
        SymbolImage(Modifier, gameViewModel.symbolBot)
        Text(text = "bot", modifier = Modifier.align(Alignment.CenterHorizontally))
        Text(text = "VS", modifier = Modifier.align(Alignment.CenterHorizontally))
        Text(text = "you", modifier = Modifier.align(Alignment.CenterHorizontally))
        SymbolImage(Modifier, gameViewModel.symbolPlayer)
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