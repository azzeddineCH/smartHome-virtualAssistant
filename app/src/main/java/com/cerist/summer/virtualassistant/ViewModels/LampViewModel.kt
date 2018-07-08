package com.cerist.summer.virtualassistant.ViewModels

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.cerist.summer.virtualassistant.Utils.toLiveData
import com.cerist.summer.virtualassistant.Repositories.LampRepository
import com.cerist.summer.virtualassistant.Utils.Status
import io.reactivex.disposables.CompositeDisposable

class LampViewModel(private val lampRepository:LampRepository):ViewModel(){

    val lampBleConnectionState by lazy {
        lampRepository.getLampConnectionState().toLiveData()
    }
    private val compositeDisposable:CompositeDisposable= CompositeDisposable()

    fun getLampLumonisitiyLevelLiveData() = lampRepository.getLampLuminosityLevel().toLiveData()
    fun getLampLightningStateLiveData() =lampRepository.getLampLightningState().toLiveData()


    fun setLampLightningState(state:LampProfile.LAMP_STATE)
            =  lampRepository.setLampLightningState(state).toLiveData()



    fun setLampLumonisitiyLevel(level: LampProfile.LAMP_LUMINOSITY)
           = lampRepository.setLampLuminosityLevel(level).toLiveData()







}