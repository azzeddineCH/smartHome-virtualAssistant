package com.cerist.summer.virtualassistant.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.cerist.summer.virtualassistant.Repositories.LampRepository
import com.polidea.rxandroidble2.RxBleConnection
import io.reactivex.disposables.CompositeDisposable

class LampViewModel(private val lampRepository:LampRepository):ViewModel(){

    companion object {
        val TAG = "LampViewModel"
    }
    private val  mLampPowerState:MutableLiveData<LampProfile.State> = MutableLiveData()
    private val  mLampLuminosityLevel:MutableLiveData<LampProfile.Luminosity>  = MutableLiveData()
    private val  mLampBleConnectionState:MutableLiveData<RxBleConnection.RxBleConnectionState>  = MutableLiveData()
    private val  compositeDisposable = CompositeDisposable()

    init {

      compositeDisposable.add(lampRepository.lampConnectionState.subscribe(
              mLampBleConnectionState::postValue,{

      }))

      compositeDisposable.add(lampRepository.lampPowerState.subscribe(
              mLampPowerState::postValue,{
                Log.d(TAG,"error is ${it.message}")
              }))

        compositeDisposable.add(lampRepository.lampLuminosityLevel.subscribe(
                mLampLuminosityLevel::postValue,{

                }))

        compositeDisposable.add(lampRepository.lampBleConnection.subscribe({},{

        }))

    }


    fun getLampLuminosityLevel():LiveData<LampProfile.Luminosity> = mLampLuminosityLevel
    fun getLampPowerState():LiveData<LampProfile.State> = mLampPowerState
    fun getLampConnectionState():LiveData<RxBleConnection.RxBleConnectionState> = mLampBleConnectionState



    fun setLampPowerState(state: LampProfile.State) {
      compositeDisposable.add(lampRepository.setLampPowerState(state)
            .subscribe({
                Log.d(TAG,"writing with success $it")
            },{
                Log.d(TvViewModel.TAG,"writing error")
            }))
    }
    fun setLampLuminosityLevel(level: LampProfile.Luminosity) {
        compositeDisposable.add(lampRepository.setLampLuminosityLevel(level)
                .subscribe({

                },{
                    Log.d(TvViewModel.TAG,"writing error")
                }))

    }


    override fun onCleared() {
        super.onCleared()
       compositeDisposable.clear()
    }





}