package com.martodev.atoute.home.presentation.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.concurrent.Executors

private const val TAG = "CameraPreview"

/**
 * Composable pour la prévisualisation de la caméra avec analyse de QR code
 */
@Composable
fun CameraPreview(
    onQrCodeScanned: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    
    // Création de PreviewView pour afficher l'aperçu de la caméra
    val previewView = remember { PreviewView(context) }
    
    // Création d'un exécuteur pour l'analyse d'image
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    // Création de l'analyseur de QR code
    val analyzer = remember { QrCodeAnalyzer(onQrCodeScanned) }
    
    LaunchedEffect(key1 = previewView) {
        // Initialisation et configuration de la caméra
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        
        cameraProviderFuture.addListener({
            try {
                // Obtention du fournisseur de caméra
                val cameraProvider = cameraProviderFuture.get()
                
                // Configurer la prévisualisation
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                
                // Configuration de l'analyseur d'images
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build().also {
                        it.setAnalyzer(cameraExecutor, analyzer)
                    }
                
                // Sélection de la caméra arrière par défaut
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                
                // Liaison des cas d'utilisation de la caméra avec le cycle de vie
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
                
            } catch (e: Exception) {
                Log.e(TAG, "Échec de la liaison des cas d'utilisation de la caméra", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
    
    // AndroidView pour intégrer PreviewView dans Compose
    AndroidView(
        factory = { previewView },
        modifier = modifier.fillMaxSize()
    )
}

/**
 * Fonction d'extension pour obtenir le fournisseur de caméra
 */
fun Context.getCameraProvider(): ProcessCameraProvider {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
    return cameraProviderFuture.get()
} 