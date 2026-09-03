package com.palaksinghal.mysaarthi.data.local.datasources

import android.content.Context
import com.palaksinghal.mysaarthi.data.local.dto.SholkaDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitaLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
){

    fun readGitaJson() :String {
        val inputStream= context.assets.open("gita.json")

        val gitaJsonString =inputStream.bufferedReader().use{it.readText()}

        return gitaJsonString
    }

    fun toGitaDto( gitaString :String ) :List<SholkaDto>{

         val gitaDto = Json.decodeFromString<List<SholkaDto>>(gitaString)
         return gitaDto

     }


}