package com.jhonkk.mylocations.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.jhonkk.mylocations.service.DefaultLocationClient
import com.jhonkk.mylocations.service.LocationClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideLocationClient(@ApplicationContext context: Context, client: FusedLocationProviderClient): LocationClient
        = DefaultLocationClient(context = context, client = client)

    @Provides
    @Singleton
    fun provideFusedLocation(@ApplicationContext context: Context): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

}