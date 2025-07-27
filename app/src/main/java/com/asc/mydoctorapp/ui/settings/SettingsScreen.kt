package com.asc.mydoctorapp.ui.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.navigation.AppRoutes
import com.asc.mydoctorapp.ui.settings.components.AppThemeOption
import com.asc.mydoctorapp.ui.settings.components.ThemeBottomSheet

enum class SettingsItem(val title: String) {
    ACCOUNT("Аккаунт пользователя"),
    ABOUT("О приложении"),
    THEME("Тема")
}

@Composable
fun SettingsScreen(onNavigateTo: (String) -> Unit, onNavigateBack: () -> Unit) {
    var showThemeSheet by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf(AppThemeOption.LIGHT) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(horizontal = 24.dp)
    ) {

        /* ---------- Header: ←  "Настройки" ---------- */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 32.dp)
                .fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back_navigation),
                contentDescription = "Назад",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onNavigateBack() }
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "Настройки",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            )
        }

        /* ---------- Items ---------- */
        SettingsItem.entries.forEach { item ->
            SettingsRow(
                title = item.title,
                onClick = {
                    when (item) {
                        SettingsItem.ACCOUNT -> onNavigateTo(AppRoutes.ProfileEdit.route)
                        SettingsItem.ABOUT -> {}
                        SettingsItem.THEME -> {
                            showThemeSheet = true
                        }
                    }
                }
            )
        }

        if (showThemeSheet) {
            ThemeBottomSheet(
                selected = selectedTheme,
                onSelect = { choice -> selectedTheme = choice },
                onDismiss = { showThemeSheet = false }
            )
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    onClick: () -> Unit,
    @DrawableRes arrowRes: Int = R.drawable.ic_forward_navigation
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 24.dp),          // расстояние между строками
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 20.sp
            ),
            modifier = Modifier.weight(1f)
        )

        /* иконка “>” в светло-сером кружке – как на скрине */
        Icon(
            painter = painterResource(id = arrowRes),
            contentDescription = "Перейти",
            tint = Color.Unspecified,
            modifier = Modifier
                .size(38.dp)
                .padding(6.dp)
        )
    }
}