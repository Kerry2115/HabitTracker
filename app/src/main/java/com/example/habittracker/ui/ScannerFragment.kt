package com.example.habittracker.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.databinding.FragmentScannerBinding
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat.getMainExecutor
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.launch

class ScannerFragment : Fragment() {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScannerViewModel by viewModels()

    private var isProcessing = false

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startCamera() else showPermissionError()
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonRescan.setOnClickListener {
            resetUi()
            startCamera()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility = if (state.loading) View.VISIBLE else View.GONE
            binding.textError.visibility = if (state.error != null) View.VISIBLE else View.GONE
            binding.textError.text = state.error ?: ""

            val product = state.product
            if (product != null) {
                binding.resultContainer.visibility = View.VISIBLE
                binding.textProductName.text = product.productName ?: "-"
                binding.textBrands.text = product.brands ?: "-"

                val ingredients = product.ingredientsTextPl ?: product.ingredientsText ?: "-"
                binding.textIngredients.text = ingredients

                val n = product.nutriments
                binding.textNutrients.text = buildString {
                    appendLine("Energia: ${n?.energyKcal100g ?: "-"} kcal / 100g")
                    appendLine("Cukry: ${n?.sugars100g ?: "-"} g / 100g")
                    appendLine("Tluszcz: ${n?.fat100g ?: "-"} g / 100g")
                    appendLine("Kwasy nasycone: ${n?.saturatedFat100g ?: "-"} g / 100g")
                    appendLine("Weglowodany: ${n?.carbohydrates100g ?: "-"} g / 100g")
                    appendLine("Bialko: ${n?.proteins100g ?: "-"} g / 100g")
                    appendLine("Sol: ${n?.salt100g ?: "-"} g / 100g")
                }
            }
        }

        ensureCameraPermissionThenStart()
    }

    private fun ensureCameraPermissionThenStart() {
        val granted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        } else {
            startCamera()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }

            val analyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            val scanner = BarcodeScanning.getClient()
            analyzer.setAnalyzer(getMainExecutor(requireContext())) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null && !isProcessing) {
                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            val barcode = barcodes.firstOrNull { it.valueType == Barcode.TYPE_PRODUCT || it.rawValue != null }
                            val raw = barcode?.rawValue
                            if (raw != null && raw.length >= 8) {
                                isProcessing = true
                                stopCamera(cameraProvider)
                                viewModel.fetchProduct(raw)
                            }
                        }
                        .addOnFailureListener {
                            // no-op
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                } else {
                    imageProxy.close()
                }
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analyzer
                )
            } catch (_: Exception) {
                // ignore
            }
        }, getMainExecutor(requireContext()))
    }

    private fun stopCamera(provider: ProcessCameraProvider) {
        provider.unbindAll()
    }

    private fun showPermissionError() {
        binding.textError.visibility = View.VISIBLE
        binding.textError.text = "Brak uprawnien do aparatu"
    }

    private fun resetUi() {
        isProcessing = false
        binding.resultContainer.visibility = View.GONE
        binding.textError.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ScannerFragment()
    }
}
