    package com.palaksinghal.mysaarthi.presentation.home

    import com.palaksinghal.mysaarthi.domain.model.AppException
    import com.palaksinghal.mysaarthi.domain.model.SadhanaEntry
    import com.palaksinghal.mysaarthi.domain.model.Shloka

    data class HomeUiState(
        val isLoading:Boolean=true,
        val error : AppException?=null,
        val shloka: Shloka?=null,
        val sadhanaEntries :List<SadhanaEntry> =emptyList(),
        val displayName:String="",
        val isEvening:Boolean =false
    )
