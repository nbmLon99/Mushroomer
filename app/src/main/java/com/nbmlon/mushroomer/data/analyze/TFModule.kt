package com.nbmlon.mushroomer.data.analyze

import android.content.Context
import com.nbmlon.mushroomer.ml.Model

class TFModule {
    fun getModel(applicationContext: Context): Model {
        return Model.newInstance(applicationContext)
    }
}