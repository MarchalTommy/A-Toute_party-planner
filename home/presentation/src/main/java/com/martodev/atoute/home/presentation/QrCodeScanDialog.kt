package com.martodev.atoute.home.presentation

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.martodev.atoute.home.presentation.camera.CameraPreview
import com.martodev.atoute.home.presentation.camera.ZxingScannerDialog

/**
 * Dialogue pour scanner un QR code et rejoindre un événement
 * Utilise CameraX et ML Kit pour détecter et analyser les codes QR
 * Offre également l'option d'utiliser ZXing pour le scan
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QrCodeScanDialog(
    onDismiss: () -> Unit,
    onScanCompleted: (eventData: String) -> Unit
) {
    // État pour gérer les scans détectés afin d'éviter les doublons
    var hasDetectedCode by remember { mutableStateOf(false) }

    // État pour savoir si nous utilisons ZXing au lieu de ML Kit
    var useZxingScanner by remember { mutableStateOf(false) }

    // Gestion de la permission de caméra
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    // Si nous utilisons ZXing, lancer le scanner ZXing
    if (useZxingScanner) {
        ZxingScannerDialog(
            onScanCompleted = { qrCodeValue ->
                onScanCompleted(qrCodeValue)
            },
            onDismiss = {
                // Revenir au dialogue normal
                useZxingScanner = false
            }
        )
        return
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Titre
                Text(
                    text = "Scanner un QR code",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Placez le QR code devant votre caméra pour rejoindre un événement",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                
                // Aperçu caméra ou demande de permission
                if (cameraPermissionState.status.isGranted) {
                    // Aperçu de la caméra avec analyse de QR code
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        CameraPreview(
                            onQrCodeScanned = { qrCodeValue ->
                                if (!hasDetectedCode) {
                                    hasDetectedCode = true
                                    onScanCompleted(qrCodeValue)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        // Indicateur de scan actif
                        CircularProgressIndicator(
                            modifier = Modifier.size(50.dp),
                            color = MaterialTheme.colorScheme.tertiary,
                            strokeWidth = 2.dp
                        )
                    }
                } else {
                    // Vue de demande de permission
                    Box(
                        modifier = Modifier
                            .size(250.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.qr_code_scanner),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        // Demander automatiquement la permission
                        LaunchedEffect(key1 = Unit) {
                            cameraPermissionState.launchPermissionRequest()
                        }
                    }
                }
                
                // Affichage conditionnel des boutons en fonction de l'état de permission
                if (!cameraPermissionState.status.isGranted) {
                    // Bouton pour demander la permission
                    Button(
                        onClick = { cameraPermissionState.launchPermissionRequest() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Autoriser la caméra")
                    }
                } else {
                    // Options de scan quand la caméra est disponible
                    
                    // Bouton ZXing
                    Button(
                        onClick = { useZxingScanner = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.qr_code_scanner),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scanner avec ZXing")
                    }
                    
                    // Bouton de simulation
                    OutlinedButton(
                        onClick = {
                            val mockData =
                                "event-123|Fête d'Anniversaire|2024-05-25T18:00:00|123 Rue de Paris|0xFFE91E63|Fête d'anniversaire pour Julie avec ses amis proches|Marc,Sophie,Léa"
                            onScanCompleted(mockData)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simuler un scan")
                    }
                }
                
                // Bouton Annuler (toujours présent)
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Annuler")
                }
            }
        }
    }
} 