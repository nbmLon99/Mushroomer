package com.nbmlon.mushroomer.data.analyze

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.Resources
import com.nbmlon.mushroomer.ml.Model
import java.io.FileInputStream
import java.io.IOException

class TFModule {
    fun getModel(applicationContext: Context): Model {
        return Model.newInstance(applicationContext)
    }
}