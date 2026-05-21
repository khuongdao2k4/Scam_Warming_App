package com.example.scam_warming_app.presentation.blacklist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlacklistScreen(
    viewModel: BlacklistViewModel = hiltViewModel()
) {
    val blacklist by viewModel.blacklist.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = if (searchQuery.isEmpty()) {
        blacklist
    } else {
        blacklist.filter { it.phoneNumber.contains(searchQuery) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Cơ sở dữ liệu", fontWeight = FontWeight.Black)
                        Text("${blacklist.size} đầu số lừa đảo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.syncBlacklist() }) {
                        Icon(Icons.Rounded.Sync, contentDescription = "Cập nhật")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // 1. Thanh tìm kiếm
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Tìm kiếm số điện thoại...") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = Color.White
                )
            )

            // 2. Danh sách cuộn
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                items(filteredList) { entry ->
                    ListItem(
                        headlineContent = { Text(entry.phoneNumber, fontWeight = FontWeight.Bold) },
                        supportingContent = { 
                            Column {
                                Text(entry.category, fontSize = 12.sp)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Group,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Cộng đồng báo cáo: ${entry.reportedCount} lần",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        },
                        trailingContent = { 
                            Badge(containerColor = Color.Red.copy(alpha = 0.1f)) {
                                Text("RỦI RO: ${entry.riskLevel}%", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        leadingContent = {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.Red.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Block, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                            }
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}
