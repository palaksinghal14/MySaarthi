package com.palaksinghal.mysaarthi.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.palaksinghal.mysaarthi.R
import com.palaksinghal.mysaarthi.domain.model.AppException

@Composable
fun AppException.toUserMessage():String = when(this){
    is AppException.NoInternetException -> stringResource(R.string.error_no_internet)
    is AppException.TimeoutException -> stringResource(R.string.error_timeout)
    is AppException.ServerException -> stringResource(R.string.error_server, this.code)
    is AppException.UserNotFoundException-> stringResource(R.string.error_user_not_found)
    is AppException.EmailAlreadyExistsException->stringResource(R.string.error_email_already_exists_exception)
    is AppException.WrongPasswordException->stringResource(R.string.error_wrong_password_exception)
    is AppException.GoogleSignInException -> stringResource(R.string.error_google_sign_in)
    is AppException.FirebaseUnavailableException -> stringResource(R.string.error_firebase_unavailable)
    is AppException.FirestoreNotFoundException -> stringResource(R.string.error_firestore_not_found)
    is AppException.FirestorePermissionDeniedException -> stringResource(R.string.error_firestore_permission_denied)
    is AppException.DatabaseException -> stringResource(R.string.error_database)
    is AppException.GitaDataParseException -> stringResource(R.string.error_gita_data_parse)
    is AppException.LocationPermissionDeniedException -> stringResource(R.string.error_location_permission_denied)
    is AppException.PlacesApiException -> stringResource(R.string.error_places_api)
    is AppException.NoNearbySeekersException -> stringResource(R.string.error_no_nearby_seekers)
    is AppException.NotificationPermissionDeniedException -> stringResource(R.string.error_notification_permission_denied)
    is AppException.UnknownException -> this.originalMessage ?: stringResource(R.string.error_unknown)
    is AppException.EmptyFieldsException -> stringResource(R.string.error_empty_fields)
    is AppException.InvalidEmailFormatException -> stringResource(R.string.error_invalid_email)
}