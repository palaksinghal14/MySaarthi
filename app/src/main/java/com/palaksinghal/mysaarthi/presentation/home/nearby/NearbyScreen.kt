package com.palaksinghal.mysaarthi.presentation.nearby

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.palaksinghal.mysaarthi.R
import com.palaksinghal.mysaarthi.domain.model.NearbySeeker
import com.palaksinghal.mysaarthi.domain.model.NearbyTemple
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
import com.palaksinghal.mysaarthi.presentation.theme.TextInk
import com.palaksinghal.mysaarthi.presentation.util.toUserMessage

// Represents whichever marker/list item is currently selected — either type
private sealed class SelectedPlace {
    data class Temple(val temple: NearbyTemple) : SelectedPlace()
    data class Seeker(val seeker: NearbySeeker) : SelectedPlace()
}

@Composable
fun NearbyScreen(
    viewModel: NearbyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedPlace by remember { mutableStateOf<SelectedPlace?>(null) }

  //  val mapStyleOptions = remember {
  //      MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
  //  }

    Box(modifier = Modifier.fillMaxSize().background(Bg)) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    color = Accent,
                    modifier = Modifier.size(32.dp).align(Alignment.Center),
                    strokeWidth = 2.dp
                )
            }
            uiState.error != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Couldn't load nearby", fontFamily = CaprasimoFamily, fontSize = 20.sp, color = TextInk, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(uiState.error!!.toUserMessage(), fontFamily = FigtreeFamily, fontSize = 14.sp, color = Neutral700, textAlign = TextAlign.Center)
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Nearby", fontFamily = CaprasimoFamily, fontWeight = FontWeight.Normal, fontSize = 28.sp, color = TextInk)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FilterChip("Places", Icons.Filled.LocationOn, uiState.selectedFilter == NearbyFilter.PLACES) { viewModel.selectFilter(NearbyFilter.PLACES) }
                            FilterChip("Seekers", Icons.Filled.Person, uiState.selectedFilter == NearbyFilter.SEEKERS) { viewModel.selectFilter(NearbyFilter.SEEKERS) }
                        }
                    }

                    Box {
                        val userLatLng = LatLng(uiState.userLat, uiState.userLng)
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(userLatLng, 13f)
                        }

                        GoogleMap(
                            modifier = Modifier.fillMaxWidth().height(320.dp),
                            cameraPositionState = cameraPositionState,
                            properties = MapProperties(isMyLocationEnabled = false, /*mapStyleOptions = mapStyleOptions*/),
                            uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false, mapToolbarEnabled = false)
                        ) {
                            MarkerComposable(state = MarkerState(position = userLatLng)) {
                                Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(Bg), contentAlignment = Alignment.Center) {
                                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Accent))
                                }
                            }

                            if (uiState.selectedFilter == NearbyFilter.PLACES) {
                                uiState.temples.forEach { temple ->
                                    MarkerComposable(
                                        state = MarkerState(position = LatLng(temple.lat, temple.lng)),
                                        onClick = {
                                            selectedPlace = SelectedPlace.Temple(temple)
                                            true // consume the click, don't move camera
                                        }
                                    ) { TempleMarkerIcon() }
                                }
                            }

                            if (uiState.selectedFilter == NearbyFilter.SEEKERS) {
                                uiState.seekers.forEach { seeker ->
                                    MarkerComposable(
                                        state = MarkerState(position = LatLng(seeker.lat, seeker.lng)),
                                        onClick = {
                                            selectedPlace = SelectedPlace.Seeker(seeker)
                                            true
                                        }
                                    ) { SeekerMarkerIcon() }
                                }
                            }
                        }

                        // Info card overlay — shows on top of the map when something is selected
                        selectedPlace?.let { place ->
                            Box(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)) {
                                PlaceInfoCard(
                                    place = place,
                                    onDismiss = { selectedPlace = null },
                                    onGetDirections = { lat, lng ->
                                        val uri = Uri.parse("google.navigation:q=$lat,$lng")
                                        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                            setPackage("com.google.android.apps.maps")
                                        }
                                        try {
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            // Google Maps app not installed — fall back to browser
                                            val browserUri = Uri.parse(
                                                "https://www.google.com/maps/dir/?api=1&destination=$lat,$lng"
                                            )
                                            context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
                                        }
                                    }
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (uiState.selectedFilter == NearbyFilter.PLACES) {
                            if (uiState.temples.isEmpty()) item { EmptyStateText("No spiritual places found nearby.") }
                            else items(uiState.temples) { temple ->
                                TempleListItem(temple, onClick = { selectedPlace = SelectedPlace.Temple(temple) })
                            }
                        } else {
                            if (uiState.seekers.isEmpty()) item { EmptyStateText("No seekers found nearby yet.") }
                            else items(uiState.seekers) { seeker ->
                                SeekerListItem(seeker, onClick = { selectedPlace = SelectedPlace.Seeker(seeker) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceInfoCard(
    place: SelectedPlace,
    onDismiss: () -> Unit,
    onGetDirections: (lat: Double, lng: Double) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Bg,
        border = BorderStroke(1.dp, Neutral300)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                when (place) {
                    is SelectedPlace.Temple -> {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(place.temple.name, fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextInk)
                            Text(place.temple.address, fontFamily = FigtreeFamily, fontSize = 12.sp, color = Neutral400)
                            Text("%.1f km away".format(place.temple.distanceKm), fontFamily = FigtreeFamily, fontSize = 12.sp, color = Accent)
                        }
                    }
                    is SelectedPlace.Seeker -> {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${place.seeker.displayName} · ${place.seeker.howLongOnPath}", fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextInk)
                            if (place.seeker.spiritualIntro.isNotBlank()) {
                                Text("\"${place.seeker.spiritualIntro}\"", fontFamily = FigtreeFamily, fontSize = 12.sp, color = Neutral700)
                            }
                            Text("%.1f km away".format(place.seeker.distanceKm), fontFamily = FigtreeFamily, fontSize = 12.sp, color = Accent)
                        }
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = Neutral400)
                }
            }

            // "Get Directions" only makes sense for temples (a real destination)
            // — not for seekers, since we're not sharing exact location for privacy
            if (place is SelectedPlace.Temple) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onGetDirections(place.temple.lat, place.temple.lng) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Text("Get Directions", fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, color = Bg)
                }
            }
        }
    }
}

