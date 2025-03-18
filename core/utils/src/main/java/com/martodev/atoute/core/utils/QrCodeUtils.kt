package com.martodev.atoute.core.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set

/**
 * Utilitaire pour générer des QR codes
 */
object QrCodeUtils {

    /**
     * Génère un QR code à partir d'une chaîne de caractères
     * 
     * @param content Le contenu à encoder dans le QR code
     * @param width La largeur du QR code en pixels
     * @param height La hauteur du QR code en pixels
     * @param margin La marge autour du QR code (par défaut 1)
     * @return Un Bitmap contenant le QR code généré
     */
    fun generateQrCode(content: String, width: Int, height: Int, margin: Int = 1): Bitmap {
        // Configurer les options pour le QR code
        val hints = hashMapOf<EncodeHintType, Any>().apply {
            // Niveau de correction d'erreur élevé pour une meilleure lisibilité
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
            // Marge autour du QR code
            put(EncodeHintType.MARGIN, margin)
            // Encodage UTF-8 pour supporter les caractères spéciaux
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
        }

        // Créer le QR code
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints)
        
        // Convertir la matrice en bitmap
        val bitmap = createBitmap(width, height)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        
        return bitmap
    }
} 