package com.gala.krobot.platform

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gala.krobot.createRobotController
import com.gala.krobot.global.globalRobotController
import com.gala.krobot.ui.theme.KrobotTheme
import com.gala.maze.common.arena.CreateRobotControllerHolder
import com.gala.maze.platform.arena.Maze
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var createRobotControllerHolder: CreateRobotControllerHolder

//    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(applicationContext)

        val robotController = createRobotController()
        globalRobotController = robotController
        createRobotControllerHolder.instance = { robotController }

        enableEdgeToEdge()
        setContent {
            KrobotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Maze()
                        Button(
                            modifier = Modifier
                                .align(Alignment.BottomCenter),
                            onClick = {
                                photoProgram()
                            },
                        ) {
                            Text("Фото программы")
                        }
                    }
                }
            }
        }
    }

    private fun photoProgram() {
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
            .putExtra(MediaStore.EXTRA_OUTPUT, 1)
        startActivityForResult(intent, CAMERA_PICTURE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_PICTURE_REQUEST) {
            val image = data!!.extras!!["data"] as Bitmap

            val options = FirebaseVisionCloudTextRecognizerOptions.Builder()
                .setLanguageHints(listOf("en"))
                .build()
            val textRecognizer = FirebaseVision.getInstance().getCloudTextRecognizer(options)

            val firebaseImage = FirebaseVisionImage.fromBitmap(image)

            textRecognizer.processImage(firebaseImage)
                .addOnSuccessListener { result ->
                    Log.i(TAG, result.text)
                }
                .addOnFailureListener { error ->
                    Log.e(TAG, "message", error)
                }

//            textRecognizer.process(image, Detector.TYPE_TEXT_RECOGNITION)
//                .addOnSuccessListener { result ->
//                    println(result)
//                }
        }
    }

    private companion object {
        private const val TAG = "MainActivity"

        private const val CAMERA_PICTURE_REQUEST = 1
    }
}
