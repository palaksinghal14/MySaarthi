package com.palaksinghal.mysaarthi.presentation.home.connect

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palaksinghal.mysaarthi.domain.model.Conversation
import com.palaksinghal.mysaarthi.presentation.theme.Accent
import com.palaksinghal.mysaarthi.presentation.theme.Bg
import com.palaksinghal.mysaarthi.presentation.theme.CaprasimoFamily
import com.palaksinghal.mysaarthi.presentation.theme.Divider
import com.palaksinghal.mysaarthi.presentation.theme.FigtreeFamily
import com.palaksinghal.mysaarthi.presentation.theme.Neutral600
import com.palaksinghal.mysaarthi.presentation.theme.TextInk
import com.palaksinghal.mysaarthi.presentation.util.toUserMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConnectScreen(
    onOpenChat: (convId: String, otherUid: String) -> Unit,
    onOpenRequests: () -> Unit,
    viewModel: ConnectViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val error = uiState.error
    if (error != null) {
        val message = error.toUserMessage()
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
            viewModel.onErrorShown()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Bg)){
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Bg)
                    .padding(start = 24.dp, end = 12.dp, top = 20.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Connect",
                    fontFamily = CaprasimoFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 22.sp,
                    color = TextInk,
                    modifier = Modifier.weight(1f)
                )

                Box {
                    IconButton(onClick = onOpenRequests) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Satsang requests",
                            tint = TextInk
                        )
                    }
                    if (uiState.incomingRequestCount > 0) {
                        CountBadge(
                            count = uiState.incomingRequestCount,
                            modifier = Modifier.align(Alignment.TopEnd)
                        )
                    }
                }
            }

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
                        text = "No conversations yet",
                        fontFamily = CaprasimoFamily,
                        fontSize = 18.sp,
                        color = TextInk
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Connect with seekers from the Nearby tab to start a conversation.",
                        fontFamily = FigtreeFamily,
                        fontSize = 14.sp,
                        color = Neutral600,
                        textAlign = TextAlign.Center
                    )
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize().background(Bg)
                ) {
                    items(
                        items = uiState.conversations,
                        key = { it.id }
                    ) { conversation ->
                        ConversationRow(
                            conversation = conversation,
                            onClick = { onOpenChat(conversation.id, conversation.otherUid) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 76.dp),
                            color = Divider,
                            thickness = 1.dp
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ConversationRow(
    conversation: Conversation,
    onClick: () -> Unit
) {
    val unread = conversation.unreadCount > 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InitialsAvatar(name = conversation.otherUserName)

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = conversation.otherUserName.ifBlank { "Seeker" },
                fontFamily = FigtreeFamily,
                fontWeight = if (unread) FontWeight.Bold else FontWeight.SemiBold,
                fontSize = 15.sp,
                color = TextInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = previewText(conversation),
                fontFamily = FigtreeFamily,
                fontWeight = if (unread) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 13.sp,
                color = if (unread) TextInk else Neutral600,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = formatRelative(conversation.lastMsgAt),
                fontFamily = FigtreeFamily,
                fontSize = 11.sp,
                color = if (unread) Accent else Neutral600
            )
            if (unread) {
                CountBadge(count = conversation.unreadCount)
            }
        }
    }
}

@Composable
private fun InitialsAvatar(name: String) {
    Surface(
        shape = CircleShape,
        color = Accent,
        modifier = Modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = name.trim().firstOrNull()?.uppercase() ?: "?",
                fontFamily = CaprasimoFamily,
                fontSize = 18.sp,
                color = Bg
            )
        }
    }
}

@Composable
private fun CountBadge(count: Int, modifier: Modifier = Modifier) {
    Surface(
        shape = CircleShape,
        color = Accent,
        modifier = modifier.size(20.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = if (count > 9) "9+" else count.toString(),
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = Bg
            )
        }
    }
}

private fun previewText(conversation: Conversation): String = when {
    conversation.lastMsg.isBlank() -> "Say Namaste 🙏"
    conversation.isLastMessageMine -> "You: ${conversation.lastMsg}"
    else -> conversation.lastMsg
}

private fun formatRelative(millis: Long): String {
    val now = System.currentTimeMillis()
    val day = 24 * 60 * 60 * 1000L
    return when {
        now - millis < day -> SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))
        now - millis < 7 * day -> SimpleDateFormat("EEE", Locale.getDefault()).format(Date(millis))
        else -> SimpleDateFormat("d MMM", Locale.getDefault()).format(Date(millis))
    }
}