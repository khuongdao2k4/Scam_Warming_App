package com.example.scam_warming_app.presentation.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scam_warming_app.utils.PermissionManager

@Composable
fun PermissionScreen(
    permissionManager: PermissionManager,
    onAllGranted: () -> Unit
) {
    val scrollState = rememberScrollState()

    // 1. Tự động kiểm tra và chuyển hướng nếu đã đủ quyền
    LaunchedEffect(Unit) {
        if (permissionManager.hasAllPermissions()) {
            onAllGranted()
        }

        permissionManager.setOnResultListener { allGranted ->
            if (allGranted) {
                onAllGranted()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.VerifiedUser,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            "Kích hoạt lá chắn",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            "Để bảo vệ bạn khỏi lừa đảo 24/7, ứng dụng cần bạn cấp các quyền truy cập hệ thống dưới đây:",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        PermissionItem(
            title = "Giám sát Cuộc gọi & SMS",
            desc = "Nhận diện số lạ và quét nội dung tin nhắn lừa đảo.",
            icon = Icons.Rounded.PhoneInTalk
        )
        
        PermissionItem(
            title = "Phân tích Giọng nói",
            desc = "Chuyển giọng nói kẻ lừa đảo thành văn bản để xử lý AI.",
            icon = Icons.Rounded.Mic
        )
        
        PermissionItem(
            title = "Danh bạ & Tin cậy",
            desc = "Tự động bỏ qua người thân để đảm bảo riêng tư tuyệt đối.",
            icon = Icons.Rounded.Contacts
        )

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { 
                permissionManager.requestPermissions() 
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Đồng ý và Tiếp tục", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        
        TextButton(onClick = onAllGranted) {
            Text("Để sau (Ứng dụng sẽ bị hạn chế)", color = Color.Gray)
        }
    }
}

@Composable
fun PermissionItem(title: String, desc: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            shape = CircleShape,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
