package com.palaksinghal.mysaarthi.presentation.home.profile

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palaksinghal.mysaarthi.domain.model.SatsangRequest
import com.palaksinghal.mysaarthi.domain.model.SatsangRequestStatus
import com.palaksinghal.mysaarthi.domain.model.otherPerson
import com.palaksinghal.mysaarthi.presentation.theme.Accent
import com.palaksinghal.mysaarthi.presentation.theme.Bg
import com.palaksinghal.mysaarthi.presentation.theme.CaprasimoFamily
import com.palaksinghal.mysaarthi.presentation.theme.FigtreeFamily
import com.palaksinghal.mysaarthi.presentation.theme.Neutral300
import com.palaksinghal.mysaarthi.presentation.theme.Neutral400
import com.palaksinghal.mysaarthi.presentation.theme.Neutral700
import com.palaksinghal.mysaarthi.presentation.theme.Sage100
import com.palaksinghal.mysaarthi.presentation.theme.Sage600
import com.palaksinghal.mysaarthi.presentation.theme.Surface
import com.palaksinghal.mysaarthi.presentation.theme.Terracotta100
import com.palaksinghal.mysaarthi.presentation.theme.Terracotta700
import com.palaksinghal.mysaarthi.presentation.theme.TextInk

private enum class RequestTab { INCOMING, OUTGOING,CONNECTED }

@Composable
fun SatsangRequestsScreen(
    onBack: () -> Unit,
    viewModel: YouViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(RequestTab.INCOMING) }

    Column(modifier = Modifier.fillMaxSize().background(Bg)) {

        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextInk)
            }
            Text(
                text = "Satsang Requests",
                fontFamily = CaprasimoFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = TextInk
            )
        }

        // Tab selector
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TabChip(
                label = "Incoming (${uiState.incomingRequests.size})",
                isSelected = selectedTab == RequestTab.INCOMING,
                onClick = { selectedTab = RequestTab.INCOMING }
            )
            TabChip(
                label = "Sent (${uiState.outgoingRequests.size})",
                isSelected = selectedTab == RequestTab.OUTGOING,
                onClick = { selectedTab = RequestTab.OUTGOING }
            )
            TabChip(label="Connected (${uiState.connections.size})",
                isSelected = selectedTab == RequestTab.CONNECTED,
                onClick = { selectedTab = RequestTab.CONNECTED }
            )

        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTab) {
                RequestTab.INCOMING -> {
                    if (uiState.incomingRequests.isEmpty()) {
                        item { EmptyState("No pending requests right now.") }
                    } else {
                        items(uiState.incomingRequests) { request ->
                            IncomingRequestCard(
                                request = request,
                                onAccept = { viewModel.respondToRequest(request.requestId, true) },
                                onDecline = { viewModel.respondToRequest(request.requestId, false) }
                            )
                        }
                    }
                }
                RequestTab.OUTGOING -> {
                    if (uiState.outgoingRequests.isEmpty()) {
                        item { EmptyState("You haven't sent any requests yet.") }
                    } else {
                        items(uiState.outgoingRequests) { request ->
                            OutgoingRequestCard(
                                request = request
                            )
                        }
                    }
                }
                RequestTab.CONNECTED -> {
                    if (uiState.connections.isEmpty()) {
                        item { EmptyState("No connections yet. Accepted requests will show up here.") }
                    } else {
                        items(uiState.connections) { connection ->
                            ConnectionCard(
                                connection = connection,
                                myUid = uiState.uid,
                                getContactEmail = { uid -> viewModel.getContactEmail(uid) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (isSelected) Accent else Surface,
        border = BorderStroke(1.dp, if (isSelected) Accent else Neutral300),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontFamily = FigtreeFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = if (isSelected) Bg else TextInk,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun IncomingRequestCard(
    request: SatsangRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Terracotta100,
        border = BorderStroke(1.dp, Accent)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${request.fromDisplayName} wants to connect",
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Terracotta700
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = Accent),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Accept", fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, color = Bg, fontSize = 13.sp)
                }
                Button(
                    onClick = onDecline,
                    colors = ButtonDefaults.buttonColors(containerColor = Surface),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Decline", fontFamily = FigtreeFamily, fontWeight = FontWeight.Medium, color = Neutral700, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun OutgoingRequestCard(request: SatsangRequest) {

    val (statusLabel, statusColor) = when (request.status) {
        SatsangRequestStatus.PENDING -> "Pending" to Neutral400
        SatsangRequestStatus.ACCEPTED -> "Accepted" to Accent
        SatsangRequestStatus.DECLINED -> "Declined" to Neutral400
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(1.dp, Neutral300)
    ) {

        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Request to ${request.toDisplayName}",
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = TextInk
            )
            Text(
                text = statusLabel,
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = statusColor
            )
        }
    }
}

@Composable
private fun ConnectionCard(
    connection: SatsangRequest,
    myUid: String,
    getContactEmail: suspend (String) -> String
) {
    val (otherUid, otherName) = connection.otherPerson(myUid)
    var email by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(otherUid) {
        email = getContactEmail(otherUid)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Sage100,
        border = BorderStroke(1.dp, Sage600)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = otherName,
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = TextInk
            )
            if (!email.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Contact: $email",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = Sage600
                )
            }
        }
    }
}

@Composable
private fun EmptyState(text: String) {
    Text(text = text, fontFamily = FigtreeFamily, fontSize = 14.sp, color = Neutral400)
}