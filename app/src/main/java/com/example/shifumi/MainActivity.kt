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
            val decimalFormat = DecimalFormat("0.00")
            gameViewModel.velocity_x = event.values[0]
            gameViewModel.velocity_y = event.values[1]
            gameViewModel.velocity_z = event.values[2]
            gameViewModel.printable = decimalFormat.format(gameViewModel.velocity_x) + "x, " + decimalFormat.format(gameViewModel.velocity_x) + "y, " + decimalFormat.format(gameViewModel.velocity_x) + "z"

            var currentTime = System.currentTimeMillis()
            if ((gameViewModel.get_velocity() >= gameViewModel.shake_threshold) && (currentTime - shookLastTime > 300)) {
                if(gameViewModel.n_shake == 3) {
                    gameViewModel.n_shake = 0
                }
                gameViewModel.n_shake++
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
        Text(
            text = stringResource(R.string.shake),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp),
            style = TextStyle(fontSize = 40.sp)
        )
        Column (
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            Text(
                text = "Velocity: ${gameViewModel.get_velocity()}",
                modifier = Modifier
                    .padding(bottom = 70.dp)
            )
            Text(
                text = "Velocity coords: " + gameViewModel.printable,
                modifier = Modifier
                    .padding(bottom = 70.dp)
            )
            Text(
                text = "you shook ${gameViewModel.n_shake} times",
                modifier = Modifier
                    .padding(bottom = 70.dp)
            )
            ImageChangeOnShake(gameViewModel)
            Button(
                onClick = {
                    navController.navigate("title_screen")
                },
                modifier = Modifier
            ) {
                Text(text = stringResource(R.string.Menu_Home))
            }
        }
    }
}

@Composable
fun ImageChangeOnShake(gameViewModel: GameViewModel, modifier: Modifier = Modifier) {
    var imageId = Random.nextInt(1, 3)
    var path : Painter
    if (imageId == 1) {
        path = painterResource(id = R.drawable.ciseaux)
    } else if (imageId == 2) {
        path = painterResource(id = R.drawable.feuille)
    } else {
        path = painterResource(id = R.drawable.pierre)
    }
    if (gameViewModel.n_shake == 3) {
        Image(
            painter = path,
            contentDescription = ""
        )
    }
}