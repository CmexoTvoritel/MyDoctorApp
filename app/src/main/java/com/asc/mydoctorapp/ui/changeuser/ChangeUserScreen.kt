package com.asc.mydoctorapp.ui.changeuser

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.ui.changeuser.viewmodel.ChangeUserViewModel
import com.asc.mydoctorapp.ui.changeuser.viewmodel.model.ChangeUserAction
import com.asc.mydoctorapp.ui.changeuser.viewmodel.model.ChangeUserEvent

private val Teal = Color(0xFF43B3AE)

@Composable
fun ChangeUserScreen(
    onBackClick: () -> Unit,
) {
    val viewModel: ChangeUserViewModel = hiltViewModel()
    val state by viewModel.viewStates().collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.viewActions().collect { action ->
            when (action) {
                is ChangeUserAction.OnNavigateAfterSave -> {
                    onBackClick()
                }
                is ChangeUserAction.ShowError -> {
                    Toast.makeText(context, action.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 24.dp)
                .fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back_navigation),
                contentDescription = "Назад",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBackClick() }
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "Изменить аккаунт",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                modifier = Modifier.weight(1f)
            )
            SavePill(
                onClick = {
                    viewModel.obtainEvent(
                        viewEvent = ChangeUserEvent.OnSaveClick
                    )
                }
            )
        }

        // Поле: Имя
        FieldLabel("Имя")
        ClearableOutlinedField(
            value = state?.name ?: "",
            onValueChange = {
                viewModel.obtainEvent(viewEvent = ChangeUserEvent.OnNameChange(it))
            }
        )
        Spacer(Modifier.height(16.dp))

        // Поле: Логин
        FieldLabel("Логин")
        ClearableOutlinedField(
            value = state?.email ?: "",
            onValueChange = {
                viewModel.obtainEvent(viewEvent = ChangeUserEvent.OnEmailChange(it))
            }
        )
        Spacer(Modifier.height(16.dp))

        // Поле: Дата рождения (строка)
        FieldLabel("Дата рождения")
        ClearableOutlinedField(
            value = state?.dateOfBirth ?: "",
            onValueChange = {
                viewModel.obtainEvent(viewEvent = ChangeUserEvent.OnDateOfBirthChange(it))
            },
            placeholder = "ДД.ММ.ГГГГ"
        )

        Spacer(Modifier.height(24.dp))
        Text(
            text = "Имя, логин и дата рождения используются в вашем профиле.",
            color = Color.Black.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = Color.Black.copy(alpha = 0.6f),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun ClearableOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth(),
        placeholder = {
            if (placeholder.isNotEmpty()) Text(placeholder)
        },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Teal,
            unfocusedBorderColor = Teal
        ),
        trailingIcon = {
            if (value.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Очистить",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onValueChange("") },
                    tint = Color.Black.copy(alpha = 0.6f)
                )
            }
        }
    )
}

@Composable
private fun SavePill(
    onClick: () -> Unit
) {
    Surface(
        color = Teal.copy(alpha = 0.3f),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .height(32.dp)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = "Сохранить",
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}