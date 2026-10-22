package com.palaksinghal.mysaarthi.presentation.home.connect.chat

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palaksinghal.mysaarthi.domain.model.ChatMessage
import com.palaksinghal.mysaarthi.presentation.theme.Accent
import com.palaksinghal.mysaarthi.presentation.theme.Bg
import com.palaksinghal.mysaarthi.presentation.theme.CaprasimoFamily
import com.palaksinghal.mysaarthi.presentation.theme.Divider
import com.palaksinghal.mysaarthi.presentation.theme.FigtreeFamily
import com.palaksinghal.mysaarthi.presentation.theme.Neutral200
import com.palaksinghal.mysaarthi.presentation.theme.Neutral300
import com.palaksinghal.mysaarthi.presentation.theme.Neutral400
import com.palaksinghal.mysaarthi.presentation.theme.Neutral500
import com.palaksinghal.mysaarthi.presentation.theme.Neutral600
import com.palaksinghal.mysaarthi.presentation.theme.Neutral700
import com.palaksinghal.mysaarthi.presentation.theme.Surface
import com.palaksinghal.mysaarthi.presentation.theme.TextInk
import com.palaksinghal.mysaarthi.presentation.util.toUserMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    onBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showReportDialog by remember { mutableStateOf(false) }

    // The AppException lives in state; toUserMessage() is @Composable so it resolves here
    val error = uiState.errorMessage
    if (error != null) {
        val message = error.toUserMessage()
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
            viewModel.onErrorShown()
        }
    }

    val info = uiState.infoMessage
    if (info != null) {
        LaunchedEffect(info) {
            snackbarHostState.showSnackbar(info)
            viewModel.onInfoShown()
        }
    }

    // Load older when scrolled near the top (highest index, because reverseLayout)
    val shouldLoadOlder by remember {
        derivedStateOf {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            last != null && last.index >= uiState.messages.size - 5
        }
    }
    LaunchedEffect(shouldLoadOlder) {
        Log.d("Pagination", "shouldLoadOlder=$shouldLoadOlder size=${uiState.messages.size}")
        if (shouldLoadOlder && uiState.messages.isNotEmpty()) viewModel.onLoadOlder()
    }

    // Scroll on my own send always; on an incoming one only if already near the bottom
    LaunchedEffect(uiState.messages.firstOrNull()?.id) {
        val newest = uiState.messages.firstOrNull() ?: return@LaunchedEffect
        if (newest.isMine || listState.firstVisibleItemIndex <= 2) {
            listState.animateScrollToItem(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
    ) {
        ChatTopBar(
            name = uiState.otherUserName,
            isBlockedByMe = uiState.isBlockedByMe,
            onBack = onBack,
            onToggleBlock = viewModel::onToggleBlock,
            onReportClick = { showReportDialog = true }
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                uiState.isLoading -> Box(
                    modifier = Modifier.fillMaxSize().background(Bg),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Accent)
                }

                uiState.isEmpty -> Column(
                    modifier = Modifier.fillMaxSize().background(Bg).padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No messages yet",
                        fontFamily = CaprasimoFamily,
                        fontSize = 18.sp,
                        color = TextInk
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Say Namaste 🙏 and begin the conversation.",
                        fontFamily = FigtreeFamily,
                        fontSize = 14.sp,
                        color = Neutral600,
                        textAlign = TextAlign.Center
                    )
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize().background(Bg),
                    state = listState,
                    reverseLayout = true,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    items(
                        items = uiState.messages,
                        key = { it.id }
                    ) { message ->
                        MessageBubble(message = message)
                    }

                    if (uiState.isLoadingOlder) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Accent,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        ChatInputBar(
            draft = uiState.draft,
            canSend = uiState.canSend,
            isBlockedByMe = uiState.isBlockedByMe,
            onDraftChange = viewModel::onDraftChange,
            onSendClick = viewModel::onSendClick
        )

    }

    if (showReportDialog) {
        ReportDialog(
            onDismiss = { showReportDialog = false },
            onSubmit = { reason ->
                showReportDialog = false
                viewModel.onReport(reason)
            }
        )
    }
}

@Composable
private fun ChatTopBar(
    name: String,
    isBlockedByMe: Boolean,
    onBack: () -> Unit,
    onToggleBlock: () -> Unit,
    onReportClick: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().background(Bg)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextInk
                )
            }

            Spacer(Modifier.size(4.dp))

            Text(
                text = name.ifBlank { "Seeker" },
                fontFamily = CaprasimoFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                color = TextInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = TextInk
                    )
                }
                DropdownMenu(
                    expanded = menuOpen,
                    onDismissRequest = { menuOpen = false },
                    modifier = Modifier.background(Surface)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (isBlockedByMe) "Unblock" else "Block",
                                fontFamily = FigtreeFamily,
                                fontSize = 14.sp,
                                color = TextInk
                            )
                        },
                        colors = MenuDefaults.itemColors(textColor = TextInk),
                        onClick = {
                            menuOpen = false
                            onToggleBlock()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Report",
                                fontFamily = FigtreeFamily,
                                fontSize = 14.sp,
                                color = TextInk
                            )
                        },
                        colors = MenuDefaults.itemColors(textColor = TextInk),
                        onClick = {
                            menuOpen = false
                            onReportClick()
                        }
                    )
                }
            }
        }

        HorizontalDivider(color = Divider, thickness = 1.dp)
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (message.isMine) Accent else Surface,
            border = if (message.isMine) null else BorderStroke(1.dp, Neutral300),
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (message.isMine) 18.dp else 4.dp,
                bottomEnd = if (message.isMine) 4.dp else 18.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)) {
                Text(
                    text = message.msg,
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = if (message.isMine) Bg else TextInk
                )
                Spacer(Modifier.height(3.dp))
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = formatTime(message.sentAt),
                        fontFamily = FigtreeFamily,
                        fontSize = 10.sp,
                        color = if (message.isMine) Bg.copy(alpha = 0.75f) else Neutral600
                    )
                    if (message.isMine) {
                        Icon(
                            imageVector = if (message.isPending) Icons.Default.Schedule
                            else Icons.Default.Check,
                            contentDescription = if (message.isPending) "Sending" else "Sent",
                            modifier = Modifier.size(12.dp),
                            tint = Bg.copy(alpha = 0.75f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    draft: String,
    canSend: Boolean,
    isBlockedByMe: Boolean,
    onDraftChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
            .consumeWindowInsets(WindowInsets.navigationBars)
            .imePadding()

    ) {
        HorizontalDivider(color = Divider, thickness = 1.dp)

        if (isBlockedByMe) {
            Text(
                text = "You blocked this person. Unblock to continue the conversation.",
                fontFamily = FigtreeFamily,
                fontSize = 12.sp,
                color = Neutral700,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = onDraftChange,
                enabled = canSend,
                placeholder = {
                    Text(
                        text = "Message",
                        fontFamily = FigtreeFamily,
                        fontSize = 14.sp,
                        color = Neutral500
                    )
                },
                maxLines = 5,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextInk,
                    unfocusedTextColor = TextInk,
                    disabledTextColor = Neutral600,
                    focusedContainerColor = Bg,
                    unfocusedContainerColor = Bg,
                    disabledContainerColor = Neutral200,
                    cursorColor = Accent,
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = Neutral300,
                    disabledBorderColor = Neutral300
                ),
                modifier = Modifier.weight(1f)
            )

            Surface(
                shape = RoundedCornerShape(50),
                color = if (canSend && draft.isNotBlank()) Accent else Neutral300,
                modifier = Modifier.size(48.dp)
            ) {
                IconButton(
                    onClick = onSendClick,
                    enabled = canSend && draft.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Bg,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    val reasons = listOf(
        "Spam or scam",
        "Harassment or abuse",
        "Inappropriate content",
        "Something else"
    )
    var selected by remember { mutableStateOf(reasons.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Bg,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Report this person",
                fontFamily = CaprasimoFamily,
                fontSize = 18.sp,
                color = TextInk
            )
        },
        text = {
            Column {
                reasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selected == reason,
                                onClick = { selected = reason }
                            )
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected == reason,
                            onClick = { selected = reason },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Accent,
                                unselectedColor = Neutral400
                            )
                        )
                        Text(
                            text = reason,
                            fontFamily = FigtreeFamily,
                            fontSize = 14.sp,
                            color = TextInk
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSubmit(selected) }
            ) {
                Text("Submit", fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp ,color = Accent)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel", fontFamily = FigtreeFamily, fontSize = 14.sp, color = Accent, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

private fun formatTime(millis: Long): String =
    SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))