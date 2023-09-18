package com.nbmlon.mushroomer.data.analyze

import android.content.Context
import androidx.activity.ComponentActivity
import com.nbmlon.mushroomer.ml.Model
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
class TFModule {
    @Provides
    fun provideModel(applicationContext: Context): Model {
        return Model.newInstance(applicationContext)
    }
}