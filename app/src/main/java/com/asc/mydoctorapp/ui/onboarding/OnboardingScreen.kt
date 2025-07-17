package com.asc.mydoctorapp.ui.onboarding

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.navigation.AppRoutes
import com.asc.mydoctorapp.ui.onboarding.viewmodel.OnboardingViewModel
import com.asc.mydoctorapp.ui.onboarding.viewmodel.model.OnboardingAction
import com.asc.mydoctorapp.ui.onboarding.viewmodel.model.OnboardingEvent
import com.asc.mydoctorapp.ui.onboarding.viewmodel.model.OnboardingPage
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onNavigateToPage: (String) -> Unit
) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    val state by viewModel.viewStates().collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.viewActions().collect { action ->
            when (action) {
                is OnboardingAction.NavigateToLogin -> {
                    onNavigateToPage(AppRoutes.Login.route)
                }
                else -> {}
            }
        }
    }
    
    OnboardingContent(
        currentPage = state?.currentPage ?: 0,
        pages = state?.pages ?: emptyList(),
        onNextPage = { viewModel.obtainEvent(OnboardingEvent.NextPage) },
        onNavigateToPage = { page -> viewModel.obtainEvent(OnboardingEvent.NavigateToPage(page)) }
    )
}

@Composable
private fun OnboardingContent(
    currentPage: Int,
    pages: List<OnboardingPage>,
    onNextPage: () -> Unit,
    onNavigateToPage: (Int) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = currentPage) { pages.size }
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(currentPage) {
        if (currentPage != pagerState.currentPage) {
            pagerState.animateScrollToPage(currentPage)
        }
    }
    
    LaunchedEffect(pagerState.currentPage) {
        if (currentPage != pagerState.currentPage) {
            onNavigateToPage(pagerState.currentPage)
        }
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo of application
            Image(
                painter = painterResource(id = R.drawable.ic_onboarding_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .padding(top = 24.dp, bottom = 16.dp)
                    .width(140.dp).height(24.dp),
                colorFilter = null
            )
            Spacer(modifier = Modifier.height(16.dp))
            // ViewPager with content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    OnboardingPageContent(
                        page = pages.getOrNull(page) ?: return@HorizontalPager
                    )
                }
            }
            
            // Tab indicator
            PagerIndicator(
                pageCount = pages.size,
                currentPage = pagerState.currentPage,
                onPageSelected = { page ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(page)
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Next button
            Button(
                onClick = onNextPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF43B3AE)
                )
            ) {
                Text(
                    text = "Далее",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentScale = ContentScale.FillHeight
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = stringResource(page.title),
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 40.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(page.subtitle),
            fontSize = 18.sp,
            color = Color(0xFF666666),
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 48.dp)
        )
    }
}

@Composable
private fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    onPageSelected: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        repeat(pageCount) { page ->
            val isSelected = page == currentPage
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (isSelected) 12.dp else 10.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color(0xFF43B3AE)
                        else Color(0xFFD9D9D9)
                    )
                    .clickable { onPageSelected(page) }
            )
        }
    }
}