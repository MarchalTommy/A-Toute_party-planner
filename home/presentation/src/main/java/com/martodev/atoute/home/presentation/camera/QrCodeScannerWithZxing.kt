package com.martodev.atoute.home.presentation.camera

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

/**
 * Lance un scanner QR code en utilisant la bibliothèque ZXing Android Embedded
 * 
 * @param onScanCompleted callback appelé lorsqu'un QR code est scanné avec succès
 * @param onScanCancelled callback appelé si le scan est annulé
 */
@Composable
fun LaunchQrCodeScannerWithZxing(
    onScanCompleted: (String) -> Unit,
    onScanCancelled: () -> Unit
) {
    // Configurer les options de scan
    val scanOptions = ScanOptions().apply {
        setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        setPrompt("Scannez un QR code pour rejoindre un événement")
        setBeepEnabled(true)
        setOrientationLocked(false)
        setBarcodeImageEnabled(true)
        addExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.MIXED_SCAN)
    }
    
    // Créer le lanceur
    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            // QR code détecté
            onScanCompleted(result.contents)
        } else {
            // Scan annulé
            onScanCancelled()
        }
    }
    
    // Lancer le scanner immédiatement
    LaunchedEffect(Unit) {
        scanLauncher.launch(scanOptions)
    }
}

/**
 * Un composable à utiliser comme alternative à CameraPreview et QrCodeAnalyzer
 * si vous préférez utiliser ZXing au lieu de ML Kit
 * 
 * @param onScanCompleted callback appelé lorsqu'un QR code est scanné avec succès
 * @param onDismiss callback appelé quand le scan est annulé
 */
@Composable
fun ZxingScannerDialog(
    onScanCompleted: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // Lancer le scanner ZXing
    LaunchQrCodeScannerWithZxing(
        onScanCompleted = onScanCompleted,
        onScanCancelled = onDismiss
    )
} 