package com.martodev.atoute.home.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Dialogue pour partager un événement via QR code
 * Note: Ceci est un mockup. Dans une implémentation réelle, 
 * il faudrait générer un vrai QR code avec une bibliothèque comme ZXing.
 */
@Composable
fun QrCodeShareDialog(
    eventName: String,
    eventShareData: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Partager \"$eventName\"",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Scannez ce QR code pour rejoindre l'événement",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Simuler un QR code avec un carré noir
                // Dans une vraie implémentation, on utiliserait une bibliothèque 
                // comme ZXing pour générer un QR code
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color.Black)
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White)
                            .align(Alignment.TopStart)
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White)
                            .align(Alignment.TopEnd)
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White)
                            .align(Alignment.BottomStart)
                    )
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color.White)
                            .align(Alignment.Center)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Fermer")
                }
            }
        }
    }
} 