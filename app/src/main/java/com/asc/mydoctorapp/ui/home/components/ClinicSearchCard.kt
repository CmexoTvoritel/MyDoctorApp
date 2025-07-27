package com.asc.mydoctorapp.ui.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.asc.mydoctorapp.R

@Composable
fun ClinicSearchCard(
    clinicName: String,
    clinicAddress: String,
    clinicEmail: String,
    clinicPhone: String,
    clinicWorkingTime: String,
    onClinicClick: (String) -> Unit,
) {
    Surface(
        border = BorderStroke(1.dp, Color(0xFF43B3AE)),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClinicClick(clinicName) }
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE0E0E0),
                modifier = Modifier.size(72.dp)
            ) {
                Image(
                    painterResource(id = R.drawable.ic_clinic),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text("$clinicName, адрес: $clinicAddress", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text("Почта: $clinicEmail", style = MaterialTheme.typography.bodyMedium, color = Color.Black.copy(alpha = 0.6f))
                Text("Почта: $clinicPhone", style = MaterialTheme.typography.bodyMedium, color = Color.Black.copy(alpha = 0.6f))
                Text("Рабочее время: $clinicWorkingTime", style = MaterialTheme.typography.bodyMedium, color = Color.Black.copy(alpha = 0.6f))
            }
        }
    }
}