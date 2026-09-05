package com.palaksinghal.mysaarthi.data.local.entity

import androidx.room.Entity
import com.palaksinghal.mysaarthi.domain.model.SadhanaEntry

@Entity(
    tableName = "sadhana_entries",
    primaryKeys = ["date", "practice"]
)
data class SadhanaEntryEntity(
    val date: String,
    val practice: String,
    val isCompleted: Boolean
)

fun SadhanaEntryEntity.toDomain() = SadhanaEntry(
    date = date,
    practice = practice,
    isCompleted = isCompleted
)