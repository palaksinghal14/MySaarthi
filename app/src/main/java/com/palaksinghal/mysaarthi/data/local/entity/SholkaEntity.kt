package com.palaksinghal.mysaarthi.data.local.entity

import androidx.room.Entity
import com.palaksinghal.mysaarthi.domain.model.Shloka

@Entity(
    tableName = "shlokas" ,
    primaryKeys = ["chapter" , "verse"]
)
data class ShlokaEntity(
    val shlokaNumber:Int,
    val chapter: Int,
    val verse: Int,
    val sanskrit: String,
    val hindi: String,
    val english: String,
    val transliteration: String
)


fun ShlokaEntity.toShloka( ) = Shloka(
    shlokaNumber=shlokaNumber,
    chapter = chapter,
    verse = verse,
    shlokaSanskrit = sanskrit,
    hindiTranslation = hindi,
    englishTranslation = english,
)