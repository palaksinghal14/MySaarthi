package com.palaksinghal.mysaarthi.presentation.nearby

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.SatsangRequestStatus
import com.palaksinghal.mysaarthi.domain.model.otherPerson
import com.palaksinghal.mysaarthi.domain.repository.AuthenticationRepo
import com.palaksinghal.mysaarthi.domain.repository.LocationRepository
import com.palaksinghal.mysaarthi.domain.repository.NearbyRepository
import com.palaksinghal.mysaarthi.domain.repository.SatsangRequestRepository
import com.palaksinghal.mysaarthi.domain.repository.UserProfileRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DEFAULT_RADIUS_KM = 10.0

@HiltViewModel
class NearbyViewModel @Inject constructor(
    private val nearbyRepository: NearbyRepository,
    private val locationRepository: LocationRepository,
    private val satsangRequestRepository: SatsangRequestRepository,
    private val authRepo : AuthenticationRepo,
    private val userProfileRepo: UserProfileRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(NearbyUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadNearbyData()
        observeConnectionStates()
    }

    fun loadNearbyData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val myUid = authRepo.getCurrentUserId()
            val isOpenToSatsang = myUid?.let {
                userProfileRepo.getUserProfile(it).getOrNull()?.isOpenToSatsang
            } ?: false

            val myLocationDeferred = async { locationRepository.getUserLocation() }
            val seekersDeferred = if (isOpenToSatsang) {
                async { nearbyRepository.getNearbySeekers(DEFAULT_RADIUS_KM) }
            } else {
                null
            }
            val templesDeferred = async { nearbyRepository.getNearbyTemples(DEFAULT_RADIUS_KM) }

            val myLocationResult = myLocationDeferred.await()
            val seekersResult = seekersDeferred?.await()
            val templesResult = templesDeferred.await()

            var combinedError: AppException? = null

            myLocationResult.onSuccess { location ->
                _uiState.update { it.copy(userLat = location.lat, userLng = location.lng) }
            }

            if(seekersResult!=null) {
                seekersResult
                    .onSuccess { seekers ->
                        Log.d("Nearby", "Seekers success: ${seekers.size}")
                        _uiState.update { it.copy(seekers = seekers) }
                    }
                    .onFailure { throwable ->
                        Log.e("Nearby", "Seekers failed: ${throwable.message}")
                        combinedError = throwable as? AppException
                            ?: AppException.UnknownException(throwable.message)
                    }
            }else{
                // Not open to satsang — no seekers to show, and this isn't an error state.
                _uiState.update { it.copy(seekers = emptyList()) }
            }
            templesResult
                .onSuccess { temples ->
                    Log.d("Nearby", "Temples success: ${temples.size}")
                    _uiState.update { it.copy(temples = temples) }
                }
                .onFailure { throwable ->
                    Log.e("Nearby", "Temples failed: ${throwable.message}", throwable)
                    if (combinedError == null) {
                        combinedError = throwable as? AppException
                            ?: AppException.UnknownException(throwable.message)
                    }
                }

            _uiState.update { it.copy(isLoading = false, error = combinedError,isOpenToSatsang=isOpenToSatsang) }
        }
    }

    fun selectFilter(filter: NearbyFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun retry() {
        loadNearbyData()
    }

    private fun observeConnectionStates() {
        viewModelScope.launch {
            val myUid = authRepo.getCurrentUserId() ?: return@launch // inject AuthenticationRepo if not already present

            // Combine outgoing requests + connections into one state map
            combine(
                satsangRequestRepository.getOutgoingSatsangReq(),
                satsangRequestRepository.getConnectedUsers()
            ) { outgoing, connected ->
                val stateMap = mutableMapOf<String, SeekerConnectionState>()

                outgoing.forEach { req ->
                    stateMap[req.toUid] = when (req.status) {
                        SatsangRequestStatus.PENDING -> SeekerConnectionState.PENDING
                        SatsangRequestStatus.DECLINED -> SeekerConnectionState.DECLINED
                        SatsangRequestStatus.ACCEPTED -> SeekerConnectionState.CONNECTED
                    }
                }

                connected.forEach { conn ->
                    val (otherUid, _) = conn.otherPerson(myUid)
                    stateMap[otherUid] = SeekerConnectionState.CONNECTED
                }

                stateMap
            }.catch {}
                .collect { stateMap ->
                _uiState.update { it.copy(seekerConnectionStates = stateMap) }
            }
        }
    }

    fun sendSatsangRequest(toUid: String, toDisplayName: String) {
        // Safety net — the UI shouldn't expose a send action when the user
        // themselves is closed to satsang, since they won't even see seekers.
        // The Firestore rule is the real enforcement; this just avoids firing
        // a request that the server will reject anyway.
        if (!_uiState.value.isOpenToSatsang) return
        viewModelScope.launch {
            satsangRequestRepository.sendSatsangReq(toUid, toDisplayName)
                .onSuccess {
                    _uiState.update { it.copy(  seekerConnectionStates = it.seekerConnectionStates + (toUid to SeekerConnectionState.PENDING)
                    ) }
                }
        }
    }
}