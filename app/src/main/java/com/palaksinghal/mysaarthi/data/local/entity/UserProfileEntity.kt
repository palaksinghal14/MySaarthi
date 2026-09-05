package com.palaksinghal.mysaarthi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.palaksinghal.mysaarthi.domain.model.PracticeReminder
import com.palaksinghal.mysaarthi.domain.model.UserProfile
import kotlin.String

@Entity(tableName = "userProfile")
data class UserProfileEntity(
    @PrimaryKey
    val uid: String,
    val displayName: String,
    val practices: List<String>,
    val howLongOnPath: String ,
    val isOpenToSatsang: Boolean,
    val spiritualIntro: String,
    val onboardingCompleted: Boolean,
    val practiceReminders :List<PracticeReminder>
)

fun UserProfileEntity.toDomain() = UserProfile(
    uid =uid,
    displayName=displayName,
    practices=practices,
    howLongOnPath=howLongOnPath ,
    isOpenToSatsang=isOpenToSatsang,
    spiritualIntro=spiritualIntro,
    onboardingCompleted=onboardingCompleted,
    practiceReminders=practiceReminders.map { reminder ->
        mapOf(
            "practice" to reminder.practice,
            "hour" to reminder.hour,
            "minute" to reminder.minute,
            "amPm" to reminder.amPm,
            "isEnabled" to reminder.isEnabled
        )
    }
)

fun UserProfile.toEntity() = UserProfileEntity(
    uid =uid,
    displayName=displayName,
    practices=practices,
    howLongOnPath=howLongOnPath ,
    isOpenToSatsang=isOpenToSatsang,
    spiritualIntro=spiritualIntro,
    onboardingCompleted=onboardingCompleted,
    practiceReminders=practiceReminders.map { map ->
        PracticeReminder(
            practice = map["practice"] as? String ?: "",
            hour = (map["hour"] as? Long)?.toInt() ?: 7,
            minute = (map["minute"] as? Long)?.toInt() ?: 0,
            amPm = map["amPm"] as? String ?: "AM",
            isEnabled = map["isEnabled"] as? Boolean ?: true
        )
    }
)