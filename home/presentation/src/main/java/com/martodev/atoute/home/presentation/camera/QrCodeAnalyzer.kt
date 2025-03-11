package com.martodev.atoute.home.presentation.camera

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * Classe d'analyse d'image pour la détection de codes QR
 * Utilise ML Kit pour le traitement des codes-barres
 */
class QrCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    // Configuration du scanner de codes-barres pour se concentrer uniquement sur les codes QR
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    // Créer le scanner de codes-barres avec les options spécifiées
    private val scanner = BarcodeScanning.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            // Convertir l'image de la caméra en format InputImage pour ML Kit
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            
            // Traiter l'image avec le scanner de codes-barres
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    // Vérifier si des codes QR ont été détectés
                    if (barcodes.isNotEmpty()) {
                        // Prendre le premier code QR détecté
                        for (barcode in barcodes) {
                            // Extraire la valeur du code QR
                            val rawValue = barcode.rawValue
                            if (rawValue != null) {
                                // Appeler le callback avec la valeur du code QR
                                onQrCodeScanned(rawValue)
                                break
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Gérer les erreurs potentielles
                    exception.printStackTrace()
                }
                .addOnCompleteListener {
                    // Important: Toujours fermer l'ImageProxy pour libérer les ressources
                    imageProxy.close()
                }
        } else {
            // Si aucune image n'est disponible, fermer quand même l'ImageProxy
            imageProxy.close()
        }
    }
} 