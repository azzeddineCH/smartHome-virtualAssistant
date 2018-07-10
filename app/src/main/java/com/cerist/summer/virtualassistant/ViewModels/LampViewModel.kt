package com.cerist.summer.virtualassistant.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
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

      compositeDisposable.add(lampRepository.lampLightningState.subscribe(
              mLampPowerState::postValue,{

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



    fun setLampLightningState(state: LampProfile.State) {
        compositeDisposable.add(lampRepository.setLampPowerState(state)
            .subscribe({

            },{

            }))

    }
    fun setLampLuminosityLevel(level: LampProfile.Luminosity) {
        compositeDisposable.add(lampRepository.setLampLuminosityLevel(level)
                .subscribe({

                },{

                }))

    }


    override fun onCleared() {
        super.onCleared()
       compositeDisposable.clear()
    }





}