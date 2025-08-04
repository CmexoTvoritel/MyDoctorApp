package com.asc.mydoctorapp.ui.records.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asc.mydoctorapp.core.data.remote.RecordUI

private val TealColor = Color(0xFF43B3AE)

@Composable
fun RecordCard(
    record: RecordUI,
    isPast: Boolean,
    isCancelled: Boolean = false,
    onRecordClick: (String) -> Unit,
    onFavoriteToggle: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBorderColor = when {
        isCancelled -> Color(0xFFE57373).copy(alpha = 0.8f) // Red for cancelled
        isPast -> TealColor.copy(alpha = 0.6f)
        else -> TealColor
    }
    val cardBackgroundColor = when {
        isCancelled -> Color(0xFFFFF5F5) // Light red background for cancelled
        isPast -> Color(0xFFF2F2F2)
        else -> Color.White
    }
    val contentAlpha = if (isPast || isCancelled) 0.6f else 1f
    
    Surface(
        color = cardBackgroundColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .border(
                width = 1.dp,
                color = cardBorderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onRecordClick(record.id) }
    ) {
        Column {
            // Плашка статуса записи
            val (badgeColor, badgeText) = when {
                isCancelled -> Color(0xFFE57373) to "Отменена" // Red for cancelled
                record.isConfirmed -> Color(0xFF43B3AE) to "Запись подтверждена" // Teal for confirmed
                else -> Color(0xFFFFA726) to "Ожидает подтверждения" // Orange for pending
            }
            
            Surface(
                color = badgeColor,
                shape = RoundedCornerShape(
                    topStart = 8.dp,
                    topEnd = 8.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = badgeText,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            
            // Основное содержимое карточки
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Фото врача
                DoctorPhoto(
                    photoRes = record.photoRes, 
                    contentAlpha = contentAlpha
                )
                
                // Информация о записи
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    // Имя и специальность
                    Column(
                        modifier = Modifier.weight(0.7f)
                    ) {
                        Text(
                            text = record.doctorName,
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                            ),
                            color = Color.Black.copy(contentAlpha)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = record.specialty,
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp
                            ),
                            color = Color.Black.copy(alpha = 0.5f * contentAlpha)
                        )
                    }

                    Divider(
                        color = cardBorderColor,
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                            .padding(horizontal = 16.dp)
                            .width(2.dp)
                            .height(height = 54.dp)
                    )
                    
                    // Время и адрес
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = record.time,
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                            ),
                            color = Color.Black.copy(contentAlpha)
                        )
                        
                        Text(
                            text = "${record.address}\n${record.clinic}",
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                            ),
                            color = Color.Black.copy(contentAlpha)
                        )
                    }
                }
                
                // Иконка избранного
                IconButton(
                    onClick = { onFavoriteToggle(record.id, !record.isFavorite) },
                    modifier = Modifier.size(24.dp)
                        .align(alignment = Alignment.Top)
                ) {
                    Icon(
                        imageVector = if (record.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (record.isFavorite) "Удалить из избранного" else "Добавить в избранное",
                        tint = if (record.isFavorite) TealColor.copy(alpha = contentAlpha) else TealColor.copy(alpha = contentAlpha)
                    )
                }
            }
        }
    }
}

@Composable
private fun DoctorPhoto(photoRes: Int?, contentAlpha: Float) {
    Surface(
        shape = RoundedCornerShape(size = 8.dp),
        color = Color.Unspecified.copy(alpha = contentAlpha)
    ) {
        if (photoRes != null) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = photoRes),
                contentDescription = "Doctor photo",
                modifier = Modifier.size(64.dp)
            )
        }
    }
}