@Composable
private fun FilterChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (isSelected) Accent else Surface,
        border = BorderStroke(1.dp, if (isSelected) Accent else Neutral400),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = if (isSelected) Bg else TextInk, modifier = Modifier.size(16.dp))
            Text(label, fontFamily = FigtreeFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = if (isSelected) Bg else TextInk)
        }
    }
}

@Composable
private fun TempleMarkerIcon() {
    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Accent), contentAlignment = Alignment.Center) {
        Icon(Icons.Filled.LocationOn, null, tint = Bg, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun SeekerMarkerIcon() {
    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Sage600), contentAlignment = Alignment.Center) {
        Icon(Icons.Filled.Person, null, tint = Bg, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun TempleListItem(temple: NearbyTemple, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(Terracotta100), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.LocationOn, null, tint = Accent, modifier = Modifier.size(22.dp))
        }
        Column {
            Text(temple.name, fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextInk)
            Text("${temple.address} · %.1f km".format(temple.distanceKm), fontFamily = FigtreeFamily, fontSize = 12.sp, color = Neutral400)
        }
    }
}

@Composable
private fun SeekerListItem(seeker: NearbySeeker, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(Sage100), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Person, null, tint = Sage600, modifier = Modifier.size(22.dp))
        }
        Column {
            Text("${seeker.displayName} · ${seeker.howLongOnPath}", fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextInk)
            Text("%.1f km · open to satsang".format(seeker.distanceKm), fontFamily = FigtreeFamily, fontSize = 12.sp, color = Neutral400)
        }
    }
}

@Composable
private fun EmptyStateText(text: String) {
    Text(text, fontFamily = FigtreeFamily, fontSize = 14.sp, color = Neutral400)
}