package com.cerist.summer.virtualassistant.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.BroadLinkProfile
import com.cerist.summer.virtualassistant.Repositories.AirConditionerRepository
import com.cerist.summer.virtualassistant.Utils.toLiveData
import com.polidea.rxandroidble2.RxBleConnection
import io.reactivex.disposables.CompositeDisposable

class AirConditionerViewModel(private val airConditionerRepository: AirConditionerRepository):ViewModel(){

    companion object {
        val TAG = "AirConditionerViewModel"
    }
    private val mAirConditionerConnectionState:MutableLiveData<RxBleConnection.RxBleConnectionState> = MutableLiveData()
    private val mAirConditionerPowerState:MutableLiveData<BroadLinkProfile.AirConditionerProfile.State> = MutableLiveData()
    private val mAirConditionerMode:MutableLiveData<BroadLinkProfile.AirConditionerProfile.Mode> = MutableLiveData()
    private val mAirConditionerTemp:MutableLiveData<Int> = MutableLiveData()
    private val compositeDisposable = CompositeDisposable()


    init {

        compositeDisposable.add(airConditionerRepository.broadLinkConnectionState.subscribe(
                mAirConditionerConnectionState::postValue,{
            Log.d(TAG,"error is ${it.message}")

        }))

        compositeDisposable.add(airConditionerRepository.airConditionerPowerState.subscribe(
                mAirConditionerPowerState::postValue,{
            Log.d(TAG,"error is ${it.message}")
        }))

        compositeDisposable.add(airConditionerRepository.airConditionerMode.subscribe(
                mAirConditionerMode::postValue,{
            Log.d(TAG,"error is ${it.message}")
        }))

        compositeDisposable.add(airConditionerRepository.airConditionerTemp.subscribe(
                mAirConditionerTemp::postValue,{
            Log.d(TAG,"error is ${it.message}")
        }))

        compositeDisposable.add(airConditionerRepository.broadLinkConnection.subscribe(
                {},{
            Log.d(TAG,"error is ${it.message}")
        }))

    }

    fun getAirConditionerConnectionState():LiveData<RxBleConnection.RxBleConnectionState> = mAirConditionerConnectionState
    fun getAirConditionerPowerState():LiveData<BroadLinkProfile.AirConditionerProfile.State> = mAirConditionerPowerState
    fun getAirConditionerMode():LiveData<BroadLinkProfile.AirConditionerProfile.Mode> = mAirConditionerMode
    fun getAirConditionerTemp():LiveData<Int>  = mAirConditionerTemp

    fun setAirConditionerPowerState(state:BroadLinkProfile.AirConditionerProfile.State) {
        compositeDisposable.add(airConditionerRepository.setAirConditionerPowerState(state)
                .subscribe({
                    Log.d(TAG,"writing with success $it")
                },{
                    Log.d(TAG,"writing error ${it.message}")
                }))
    }

    fun setAirConditionerMode(mode: BroadLinkProfile.AirConditionerProfile.Mode){
        compositeDisposable.add(airConditionerRepository.setAirConditionerMode(mode)
                .subscribe({
                    Log.d(TAG,"writing with success $it")
                },{
                    Log.d(TAG,"writing error ${it.message}")
                }))
    }

    fun setAirConditionerTemp(temperature:Int){
        compositeDisposable.add(airConditionerRepository.setAirConditionerTemp(temperature)
                .subscribe({
                    Log.d(TAG,"writing with success $it")
                },{
                    Log.d(TAG,"writing error ${it.message}")
                }))}

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}