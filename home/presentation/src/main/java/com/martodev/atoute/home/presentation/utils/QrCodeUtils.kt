package com.martodev.atoute.home.presentation.utils

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/**
 * Utilitaire pour la génération de QR codes à l'aide de la bibliothèque ZXing
 */
object QrCodeUtils {
    
    /**
     * Génère un QR code sous forme d'ImageBitmap pour Compose à partir d'une chaîne de données
     * 
     * @param content La chaîne de caractères à encoder dans le QR code
     * @param size La taille en pixels du QR code (le QR code sera un carré de size x size)
     * @param margin La marge autour du QR code (0 pour aucune marge)
     * @return L'ImageBitmap contenant le QR code
     */
    fun generateQrCodeBitmap(
        content: String,
        size: Int = 512,
        margin: Int = 0  // Par défaut sans marge pour optimiser l'espace
    ): ImageBitmap {
        // Définir les options pour le QR code
        val hints = hashMapOf<EncodeHintType, Any>().apply {
            // Niveau de correction d'erreur élevé pour une meilleure lisibilité même si le code est partiellement masqué
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
            // Marge autour du QR code (petite pour maximiser la taille)
            put(EncodeHintType.MARGIN, margin)
            // Encodage des caractères
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
        }
        
        // Créer le QR code
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        
        // Convertir la matrice en bitmap
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)
        
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
            }
        }
        
        // Créer le bitmap
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        
        // Convertir en ImageBitmap pour Compose
        return bitmap.asImageBitmap()
    }
} 