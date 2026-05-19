package com.example.scam_warming_app.presentation.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PhoneCallback
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scam_warming_app.data.local.entity.CallEntity
import com.example.scam_warming_app.data.local.entity.SmsEntity
import com.example.scam_warming_app.utils.DownloadStatus

@Composable
fun HomeScreen(
    onNavigateToReport: () -> Unit,
    onNavigateToDetail: (Long, String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val smsHistory by viewModel.smsHistory.collectAsState()
    val callHistory by viewModel.callHistory.collectAsState()
    val stats by viewModel.protectionStats.collectAsState()
    val dbCount by viewModel.blacklistCount.collectAsState()
    val isAiReady by viewModel.isAiReady.collectAsState()
    val downloadStatus by viewModel.downloadStatus.collectAsState()

    // Launcher để chọn file thủ công (Dự phòng trường hợp tải tự động thất bại)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.let { inputStream ->
                viewModel.installManualModel(inputStream)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToReport,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Báo cáo lừa đảo")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.background
                    )
                ))
        ) {
            HeaderSection()
            
            AiStatusCard(
                isReady = isAiReady,
                status = downloadStatus,
                onDownloadClick = { viewModel.startModelDownload() },
                onPickFileClick = { filePickerLauncher.launch("*/*") }
            )

            StatsSection(stats.blockedSms, stats.analyzedCalls, dbCount)
            
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Nhật ký bảo vệ", 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.Black, 
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { viewModel.deleteHistory() }) {
                        Text("Xóa hết", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { SectionTitle("Tin nhắn gần đây", Icons.Rounded.Sms) }
                    if (smsHistory.isEmpty()) {
                        item { EmptyCard("Chưa phát hiện tin nhắn độc hại") }
                    } else {
                        items(smsHistory) { sms ->
                            SmsHistoryItem(sms, onClick = { onNavigateToDetail(sms.id, "sms") })
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    
                    item { SectionTitle("Cuộc gọi số lạ", Icons.AutoMirrored.Rounded.PhoneCallback) }
                    if (callHistory.isEmpty()) {
                        item { EmptyCard("Hệ thống chưa ghi nhận cuộc gọi lạ") }
                    } else {
                        items(callHistory) { call ->
                            CallHistoryItem(call, onClick = { onNavigateToDetail(call.id, "call") })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AiStatusCard(
    isReady: Boolean, 
    status: DownloadStatus, 
    onDownloadClick: () -> Unit,
    onPickFileClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isReady) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isReady) Icons.Rounded.Psychology else Icons.Rounded.PsychologyAlt,
                contentDescription = null,
                tint = if (isReady) Color(0xFF2E7D32) else Color.Gray
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isReady) "Trí tuệ nhân tạo: Sẵn sàng" else "Chưa kích hoạt AI nâng cao",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                if (!isReady) {
                    when (status) {
                        is DownloadStatus.Downloading -> {
                            val progress = if (status.progress >= 0) status.progress / 100f else null
                            if (progress != null) {
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                )
                                Text("Đang tải/nạp AI: ${status.progress}%", fontSize = 11.sp, color = Color.Gray)
                            } else {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                                Text("Đang kết nối GitHub/Google...", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                        is DownloadStatus.Error -> Text(status.message, fontSize = 11.sp, color = Color.Red)
                        else -> Text("Đang khởi động tiến trình tải...", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
            if (!isReady && status !is DownloadStatus.Downloading) {
                Column {
                    TextButton(onClick = onDownloadClick) { Text("THỬ LẠI", fontSize = 10.sp) }
                    TextButton(onClick = onPickFileClick) { Text("CHỌN FILE", fontSize = 10.sp) }
                }
            }
        }
    }
}

@Composable
fun StatsSection(blockedSms: Int, analyzedCalls: Int, dbSize: Int) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard("Đã chặn SMS", blockedSms.toString(), Icons.Rounded.Message, Modifier.weight(1f))
        StatCard("Đã quét Call", analyzedCalls.toString(), Icons.Rounded.Phone, Modifier.weight(1f))
        StatCard("Dữ liệu Scam", dbSize.toString(), Icons.Rounded.Update, Modifier.weight(1f))
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Text(value, fontWeight = FontWeight.Black, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Text(label, fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun HeaderSection() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(animation = tween(1500), repeatMode = RepeatMode.Reverse),
        label = "scale"
    )

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF004D40)),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Shield, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(28.dp).graphicsLayer(scaleX = scale, scaleY = scale)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text("Lá chắn đang chạy", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text("AI đang giám sát thời gian thực", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
        Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun EmptyCard(text: String) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)), shape = RoundedCornerShape(16.dp)) {
        Text(text = text, modifier = Modifier.padding(24.dp).fillMaxWidth(), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
    }
}

@Composable
fun SmsHistoryItem(sms: SmsEntity, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = if (sms.isScam) Color(0xFFFFF1F0) else Color.White), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(if (sms.isScam) Color.Red else Color(0xFF4CAF50)))
                Spacer(modifier = Modifier.width(8.dp))
                Text(sms.sender, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                if (sms.isScam) Text("NGUY HIỂM", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(sms.message, fontSize = 14.sp, color = Color.DarkGray, maxLines = 2)
        }
    }
}

@Composable
fun CallHistoryItem(call: CallEntity, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = if (call.isScam) Color(0xFFFFF1F0) else Color.White), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (call.isScam) Color.Red.copy(alpha = 0.1f) else Color(0xFFE8F5E9)), contentAlignment = Alignment.Center) {
                Icon(imageVector = if (call.isScam) Icons.Rounded.ReportProblem else Icons.Rounded.VerifiedUser, contentDescription = null, tint = if (call.isScam) Color.Red else Color(0xFF43A047), modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(call.phoneNumber, fontWeight = FontWeight.Bold)
                Text(call.category ?: "Cuộc gọi số lạ", fontSize = 13.sp, color = Color.Gray)
            }
            if (call.isScam) Text("Rủi ro: ${call.riskScore}%", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}
