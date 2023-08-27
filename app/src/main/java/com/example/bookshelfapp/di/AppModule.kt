package com.example.bookshelfapp.di

import android.app.Application
import android.content.Context
import com.example.bookshelfapp.BookShelfApp
import com.example.bookshelfapp.data.repository.auth.AuthRepository
import com.example.bookshelfapp.data.repository.auth.AuthRepositoryImpl
import com.example.bookshelfapp.data.repository.bookshelf.BookShelfRepository
import com.example.bookshelfapp.data.repository.bookshelf.BookShelfRepositoryImpl
import com.example.bookshelfapp.data.services.BookServiceApi
import com.example.bookshelfapp.data.usecase.GetBooksUseCase
import com.example.bookshelfapp.data.utils.Constants.BASE_URL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun providesFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    fun providesFireStore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    fun providesAuthRepository(impl: AuthRepositoryImpl): AuthRepository {
        return impl
    }

    @Provides
    fun providesMainApplicationInstance(@ApplicationContext context: Context): BookShelfApp {
        return context as BookShelfApp
    }

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit {
        return  Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    fun providesBookServiceApi(retrofit: Retrofit): BookServiceApi {
        return retrofit.create(BookServiceApi::class.java)
    }

    @Provides
    fun providesBookShelfRepository(impl: BookShelfRepositoryImpl): BookShelfRepository {
        return impl
    }

    @Provides
    fun providesGetBooksUseCase(repository: BookShelfRepository): GetBooksUseCase {
        return GetBooksUseCase(repository)
    }


}