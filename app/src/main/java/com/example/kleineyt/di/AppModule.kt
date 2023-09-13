package com.example.kleineyt.di

import android.app.Application
import com.example.kleineyt.firebase.FirebaseCommon
import com.example.kleineyt.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestoreDatabase() = Firebase.firestore

    @Provides
    fun provideIntroductionSP(
        application: Application
    ) = application.getSharedPreferences(Constants.INTRODUCTION_SP, Application.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideFirebaseCommon(firebaseAuth: FirebaseAuth , firebaseFirestore: FirebaseFirestore)
    = FirebaseCommon(firebaseFirestore,firebaseAuth)

    @Provides
    @Singleton
    fun provideFirebaseStorage() = FirebaseStorage.getInstance().reference


}