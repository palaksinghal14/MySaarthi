package com.palaksinghal.mysaarthi.core.utils

fun conversationParticipants(uidA: String, uidB: String): List<String> =
    listOf(uidA, uidB).sorted()

fun conversationId(uidA: String, uidB: String): String =
    conversationParticipants(uidA, uidB).joinToString("_")