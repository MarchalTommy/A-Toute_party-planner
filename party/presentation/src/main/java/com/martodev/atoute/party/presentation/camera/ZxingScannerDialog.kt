package com.martodev.atoute.party.presentation.camera

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

/**
 * Dialogue de scan QR code utilisant la bibliothèque ZXing pour Android
 * Lance une activité de scan fournie par ZXing et renvoie le résultat
 */
@Composable
fun ZxingScannerDialog(
    onScanCompleted: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // Scanner configuré pour QR codes uniquement
    val scanOptions = remember {
        ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("Placez un QR code dans le rectangle pour le scanner")
            setBeepEnabled(true)
            setOrientationLocked(false)
            setTorchEnabled(false)
            setCameraId(0) // Caméra arrière
            setBarcodeImageEnabled(true)
            addExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.MIXED_SCAN)
        }
    }
    
    // Gestionnaire de résultat de l'activité de scan
    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            // Un QR code a été scanné avec succès
            onScanCompleted(result.contents)
        } else {
            // Scan annulé ou échoué
            onDismiss()
        }
    }
    
    // Lancer l'activité de scan dès que ce composable est composé
    LaunchedEffect(Unit) {
        scanLauncher.launch(scanOptions)
    }
    
    // Ce composable n'affiche rien directement car il lance juste l'activité ZXing
} 