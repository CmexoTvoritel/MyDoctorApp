package com.asc.mydoctorapp.ui.login

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.navigation.AppRoutes
import com.asc.mydoctorapp.ui.login.viewmodel.LoginViewModel
import com.asc.mydoctorapp.ui.login.viewmodel.model.LoginAction
import com.asc.mydoctorapp.ui.login.viewmodel.model.LoginEvent

private val TealColor = Color(0xFF55ABA9)
private val TealCheckboxColor = Color(0xFF43B3AE)
private val VkBlueColor = Color(0xFF0077FF)
private val GrayTextColor = Color(0xFF9E9E9E)

@Composable
fun LoginScreen(
    navigateTo: (String) -> Unit = {}
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val state by viewModel.viewStates().collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    
    LaunchedEffect(Unit) {
        viewModel.viewActions().collect { action ->
            when (action) {
                is LoginAction.NavigateToHome -> {
                    navigateTo(AppRoutes.Home.route)
                }
                is LoginAction.ShowError -> {
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
                    OutlinedTextField(
                        value = state?.login ?: "",
                        onValueChange = { viewModel.obtainEvent(LoginEvent.OnLoginChanged(it)) },
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
                    
                    OutlinedTextField(
                        value = state?.password ?: "",
                        onValueChange = { viewModel.obtainEvent(LoginEvent.OnPasswordChanged(it)) },
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
                                onClick = { viewModel.obtainEvent(LoginEvent.OnPasswordVisibilityToggle) }
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
                        onClick = { viewModel.obtainEvent(LoginEvent.OnLoginClick) },
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
                                text = "Войти",
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
                            text = "Нет аккаунта?",
                            fontSize = 17.sp
                        )
                        TextButton(
                            onClick = { 
                                navigateTo(AppRoutes.Registration.route)
                            },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                        ) {
                            Text(
                                text = "Регистрация",
                                color = TealColor,
                                fontSize = 17.sp,
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Или войдите через:",
                        color = GrayTextColor,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 17.sp
                    )
                    
                    Button(
                        onClick = { viewModel.obtainEvent(LoginEvent.OnVkLoginClick) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VkBlueColor
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_vk_logo),
                                contentDescription = "VK Logo",
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ID",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
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
                            onCheckedChange = { viewModel.obtainEvent(LoginEvent.OnConsentToggle(it)) },
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