package com.asc.mydoctorapp.ui.doctordetail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asc.mydoctorapp.ui.doctordetail.viewmodel.model.ReviewUi

@Composable
fun ReviewCard(
    review: ReviewUi,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(0xFF43B3AE)
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, accentColor),
        modifier = modifier
            .width(220.dp)
            .height(120.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Верхняя часть: аватар, имя и рейтинг
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Аватар
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFE0E0E0),
                    modifier = Modifier.size(32.dp)
                ) {
                    review.avatarRes?.let {
                        Image(
                            painter = painterResource(id = it),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column {
                    // Имя автора
                    Text(
                        text = review.author,
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    )
                    
                    // Рейтинг (звезды)
                    Row {
                        repeat(5) {
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = review.text,
                style = TextStyle(
                    fontSize = 15.sp,
                    lineHeight = 18.sp
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
