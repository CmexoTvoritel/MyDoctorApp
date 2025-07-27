package com.asc.mydoctorapp.ui.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AppThemeOption { LIGHT, DARK, SYSTEM }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeBottomSheet(
    selected: AppThemeOption,
    onSelect: (AppThemeOption) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Выберите тему",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            ThemeRow("Светлая", selected == AppThemeOption.LIGHT) {
                onSelect(AppThemeOption.LIGHT)
            }
            Spacer(Modifier.height(24.dp))
            ThemeRow("Тёмная", selected == AppThemeOption.DARK) {
                onSelect(AppThemeOption.DARK)
            }
            Spacer(Modifier.height(24.dp))
            ThemeRow("Как в системе", selected == AppThemeOption.SYSTEM) {
                onSelect(AppThemeOption.SYSTEM)
            }
            Spacer(modifier = Modifier.height(height = 48.dp))
        }
    }
}

@Composable
private fun ThemeRow(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF43B3AE)
            )
        }
    }
}