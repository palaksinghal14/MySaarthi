package com.palaksinghal.mysaarthi.presentation.nearby

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palaksinghal.mysaarthi.domain.model.AppException
import com.palaksinghal.mysaarthi.domain.model.SatsangRequestStatus
import com.palaksinghal.mysaarthi.domain.repository.LocationRepository
import com.palaksinghal.mysaarthi.domain.repository.NearbyRepository
import com.palaksinghal.mysaarthi.domain.repository.SatsangRequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DEFAULT_RADIUS_KM = 10.0

@HiltViewModel
class NearbyViewModel @Inject constructor(
    private val nearbyRepository: NearbyRepository,
    private val locationRepository: LocationRepository,
    private val satsangRequestRepository: SatsangRequestRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NearbyUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadNearbyData()
        observeOutgoingRequests()
    }

    fun loadNearbyData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val myLocationDeferred = async { locationRepository.getUserLocation() }
            val seekersDeferred = async { nearbyRepository.getNearbySeekers(DEFAULT_RADIUS_KM) }
            val templesDeferred = async { nearbyRepository.getNearbyTemples(DEFAULT_RADIUS_KM) }

            val myLocationResult = myLocationDeferred.await()
            val seekersResult = seekersDeferred.await()
            val templesResult = templesDeferred.await()

            var combinedError: AppException? = null

            myLocationResult.onSuccess { location ->
                _uiState.update { it.copy(userLat = location.lat, userLng = location.lng) }
            }

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

            _uiState.update { it.copy(isLoading = false, error = combinedError) }
        }
    }

    fun selectFilter(filter: NearbyFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun retry() {
        loadNearbyData()
    }

    fun observeOutgoingRequests(){
        viewModelScope.launch {
            satsangRequestRepository.getOutgoingSatsangReq().collect { requests ->
                val statusMap = requests.associate { it.toUid to it.status }
                _uiState.update { it.copy(sentRequestStatuses = statusMap) }
            }
        }
    }

    fun sendSatsangRequest(toUid: String, toDisplayName: String) {
        viewModelScope.launch {
            satsangRequestRepository.sendSatsangReq(toUid, toDisplayName)
                .onSuccess {
                    _uiState.update { it.copy( sentRequestStatuses = it.sentRequestStatuses + (toUid to SatsangRequestStatus.PENDING)
                    ) }
                }
        }
    }
}