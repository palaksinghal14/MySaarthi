package com.palaksinghal.mysaarthi.domain.model

sealed class AppException:Exception(){
    // Network
    object NoInternetException : AppException()
    object TimeoutException : AppException()
    data class ServerException(val code: Int) : AppException()

    // Firebase Auth
    object UserNotFoundException : AppException()
    object WrongPasswordException : AppException()
    object EmailAlreadyExistsException : AppException()
    object FirebaseUnavailableException : AppException()
    object GoogleSignInException : AppException()

    // Firestore
    object FirestoreNotFoundException : AppException()
    object FirestorePermissionDeniedException : AppException()

    // Room (local database)
    object DatabaseException : AppException()

    // Gita API (Retrofit)
    object GitaDataParseException : AppException()

    // Nearby / Places / GeoFire
    object LocationPermissionDeniedException : AppException()
    object PlacesApiException : AppException()
    object NoNearbySeekersException : AppException()

    // Notifications
    object NotificationPermissionDeniedException : AppException()

    // Generic fallback
    data class UnknownException(val originalMessage: String?) : AppException()
}