package com.palaksinghal.mysaarthi.core.utils
import android.content.Context
import android.database.sqlite.SQLiteException
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.gson.JsonSyntaxException
import com.palaksinghal.mysaarthi.domain.model.AppException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
fun Throwable.toAppException(context: Context? = null) : AppException{
    // Check actual device connectivity first — this catches every network
    // failure regardless of how the specific SDK happened to wrap it
    if (context != null && !isNetworkAvailable(context)) {
        return AppException.NoInternetException
    }

    return when(this){
        // Network
        is UnknownHostException -> AppException.NoInternetException
        is SocketTimeoutException -> AppException.TimeoutException
        is HttpException -> AppException.ServerException(this.code())

        // Firebase Auth
        is FirebaseAuthInvalidUserException -> AppException.UserNotFoundException
        is FirebaseAuthInvalidCredentialsException -> AppException.WrongPasswordException
        is FirebaseAuthUserCollisionException -> AppException.EmailAlreadyExistsException
        is FirebaseNetworkException -> AppException.FirebaseUnavailableException
        is ApiException -> AppException.GoogleSignInException

        // Firestore
        is FirebaseFirestoreException -> when (this.code) {
            FirebaseFirestoreException.Code.UNAVAILABLE -> AppException.FirebaseUnavailableException
            FirebaseFirestoreException.Code.NOT_FOUND -> AppException.FirestoreNotFoundException
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> AppException.FirestorePermissionDeniedException
            else -> AppException.UnknownException(this.message)
        }

        // Room
        is SQLiteException -> AppException.DatabaseException

        is JsonSyntaxException -> AppException.GitaDataParseException

        else -> AppException.UnknownException(this.message)
    }
}