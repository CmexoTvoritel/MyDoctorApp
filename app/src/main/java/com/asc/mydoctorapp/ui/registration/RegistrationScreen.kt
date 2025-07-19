package com.asc.mydoctorapp.ui.registration

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.navigation.AppRoutes
import com.asc.mydoctorapp.ui.registration.viewmodel.RegistrationViewModel
import com.asc.mydoctorapp.ui.registration.viewmodel.model.RegistrationAction
import com.asc.mydoctorapp.ui.registration.viewmodel.model.RegistrationEvent

private val TealColor = Color(0xFF55ABA9)
private val TealCheckboxColor = Color(0xFF43B3AE)

@Composable
fun RegistrationScreen(
    navigateTo: (String) -> Unit = {}
) {
    val viewModel: RegistrationViewModel = hiltViewModel()
    val state by viewModel.viewStates().collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    
    LaunchedEffect(Unit) {
        viewModel.viewActions().collect { action ->
            when (action) {
                is RegistrationAction.NavigateToHome -> {
                    navigateTo(AppRoutes.Home.route)
                }
                is RegistrationAction.ShowError -> {
                    snackbarHostState.showSnackbar(action.message)
                }
                else -> {}
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(TealColor),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo_dark),
                        contentDescription = "Doctor App Logo",
                        modifier = Modifier
                            .width(140.dp)
                            .padding(bottom = 30.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 36.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Поле логина
                    OutlinedTextField(
                        value = state?.login ?: "",
                        onValueChange = { viewModel.obtainEvent(RegistrationEvent.OnLoginChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            fontSize = 20.sp
                        ),
                        label = { Text(text = "Введите email") },
                        singleLine = true,
                        isError = state?.isLoginError ?: false,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealColor,
                            unfocusedBorderColor = Color.Gray,
                            errorBorderColor = Color.Red
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    
                    // Поле имени
                    OutlinedTextField(
                        value = state?.name ?: "",
                        onValueChange = { viewModel.obtainEvent(RegistrationEvent.OnNameChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            fontSize = 20.sp
                        ),
                        label = { Text(text = "Имя") },
                        singleLine = true,
                        isError = state?.isNameError ?: false,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealColor,
                            unfocusedBorderColor = Color.Gray,
                            errorBorderColor = Color.Red
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    
                    // Поле даты рождения
                    OutlinedTextField(
                        value = state?.birthDate ?: "",
                        onValueChange = { viewModel.obtainEvent(RegistrationEvent.OnBirthDateChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            fontSize = 20.sp
                        ),
                        label = { Text(text = "Дата рождения") },
                        singleLine = true,
                        isError = state?.isBirthDateError ?: false,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealColor,
                            unfocusedBorderColor = Color.Gray,
                            errorBorderColor = Color.Red
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    
                    // Поле пароля
                    OutlinedTextField(
                        value = state?.password ?: "",
                        onValueChange = { viewModel.obtainEvent(RegistrationEvent.OnPasswordChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            fontSize = 20.sp
                        ),
                        label = { Text(text = "Пароль") },
                        singleLine = true,
                        isError = state?.isPasswordError ?: false,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealColor,
                            unfocusedBorderColor = Color.Gray,
                            errorBorderColor = Color.Red
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (state?.isPasswordVisible == true) 
                            VisualTransformation.None 
                        else 
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon = if (state?.isPasswordVisible == true)
                                R.drawable.ic_eye_show
                            else
                                R.drawable.ic_eye_hide
                                
                            IconButton(
                                onClick = { viewModel.obtainEvent(RegistrationEvent.OnPasswordVisibilityToggle) }
                            ) {
                                Icon(
                                    modifier = Modifier.padding(end = 12.dp),
                                    painter = painterResource(icon),
                                    contentDescription = "Toggle password visibility",
                                    tint = TealColor
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.obtainEvent(RegistrationEvent.OnRegisterClick) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TealColor
                        ),
                        enabled = !(state?.isLoading ?: false)
                    ) {
                        if (state?.isLoading == true) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Зарегистрироваться",
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Уже есть аккаунт?",
                            fontSize = 17.sp
                        )
                        TextButton(
                            onClick = { 
                                navigateTo(AppRoutes.Login.route)
                            },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                        ) {
                            Text(
                                text = "Войти",
                                color = TealColor,
                                fontSize = 17.sp,
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = state?.consentGiven ?: false,
                            onCheckedChange = { viewModel.obtainEvent(RegistrationEvent.OnConsentToggle(it)) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = TealCheckboxColor,
                                checkmarkColor = Color.White,
                                uncheckedColor = TealCheckboxColor
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        
                        Text(
                            text = buildAnnotatedString {
                                append("Даю согласие на обработку персональных данных, соглашаюсь с ")
                                withStyle(style = SpanStyle(color = TealColor)) {
                                    append("Лицензионным соглашением")
                                }
                                append(" и подтверждаю, что ознакомлен с ")
                                withStyle(style = SpanStyle(color = TealColor)) {
                                    append("Политикой в отношении обработки ПД")
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}