package com.example.steppy

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.steppy.ui.theme.SteppyTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlin.math.roundToInt

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SteppyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SteppyApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SteppyApp() {
    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    var steps by remember {
        mutableStateOf(0)
    }

    var activityPermissionState: PermissionState? = null

    if(Build.VERSION.SDK_INT >= 29) {
        activityPermissionState = rememberPermissionState(permission = Manifest.permission.ACTIVITY_RECOGNITION)
        LaunchedEffect(key1 = activityPermissionState.status.isGranted) {
            if(!activityPermissionState.status.isGranted) {
                activityPermissionState.launchPermissionRequest()
            }
        }
    }


    if(stepSensor == null) {
        Toast.makeText(context, "No sensor found", Toast.LENGTH_SHORT).show()
    }
    else if(activityPermissionState == null || activityPermissionState.status.isGranted) {
        sensorManager.registerListener(object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                steps = event!!.values[0].roundToInt()
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }

        }, stepSensor, SensorManager.SENSOR_DELAY_UI)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Your steps")
        ElevatedCard(
            shape = CircleShape,
            modifier = Modifier
                .size(250.dp)
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                Text(text = "$steps", modifier = Modifier.fillMaxSize(), fontSize = 50.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SteppyTheme {
        SteppyApp()
    }
}