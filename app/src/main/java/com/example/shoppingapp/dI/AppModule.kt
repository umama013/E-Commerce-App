package com.example.shoppingapp.dI

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.example.shoppingapp.firebase.FirebaseCommons
import com.example.shoppingapp.util.Collection.INTRODUCTION_SP
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule
 {
  @Provides
  @Singleton
  fun provideFirebaseAuth() = FirebaseAuth.getInstance()

  @Provides
  @Singleton
  fun providesFirestoreAuth() = Firebase.firestore

  @Provides
  @Singleton
  fun provideFirebaseStorage() = FirebaseStorage.getInstance()


  @Provides
  fun provideIntroductionSP(
   application: Application
  ) = application.getSharedPreferences(INTRODUCTION_SP,MODE_PRIVATE)

  @Provides
  @Singleton
  fun providesFirebaseCommons(
   firebaseAuth: FirebaseAuth,
   firestore: FirebaseFirestore
  ) = FirebaseCommons(firestore,firebaseAuth)
  @Provides
  @Singleton
  fun provideStorageReference(firebaseStorage: FirebaseStorage): StorageReference {
   return firebaseStorage.reference
  }
}