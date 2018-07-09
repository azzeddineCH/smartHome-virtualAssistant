package com.cerist.summer.virtualassistant.ViewModels

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.cerist.summer.virtualassistant.Utils.toLiveData
import com.cerist.summer.virtualassistant.Repositories.LampRepository
import com.cerist.summer.virtualassistant.Utils.Status
import io.reactivex.disposables.CompositeDisposable

class LampViewModel(private val lampRepository:LampRepository):ViewModel(){

    companion object {
        val TAG = "LampViewModel"
    }
    val lampBleConnectionState by lazy {
        lampRepository.lampConnectionState.toLiveData()
    }

    val dispsable = lampRepository.lampBleConnection.subscribe {
        Log.d(TAG,"Tv connection moves to ${it.readRssi()}")
    }

    fun getLampLuminosityLevel() = lampRepository.lampLuminosityLevel.toLiveData()
    fun getLampLightningState() =lampRepository.lampLightningState.toLiveData()


    fun setLampLightningState(state:LampProfile.LAMP_STATE) =  lampRepository.setLampLightningState(state).toLiveData()
    fun setLampLuminosityLevel(level: LampProfile.LAMP_LUMINOSITY) = lampRepository.setLampLuminosityLevel(level).toLiveData()


    override fun onCleared() {
        super.onCleared()
        dispsable.dispose()
    }





}