package com.example.scam_warming_app.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToTrusted: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isCallEnabled by viewModel.isCallProtectionEnabled.collectAsState()
    val isSmsEnabled by viewModel.isSmsProtectionEnabled.collectAsState()
    val riskThreshold by viewModel.riskThreshold.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cấu hình bảo vệ", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState()) // Đảm bảo toàn bộ trang có thể cuộn
                .padding(16.dp)
        ) {
            Text(
                "Trạng thái giám sát",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            SettingsToggleItem(
                title = "Bảo vệ cuộc gọi",
                description = "Tự động phân tích nội dung cuộc gọi từ số lạ",
                icon = Icons.Rounded.PhoneInTalk,
                checked = isCallEnabled,
                onCheckedChange = { viewModel.toggleCallProtection(it) }
            )

            SettingsToggleItem(
                title = "Bảo vệ tin nhắn",
                description = "Quét link độc hại và từ khóa lừa đảo trong SMS",
                icon = Icons.Rounded.Sms,
                checked = isSmsEnabled,
                onCheckedChange = { viewModel.toggleSmsProtection(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Vùng an toàn",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            SettingsActionItem(
                title = "Số điện thoại tin cậy",
                description = "Danh sách người thân, shipper... không cần quét AI",
                icon = Icons.Rounded.VerifiedUser,
                onClick = onNavigateToTrusted
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Độ nhạy của AI",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Ngưỡng cảnh báo: $riskThreshold%", fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = riskThreshold.toFloat(),
                        onValueChange = { viewModel.setRiskThreshold(it.toInt()) },
                        valueRange = 0f..100f,
                        steps = 10,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        "Gợi ý: 60% là mức cân bằng giữa bảo mật và tránh làm phiền.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Hệ thống",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            SettingsActionItem(
                title = "Cập nhật dữ liệu lừa đảo",
                description = if (isSyncing) "Đang tải dữ liệu..." else "Tải về danh sách mới nhất từ cộng đồng",
                icon = Icons.Rounded.Sync,
                trailing = {
                    if (isSyncing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    }
                },
                onClick = {
                    if (!isSyncing) {
                        viewModel.syncData { success ->
                            if (success) {
                                Toast.makeText(context, "Đã cập nhật dữ liệu mới nhất!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            )
            
            // QUAN TRỌNG: Thêm khoảng trống cuối để không bị che bởi NavigationBar
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SettingsToggleItem(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
fun SettingsActionItem(
    title: String,
    description: String,
    icon: ImageVector,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (trailing != null) {
                trailing()
            } else {
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
