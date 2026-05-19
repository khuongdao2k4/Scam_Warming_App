package com.example.scam_warming_app.presentation.detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    id: Long,
    type: String, // "sms" or "call"
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isBlocking by viewModel.isBlocking.collectAsState()

    LaunchedEffect(id) {
        if (type == "sms") {
            viewModel.loadSmsDetail(id)
        } else {
            viewModel.loadCallDetail(id)
        }
    }

    val smsDetail by viewModel.smsDetail.collectAsState()
    val callDetail by viewModel.callDetail.collectAsState()

    val title = if (type == "sms") "Chi tiết tin nhắn" else "Chi tiết cuộc gọi"
    val sender = if (type == "sms") smsDetail?.sender else callDetail?.phoneNumber
    val timestamp = if (type == "sms") smsDetail?.timestamp else callDetail?.timestamp
    val isScam = if (type == "sms") smsDetail?.isScam == true else callDetail?.isScam == true
    val category = if (type == "sms") smsDetail?.category else callDetail?.category
    val riskScore = if (type == "sms") smsDetail?.riskScore else callDetail?.riskScore
    val content = if (type == "sms") smsDetail?.message else callDetail?.transcript
    val reasons = if (type == "sms") smsDetail?.reasons?.split(", ") ?: emptyList() else listOf("Phân tích dựa trên hành vi hội thoại")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Card with Risk Level
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isScam) Color(0xFFB71C1C) else Color(0xFF2E7D32)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isScam) Icons.Rounded.ReportProblem else Icons.Rounded.VerifiedUser,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isScam) "CẢNH BÁO LỪA ĐẢO" else "AN TOÀN",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Mức độ rủi ro: $riskScore%",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Information Section
            InfoRow(label = "Đối tượng:", value = sender ?: "Không rõ", icon = Icons.Rounded.Person)
            InfoRow(
                label = "Thời gian:", 
                value = formatTimestamp(timestamp ?: 0L), 
                icon = Icons.Rounded.AccessTime
            )
            InfoRow(label = "Phân loại:", value = category ?: "Chưa xác định", icon = Icons.Rounded.Category)

            Spacer(modifier = Modifier.height(24.dp))

            // Content Section
            Text(
                text = if (type == "sms") "Nội dung tin nhắn:" else "Nội dung cuộc gọi (Transcript):",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Text(
                    text = content ?: "Không có nội dung dữ liệu.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AI Reasons Section
            if (isScam) {
                Text(
                    text = "Lý do AI cảnh báo:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB71C1C)
                )
                reasons.forEach { reason ->
                    Row(
                        modifier = Modifier.padding(top = 8.dp, start = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Circle, contentDescription = null, modifier = Modifier.size(8.dp), tint = Color(0xFFB71C1C))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = reason, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (isScam) {
                Button(
                    onClick = { 
                        sender?.let { phone ->
                            viewModel.blockNumber(phone) { success ->
                                if (success) {
                                    Toast.makeText(context, "Đã chặn số thành công", Toast.LENGTH_SHORT).show()
                                    onBack()
                                } else {
                                    Toast.makeText(context, "Lỗi: Không thể chặn số này", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isBlocking
                ) {
                    if (isBlocking) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Icon(Icons.Rounded.Block, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chặn số điện thoại này", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    if (timestamp == 0L) return "Không rõ"
    val sdf = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
