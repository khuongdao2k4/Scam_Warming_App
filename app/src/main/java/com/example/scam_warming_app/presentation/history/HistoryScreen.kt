package com.example.scam_warming_app.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Message
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scam_warming_app.domain.usecase.HistoryItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onNavigateToDetail: (Long, String) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val historyItems by viewModel.historyItems.collectAsState()
    var showOnlyScam by remember { mutableStateOf(false) }

    val filteredItems = if (showOnlyScam) {
        historyItems.filter { it.isScam }
    } else {
        historyItems
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nhật ký bảo vệ", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearAllHistory() }) {
                        Icon(Icons.Rounded.DeleteSweep, contentDescription = "Xóa hết", tint = Color.Gray)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Filter Chip
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = !showOnlyScam,
                    onClick = { showOnlyScam = false },
                    label = { Text("Tất cả") }
                )
                FilterChip(
                    selected = showOnlyScam,
                    onClick = { showOnlyScam = true },
                    label = { Text("Chỉ lừa đảo") },
                    leadingIcon = {
                        if (showOnlyScam) Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                )
            }

            if (filteredItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không có dữ liệu lịch sử", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    items(filteredItems) { item ->
                        HistoryRow(
                            item = item,
                            onClick = { 
                                val type = if (item is HistoryItem.Sms) "sms" else "call"
                                onNavigateToDetail(item.id, type)
                            }
                        )
                        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryRow(item: HistoryItem, onClick: () -> Unit) {
    val icon = if (item is HistoryItem.Sms) Icons.AutoMirrored.Rounded.Message else Icons.Rounded.Phone
    val color = if (item.isScam) Color.Red else Color(0xFF43A047)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.identifier,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = if (item is HistoryItem.Sms) item.message else item.category ?: "Cuộc gọi số lạ",
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatTime(item.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            if (item.isScam) {
                Text("CẢNH BÁO", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
