package com.asc.mydoctorapp.ui.doctorlist.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.asc.mydoctorapp.ui.doctorlist.model.DoctorUIItem

@Composable
fun DoctorItemCard(
    doctor: DoctorUIItem,
    onCardClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Surface(
        border = BorderStroke(1.dp, Color(0xFF43B3AE)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .clickable { onCardClick() }
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {

            // Фото
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE0E0E0),
                modifier = Modifier.size(64.dp)
            ) {
                doctor.photoRes?.let {
                    Image(painterResource(it), contentDescription = null, contentScale = ContentScale.Crop)
                }
            }

            Spacer(Modifier.width(12.dp))

            // Имя, спец, рейтинг
            Column(Modifier.weight(1f)) {
                Text(doctor.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(doctor.specialty, style = MaterialTheme.typography.bodyMedium, color = Color.Black.copy(alpha = 0.6f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { Icon(Icons.Outlined.Star, null, tint = Color(0xFF43B3AE), modifier = Modifier.size(16.dp)) }
                    // Можно заменить динамическим рейтингом позже
                }
            }

            // Избранное
            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = if (doctor.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = Color(0xFF43B3AE)
                )
            }
        }
    }
}