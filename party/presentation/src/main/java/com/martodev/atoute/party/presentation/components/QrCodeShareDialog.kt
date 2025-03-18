package com.martodev.atoute.party.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.martodev.atoute.core.utils.QrCodeUtils

/**
 * Dialogue pour partager un événement via un QR code
 * Affiche un QR code contenant les données de l'événement formatées
 */
@Composable
fun QrCodeShareDialog(
    eventData: String?,
    onDismiss: () -> Unit
) {
    // État pour suivre si le QR code a été généré avec succès
    var qrCodeBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Générer le QR code lors du lancement
    LaunchedEffect(eventData) {
        if (eventData != null) {
            try {
                val bitmap = QrCodeUtils.generateQrCode(eventData, 300, 300)
                qrCodeBitmap = bitmap
            } catch (e: Exception) {
                errorMessage = "Erreur lors de la génération du QR code: ${e.message}"
            }
        } else {
            errorMessage = "Données d'événement invalides"
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Partager l'événement",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Afficher le QR code ou un message d'erreur
                if (qrCodeBitmap != null) {
                    Image(
                        bitmap = qrCodeBitmap!!.asImageBitmap(),
                        contentDescription = "QR Code pour partager l'événement",
                        modifier = Modifier.size(220.dp)
                    )
                    
                    Text(
                        text = "Scannez ce code pour dupliquer l'événement sur un autre appareil",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                } else if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "Une erreur s'est produite",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Fermer")
                }
            }
        }
    }
} 