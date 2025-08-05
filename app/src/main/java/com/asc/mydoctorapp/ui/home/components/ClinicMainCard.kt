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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.core.domain.model.Clinic
import com.asc.mydoctorapp.ui.chat.TealColor

@Composable
fun ClinicMainCard(
    clinicInfo: Clinic,
    onClinicClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width = 240.dp)
            .border(
                width = 1.dp,
                color = TealColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClinicClick() }
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.ic_clinic),
                    contentDescription = "Clinic ${clinicInfo.name} photo",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .size(75.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = clinicInfo.name ?: "",
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 22.sp
                        ),
                        color = Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Адрес: ${clinicInfo.address}",
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp
                ),
                color = Black.copy(alpha = 0.7f)
            )
        }
    }
}