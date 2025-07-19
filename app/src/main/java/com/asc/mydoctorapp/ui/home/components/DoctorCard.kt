package com.asc.mydoctorapp.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asc.mydoctorapp.ui.home.viewmodel.model.DoctorUi

private val TealColor = Color(0xFF43B3AE)

@Composable
fun DoctorCard(
    doctor: DoctorUi,
    isFavorite: Boolean,
    onDoctorClick: (String) -> Unit,
    onFavoriteToggle: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = TealColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable { onDoctorClick(doctor.id) }
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Row {
                Image(
                    painter = painterResource(id = doctor.photoRes),
                    contentDescription = "Doctor ${doctor.name} photo",
                    modifier = Modifier
                        .size(75.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(width = 8.dp))
                        Text(
                            text = String.format("%.1f", doctor.rating),
                            style = TextStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = 22.sp
                            ),
                            color = TealColor
                        )

                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = TealColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Icon(
                        modifier = Modifier.size(28.dp)
                            .clickable {
                                onFavoriteToggle(doctor.id, !isFavorite)
                            },
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = TealColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier.width(width = 100.dp),
                text = doctor.name,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    fontSize = 20.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                modifier = Modifier.width(width = 100.dp)
                    .height(height = 50.dp),
                text = doctor.specialty,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    color = Color.Black.copy(alpha = 0.5f),
                    fontSize = 20.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
