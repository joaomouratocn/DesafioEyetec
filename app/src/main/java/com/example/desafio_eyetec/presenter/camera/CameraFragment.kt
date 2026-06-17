package com.example.desafio_eyetec.presenter.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.desafio_eyetec.R
import com.example.desafio_eyetec.databinding.FragmentCameraBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {
    private val binding by lazy { FragmentCameraBinding.inflate(layoutInflater) }
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().popBackStack()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            activityResultLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.btnCapture.setOnClickListener { takePhoto() }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.viewFinder.surfaceProvider
            }

            imageCapture = ImageCapture.Builder().build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, FaceAnalyzer { feedback ->
                        activity?.runOnUiThread {
                            updateUI(feedback)
                        }
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e("CameraFragment", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun updateUI(feedback: FaceFeedback) {
        when (feedback) {
            FaceFeedback.NONE -> {
                binding.txtGuidance.text = getString(R.string.position_face_center)
                binding.faceOverlay.setBackgroundResource(R.drawable.face_guidance_border)
                binding.btnCapture.isEnabled = false
            }
            FaceFeedback.TOO_FAR -> {
                binding.txtGuidance.text = getString(R.string.face_too_far)
                binding.faceOverlay.setBackgroundResource(R.drawable.face_guidance_border)
                binding.btnCapture.isEnabled = false
            }
            FaceFeedback.TOO_CLOSE -> {
                binding.txtGuidance.text = getString(R.string.face_too_close)
                binding.faceOverlay.setBackgroundResource(R.drawable.face_guidance_border)
                binding.btnCapture.isEnabled = false
            }
            FaceFeedback.NOT_CENTERED -> {
                binding.txtGuidance.text = getString(R.string.face_not_centered)
                binding.faceOverlay.setBackgroundResource(R.drawable.face_guidance_border)
                binding.btnCapture.isEnabled = false
            }
            FaceFeedback.GOOD -> {
                binding.txtGuidance.text = getString(R.string.face_detected)
                binding.faceOverlay.setBackgroundResource(R.drawable.face_guidance_border_green)
                binding.btnCapture.isEnabled = true
            }
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = java.io.File(
            requireContext().filesDir,
            SimpleDateFormat(
                "yyyy-MM-dd-HH-mm-ss-SSS",
                Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraFragment", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = android.net.Uri.fromFile(photoFile)
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        "photoPath",
                        savedUri.toString()
                    )
                    findNavController().popBackStack()
                }
            }
        )
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private enum class FaceFeedback {
        NONE, TOO_FAR, TOO_CLOSE, NOT_CENTERED, GOOD
    }

    private class FaceAnalyzer(private val onFaceDetected: (FaceFeedback) -> Unit) :
        ImageAnalysis.Analyzer {
        private val detector = FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build()
        )

        @OptIn(ExperimentalGetImage::class)
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                detector.process(image)
                    .addOnSuccessListener { faces ->
                        if (faces.isEmpty()) {
                            onFaceDetected(FaceFeedback.NONE)
                        } else {
                            val face = faces[0]
                            val boundingBox = face.boundingBox
                            
                            // Image dimensions (post-rotation)
                            val isRotated = imageProxy.imageInfo.rotationDegrees == 90 || imageProxy.imageInfo.rotationDegrees == 270
                            val width = if (isRotated) imageProxy.height.toFloat() else imageProxy.width.toFloat()
                            val height = if (isRotated) imageProxy.width.toFloat() else imageProxy.height.toFloat()
                            
                            // Face dimensions relative to image
                            val faceWidth = boundingBox.width().toFloat() / width
                            val faceHeight = boundingBox.height().toFloat() / height
                            val faceArea = faceWidth * faceHeight
                            
                            // Face center relative to image (0.0 to 1.0)
                            val centerX = boundingBox.centerX().toFloat() / width
                            val centerY = boundingBox.centerY().toFloat() / height
                            
                            val feedback = when {
                                faceArea < 0.25 -> FaceFeedback.TOO_FAR
                                faceArea > 0.50 -> FaceFeedback.TOO_CLOSE
                                centerX < 0.4 || centerX > 0.6 || centerY < 0.45 || centerY > 0.55 -> FaceFeedback.NOT_CENTERED
                                else -> FaceFeedback.GOOD
                            }
                            onFaceDetected(feedback)
                        }
                    }
                    .addOnFailureListener {
                        onFaceDetected(FaceFeedback.NONE)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }
    }
}
