package alreyesh.android.scanmarketclient.Camara

import alreyesh.android.scanmarketclient.Activities.MainActivity

import alreyesh.android.scanmarketclient.databinding.ActivityCameraBinding
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.nnapi.NnApiDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.random.Random


class CamaraActivity : AppCompatActivity(){
    private lateinit var activityCameraBinding: ActivityCameraBinding

    private lateinit var bitmapBuffer: Bitmap

    private val executor = Executors.newSingleThreadExecutor()
    private val permissions = listOf(Manifest.permission.CAMERA)
    private val permissionsRequestCode = Random.nextInt(0, 10000)

    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private val isFrontFacing get() = lensFacing == CameraSelector.LENS_FACING_FRONT

    private var pauseAnalysis = false
    private var imageRotationDegrees: Int = 0
    private val tfImageBuffer = TensorImage(DataType.UINT8)

    private val tfImageProcessor by lazy {
        val cropSize = minOf(bitmapBuffer.width, bitmapBuffer.height)
        ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(ResizeOp(
                tfInputSize.height, tfInputSize.width, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(Rot90Op(-imageRotationDegrees / 90))
            .add(NormalizeOp(0f, 1f))
            .build()
    }

    private val nnApiDelegate by lazy  {
        NnApiDelegate()
    }

    private val tflite by lazy {
        Interpreter(
            FileUtil.loadMappedFile(this, MODEL_PATH),
            Interpreter.Options().addDelegate(nnApiDelegate))
    }
    private val detector by lazy {
        ObjectDetectionHelper(
            tflite,
            FileUtil.loadLabels(this, LABELS_PATH)
        )
    }

    private val tfInputSize by lazy {
        val inputIndex = 0
        val inputShape = tflite.getInputTensor(inputIndex).shape()
        Size(inputShape[2], inputShape[1]) // Order of axis is: {1, height, width, 3}
    }

    private var result:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCameraBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(activityCameraBinding.root)

        activityCameraBinding.cameraCaptureButton.setOnClickListener {

            // Disable all camera controls
            it.isEnabled = false

            if (pauseAnalysis) {
                // If image analysis is in paused state, resume it
                pauseAnalysis = false
                activityCameraBinding.imagePredicted.visibility = View.GONE

            } else {
                // Otherwise, pause image analysis and freeze image
                pauseAnalysis = true
                val matrix = Matrix().apply {
                    postRotate(imageRotationDegrees.toFloat())
                    if (isFrontFacing) postScale(-1f, 1f)
                }
                val uprightImage = Bitmap.createBitmap(
                    bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height, matrix, true)
                activityCameraBinding.imagePredicted.setImageBitmap(uprightImage)
                activityCameraBinding.imagePredicted.visibility = View.VISIBLE

                if(result !=null){

                    val db = Firebase.firestore
                    var firestore: FirebaseFirestore
                    firestore = FirebaseFirestore.getInstance()
                    firestore.collection("productos").whereEqualTo("nombre",result)
                        .get()
                        .addOnSuccessListener {
                            if(it.documents.size >0){

                                //Toast.makeText(this, "Producto en la base de datos:  ${it.documents.get(0).data?.get("nombre") }  ", Toast.LENGTH_SHORT).show()
                                val sharedPreferences: SharedPreferences = this.getSharedPreferences("Preferences",Context.MODE_PRIVATE)
                                val editor: SharedPreferences.Editor =  sharedPreferences.edit()
                                val codigo:String?= it.documents.get(0).get("codigo").toString()
                                val imagen:String?= it.documents.get(0).get("imagen").toString()
                                val precio:String?= it.documents.get(0).get("precio").toString()


                                if(codigo !=null){
                                    editor.putString("productcamera",result)
                                    editor.putString("codigocamera",codigo)
                                    editor.putString("imagencamera",imagen)
                                    editor.putString("preciocamera",precio)
                                    editor.commit()
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    val bundle = Bundle()
                                    bundle.putString("cameras","productodecamera")

                                    intent.putExtras(bundle)

                                    startActivity(intent)
                                }

                                /*
                                val fragment  = RecommentFragment()
                                val fragmentManager = supportFragmentManager
                                fragmentManager
                                    .beginTransaction().replace(R.id.content_frame, fragment)
                                    .commit()
                                    */
                            }else{
                                Toast.makeText(this, "No existe en la base de datos", Toast.LENGTH_SHORT).show()

                            }
                           }
                        .addOnFailureListener{
                            it.printStackTrace()
                            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                        }


                 /*   if(producto !=null){
                        Toast.makeText(this,"Existe en la bd:  "+ result, Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"NO Existe en la bd:"+ result, Toast.LENGTH_SHORT).show()
                    }
                    */
                }else{

                }




            }

            // Re-enable camera controls
            it.isEnabled = true
        }
    }

    override fun onDestroy() {

        // Terminate all outstanding analyzing jobs (if there is any).
        executor.apply {
            shutdown()
            awaitTermination(1000, TimeUnit.MILLISECONDS)
        }

        // Release TFLite resources.
        tflite.close()
        nnApiDelegate.close()

        super.onDestroy()
    }

    /** Declare and bind preview and analysis use cases */
    @SuppressLint("UnsafeExperimentalUsageError")
    private fun bindCameraUseCases() = activityCameraBinding.viewFinder.post {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener ({

            // Camera provider is now guaranteed to be available
            val cameraProvider = cameraProviderFuture.get()

            // Set up the view finder use case to display camera preview
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(activityCameraBinding.viewFinder.display.rotation)
                .build()

            // Set up the image analysis use case which will process frames in real time
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(activityCameraBinding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()

            var frameCounter = 0
            var lastFpsTimestamp = System.currentTimeMillis()

            imageAnalysis.setAnalyzer(executor, ImageAnalysis.Analyzer { image ->
                if (!::bitmapBuffer.isInitialized) {
                    // The image rotation and RGB image buffer are initialized only once
                    // the analyzer has started running
                    imageRotationDegrees = image.imageInfo.rotationDegrees
                    bitmapBuffer = Bitmap.createBitmap(
                        image.width, image.height, Bitmap.Config.ARGB_8888)
                }

                // Early exit: image analysis is in paused state
                if (pauseAnalysis) {
                    image.close()
                    return@Analyzer
                }

                // Copy out RGB bits to our shared buffer
                image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer)  }

                // Process the image in Tensorflow
                val tfImage =  tfImageProcessor.process(tfImageBuffer.apply { load(bitmapBuffer) })

                // Perform the object detection for the current frame
                val predictions = detector.predict(tfImage)

                // Report only the top prediction
                reportPrediction(predictions.maxByOrNull { it.score })

                // Compute the FPS of the entire pipeline
                val frameCount = 10
                if (++frameCounter % frameCount == 0) {
                    frameCounter = 0
                    val now = System.currentTimeMillis()
                    val delta = now - lastFpsTimestamp
                    val fps = 1000 * frameCount.toFloat() / delta
                    Log.d(TAG, "FPS: ${"%.02f".format(fps)} with tensorSize: ${tfImage.width} x ${tfImage.height}")
                    lastFpsTimestamp = now
                }
            })

            // Create a new camera selector each time, enforcing lens facing
            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

            // Apply declared configs to CameraX using the same lifecycle owner
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this as LifecycleOwner, cameraSelector, preview, imageAnalysis)

            // Use the camera object to link our preview use case with the view
            preview.setSurfaceProvider(activityCameraBinding.viewFinder.surfaceProvider)

        }, ContextCompat.getMainExecutor(this))
    }

    private fun reportPrediction(
        prediction: ObjectDetectionHelper.ObjectPrediction?
    ) = activityCameraBinding.viewFinder.post {

        // Early exit: if prediction is not good enough, don't report it
        if (prediction == null || prediction.score < ACCURACY_THRESHOLD) {
            activityCameraBinding.boxPrediction.visibility = View.GONE
            activityCameraBinding.textPrediction.visibility = View.GONE
            return@post
        }

        // Location has to be mapped to our local coordinates
        val location = mapOutputCoordinates(prediction.location)

        // Update the text and UI
        activityCameraBinding.textPrediction.text = "${"%.2f".format(prediction.score)} ${prediction.label}"
        (activityCameraBinding.boxPrediction.layoutParams as ViewGroup.MarginLayoutParams).apply {
            topMargin = location.top.toInt()
            leftMargin = location.left.toInt()
            width = min(activityCameraBinding.viewFinder.width, location.right.toInt() - location.left.toInt())
            height = min(activityCameraBinding.viewFinder.height, location.bottom.toInt() - location.top.toInt())
        }
        result=prediction.label
        // Make sure all UI elements are visible
        activityCameraBinding.boxPrediction.visibility = View.VISIBLE
        activityCameraBinding.textPrediction.visibility = View.VISIBLE
    }

    /**
     * Helper function used to map the coordinates for objects coming out of
     * the model into the coordinates that the user sees on the screen.
     */
    private fun mapOutputCoordinates(location: RectF): RectF {

        // Step 1: map location to the preview coordinates
        val previewLocation = RectF(
            location.left * activityCameraBinding.viewFinder.width,
            location.top * activityCameraBinding.viewFinder.height,
            location.right * activityCameraBinding.viewFinder.width,
            location.bottom * activityCameraBinding.viewFinder.height
        )

        // Step 2: compensate for camera sensor orientation and mirroring
        val isFrontFacing = lensFacing == CameraSelector.LENS_FACING_FRONT
        val correctedLocation = if (isFrontFacing) {
            RectF(
                activityCameraBinding.viewFinder.width - previewLocation.right,
                previewLocation.top,
                activityCameraBinding.viewFinder.width - previewLocation.left,
                previewLocation.bottom)
        } else {
            previewLocation
        }

        // Step 3: compensate for 1:1 to 4:3 aspect ratio conversion + small margin
        val margin = 0.1f
        val requestedRatio = 4f / 3f
        val midX = (correctedLocation.left + correctedLocation.right) / 2f
        val midY = (correctedLocation.top + correctedLocation.bottom) / 2f
        return if (activityCameraBinding.viewFinder.width < activityCameraBinding.viewFinder.height) {
            RectF(
                midX - (1f + margin) * requestedRatio * correctedLocation.width() / 2f,
                midY - (1f - margin) * correctedLocation.height() / 2f,
                midX + (1f + margin) * requestedRatio * correctedLocation.width() / 2f,
                midY + (1f - margin) * correctedLocation.height() / 2f
            )
        } else {
            RectF(
                midX - (1f - margin) * correctedLocation.width() / 2f,
                midY - (1f + margin) * requestedRatio * correctedLocation.height() / 2f,
                midX + (1f - margin) * correctedLocation.width() / 2f,
                midY + (1f + margin) * requestedRatio * correctedLocation.height() / 2f
            )
        }
    }

    override fun onResume() {
        super.onResume()

        // Request permissions each time the app resumes, since they can be revoked at any time
        if (!hasPermissions(this)) {
            ActivityCompat.requestPermissions(
                this, permissions.toTypedArray(), permissionsRequestCode)
        } else {
            bindCameraUseCases()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionsRequestCode && hasPermissions(this)) {
            bindCameraUseCases()
        } else {
            finish() // If we don't have the required permissions, we can't run
        }
    }

    /** Convenience method used to check if all permissions required by this app are granted */
    private fun hasPermissions(context: Context) = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val TAG = CamaraActivity::class.java.simpleName

        private const val ACCURACY_THRESHOLD = 0.5f
        private const val MODEL_PATH = "coco_ssd_mobilenet_v1_1.0_quant.tflite"
        private const val LABELS_PATH = "coco_ssd_mobilenet_v1_1.0_labels.txt"
    }
}