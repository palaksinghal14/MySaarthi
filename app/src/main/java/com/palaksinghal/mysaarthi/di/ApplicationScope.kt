package com.palaksinghal.mysaarthi.di

import javax.inject.Qualifier

//Hilt needs it to tell “the app-wide CoroutineScope” apart from any other CoroutineScope you might inject later.
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope