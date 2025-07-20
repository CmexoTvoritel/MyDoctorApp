package com.asc.mydoctorapp.ui.bottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asc.mydoctorapp.navigation.BottomBarItem

@Composable
fun CustomBottomNavigationBar(
    bottomBarItems: List<BottomBarItem>,
    selectedBottomBarItem: BottomBarItem,
    onBottomBarItemClick: (BottomBarItem) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {
            bottomBarItems.forEach { item ->
                val isSelected = selectedBottomBarItem == item
                
                BottomBarItemComponent(
                    item = item,
                    isSelected = isSelected,
                    onItemClick = { onBottomBarItemClick(item) }
                )
            }
        }
    }
}

@Composable
private fun BottomBarItemComponent(
    item: BottomBarItem,
    isSelected: Boolean,
    onItemClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onItemClick()
            }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val tint = if (isSelected) {
            Color(0xFF43B3AE)
        } else {
            Color.Black
        }

        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = stringResource(id = item.menuName),
            modifier = Modifier.size(28.dp),
            tint = tint
        )
        
        Text(
            text = stringResource(id = item.menuName),
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            ),
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(height = 18.dp))
    }
}