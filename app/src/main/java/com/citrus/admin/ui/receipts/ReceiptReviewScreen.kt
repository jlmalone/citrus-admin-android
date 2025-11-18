package com.citrus.admin.ui.receipts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.citrus.admin.data.local.entity.ReceiptEntity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptReviewScreen(
    viewModel: ReceiptViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Receipt Review") },
                actions = {
                    IconButton(onClick = { viewModel.syncReceipts() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter Section
            FilterSection(
                selectedFilter = uiState.selectedFilter,
                onFilterChanged = viewModel::onFilterChanged
            )

            // Receipt List
            when {
                uiState.isLoading && uiState.receipts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.receipts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No receipts found")
                    }
                }
                else -> {
                    ReceiptList(
                        receipts = uiState.receipts,
                        onApprove = viewModel::approveReceipt,
                        onReject = viewModel::rejectReceipt
                    )
                }
            }
        }
    }
}

@Composable
fun FilterSection(
    selectedFilter: ReceiptFilter,
    onFilterChanged: (ReceiptFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == ReceiptFilter.PENDING,
            onClick = { onFilterChanged(ReceiptFilter.PENDING) },
            label = { Text("Pending") },
            leadingIcon = {
                if (selectedFilter == ReceiptFilter.PENDING) {
                    Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp))
                }
            }
        )
        FilterChip(
            selected = selectedFilter == ReceiptFilter.APPROVED,
            onClick = { onFilterChanged(ReceiptFilter.APPROVED) },
            label = { Text("Approved") },
            leadingIcon = {
                if (selectedFilter == ReceiptFilter.APPROVED) {
                    Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp))
                }
            }
        )
        FilterChip(
            selected = selectedFilter == ReceiptFilter.REJECTED,
            onClick = { onFilterChanged(ReceiptFilter.REJECTED) },
            label = { Text("Rejected") },
            leadingIcon = {
                if (selectedFilter == ReceiptFilter.REJECTED) {
                    Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp))
                }
            }
        )
        FilterChip(
            selected = selectedFilter == ReceiptFilter.ALL,
            onClick = { onFilterChanged(ReceiptFilter.ALL) },
            label = { Text("All") },
            leadingIcon = {
                if (selectedFilter == ReceiptFilter.ALL) {
                    Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp))
                }
            }
        )
    }
}

@Composable
fun ReceiptList(
    receipts: List<ReceiptEntity>,
    onApprove: (String, String?) -> Unit,
    onReject: (String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(receipts, key = { it.id }) { receipt ->
            ReceiptCard(
                receipt = receipt,
                onApprove = { notes -> onApprove(receipt.id, notes) },
                onReject = { notes -> onReject(receipt.id, notes) }
            )
        }
    }
}

@Composable
fun ReceiptCard(
    receipt: ReceiptEntity,
    onApprove: (String?) -> Unit,
    onReject: (String) -> Unit
) {
    var showRejectDialog by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance() }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = receipt.userName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = dateFormat.format(Date(receipt.submittedAt)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AssistChip(
                    onClick = { },
                    label = { Text(receipt.status.uppercase()) },
                    leadingIcon = {
                        Icon(
                            when (receipt.status) {
                                "approved" -> Icons.Default.CheckCircle
                                "rejected" -> Icons.Default.Close
                                else -> Icons.Default.Info
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Receipt Image
            receipt.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Receipt image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = currencyFormat.format(receipt.amount),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = receipt.category,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = receipt.description,
                style = MaterialTheme.typography.bodyMedium
            )

            receipt.notes?.let { notes ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Notes: $notes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Action Buttons (only for pending receipts)
            if (receipt.status == "pending") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showRejectDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject")
                    }
                    Button(
                        onClick = { onApprove(null) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Approve")
                    }
                }
            }
        }
    }

    if (showRejectDialog) {
        RejectDialog(
            onDismiss = { showRejectDialog = false },
            onConfirm = { notes ->
                onReject(notes)
                showRejectDialog = false
            }
        )
    }
}

@Composable
fun RejectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reject Receipt") },
        text = {
            Column {
                Text("Please provide a reason for rejection:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Rejection reason...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(notes) },
                enabled = notes.isNotBlank()
            ) {
                Text("Reject")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
