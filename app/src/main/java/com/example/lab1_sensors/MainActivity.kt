package com.example.lab1_sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.textservice.TextServicesManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.lab1_sensors.ui.theme.Lab1_SensorsTheme
import dev.ricknout.composesensors.SensorValue
import dev.ricknout.composesensors.ambienttemperature.getAmbientTemperatureSensor
import dev.ricknout.composesensors.ambienttemperature.isAmbientTemperatureSensorAvailable
import dev.ricknout.composesensors.ambienttemperature.rememberAmbientTemperatureSensorValueAsState
import dev.ricknout.composesensors.getSensorManager
import dev.ricknout.composesensors.gyroscope.getGyroscopeSensor
import dev.ricknout.composesensors.gyroscope.isGyroscopeSensorAvailable
import dev.ricknout.composesensors.gyroscope.rememberGyroscopeSensorValueAsState
import dev.ricknout.composesensors.isSensorAvailable
import dev.ricknout.composesensors.light.getLightSensor
import dev.ricknout.composesensors.light.isLightSensorAvailable
import dev.ricknout.composesensors.light.rememberLightSensorValueAsState
import dev.ricknout.composesensors.magneticfield.getMagneticFieldSensor
import dev.ricknout.composesensors.magneticfield.isMagneticFieldSensorAvailable
import dev.ricknout.composesensors.magneticfield.rememberMagneticFieldSensorValueAsState
import dev.ricknout.composesensors.pressure.getPressureSensor
import dev.ricknout.composesensors.pressure.rememberPressureSensorValueAsState
import dev.ricknout.composesensors.rememberSensorValueAsState
import kotlinx.coroutines.flow.MutableStateFlow

private var sensorManager: SensorManager? = null
private var lightSensor: Sensor? = null
private var currentLight = mutableStateOf(0f)

class MainActivity : ComponentActivity(), SensorEventListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab1_SensorsTheme {
                sensorManager = LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager
                lightSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)
                sensorManager?.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)

                Column(
                    modifier = Modifier
                        .drawBehind {
                            drawCircle(
                                color = getCurrentColor(currentLight.value)
                            )
                        }
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){

                    Text(text = getTitleByValue(currentLight.value))
                    Text(
                        text = currentLight.value.toString(),
                        color = if(getCurrentColor(currentLight.value) == Color.Black)
                                    Color.White
                                else
                                    Color.Black
                    )
                }
            }
        }
    }

    fun getCurrentColor(value: Float): Color {
        return when(value){
            0f -> Color.Black
            in 1f..10f -> Color.Gray
            in 11f..70f -> Color.White
            in 71f..2000f -> Color.Yellow.copy(alpha = 0.5f)
            in 2001f..5000f -> Color.Yellow
            else -> Color.Blue
        }
    }

    fun getTitleByValue(value: Float): String {
        return when(value){
            0F -> "Ничего не видно"
            in 1f..10f -> "Темно"
            in 11f..70f -> "Уже светлее"
            in 71f..2000f -> "Ура, видно"
            in 2001f..5000f -> "Все видно"
            else -> "Щас ослепну"
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        currentLight.value = p0!!.values[0]
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }
}