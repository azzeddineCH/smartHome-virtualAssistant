package com.cerist.summer.virtualassistant.ViewModels

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.cerist.summer.virtualassistant.Extentions.toLiveData
import com.cerist.summer.virtualassistant.Repositories.LampRepository
import io.reactivex.disposables.CompositeDisposable

class LampViewModel(val lampRepository:LampRepository):ViewModel(){

    val lampBleConnectionState = lampRepository.getLampConnectionState()?.toLiveData()
    val lampBleLightningState = lampRepository.getLampState()?.toLiveData()
    val lampBleLuminosityLevel = lampRepository.getLampLuminosity()?.toLiveData()


    private val disposables = CompositeDisposable()

    fun setLampState(state:LampProfile.LAMP_STATE){

       val disposable = lampRepository.setLampState(state)
                .subscribe(
                        { Log.d("LampViewModel","state: ${it.name}")},
                        { Log.e("LampViewModel","error: ${it.message}")
                })
        disposables.add(disposable)
    }


    fun setLampLumonisitiy(level: LampProfile.LAMP_LUMINOSITY){

        val disposable = lampRepository.setLampLumonisitiy(level)
                .subscribe(
                        { Log.d("LampViewModel","state: ${it.name}")},
                        { Log.e("LampViewModel","error: ${it.message}")
                        })
        disposables.add(disposable)
    }

    override fun onCleared() {
        disposables.clear()
        lampRepository.clearDisposables()
    }



}