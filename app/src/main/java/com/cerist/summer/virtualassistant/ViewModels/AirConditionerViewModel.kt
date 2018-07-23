package com.cerist.summer.virtualassistant.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.BroadLinkProfile
import com.cerist.summer.virtualassistant.Repositories.AirConditionerRepository
import com.cerist.summer.virtualassistant.Utils.Data.Status
import com.polidea.rxandroidble2.RxBleConnection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class AirConditionerViewModel(private val airConditionerRepository: AirConditionerRepository):ViewModel(){

    companion object {
        val TAG = "AirConditionerViewModel"
    }
    private val mAirConditionerConnectionState:MutableLiveData<RxBleConnection.RxBleConnectionState> = MutableLiveData()
    private val mBluetoothErrorStatus:MutableLiveData<String> = MutableLiveData()

    private val mAirConditionerPowerState:MutableLiveData<BroadLinkProfile.AirConditionerProfile.State> = MutableLiveData()
    private val mAirConditionerMode:MutableLiveData<BroadLinkProfile.AirConditionerProfile.Mode> = MutableLiveData()
    private val mAirConditionerTemp:MutableLiveData<Int> = MutableLiveData()

    private var bleConnection:RxBleConnection ? = null
    private val compositeDisposable = CompositeDisposable()


    init {

        compositeDisposable.add(airConditionerRepository.broadLinkConnection.subscribe({
            Log.d(TAG,"subscribing to the RxBleConnectionState")
            bleConnection = it
        },{
            Log.d(TAG,"error while subscribing to the RxBleConnectionState${it.message}")
            mBluetoothErrorStatus.postValue(Status.BLUETOOTH_CONNECTION_LOST)
        }))


        compositeDisposable.add(airConditionerRepository.broadLinkConnectionState.subscribe(
                mAirConditionerConnectionState::postValue) {
                    Log.d(TAG,"error while subscribing to the RxBleConnectionState${it.message}")
                    mBluetoothErrorStatus.postValue(Status.BLUETOOTH_CONNECTION_LOST)
                })

    }


    fun getAirConditionerPowerState(){
        compositeDisposable.add(
                Observable.just(Unit)
                        .flatMap {
                            if(isDeviceConnected())
                                Observable.just(it)
                            else
                                Observable.error(Throwable("device not connected"))
                        }
                        .flatMap {
                            airConditionerRepository.getAirConditionerPowerState(bleConnection!!)}
                        .subscribe(mAirConditionerPowerState::postValue) {

                        })
    }
    fun getAirConditionerMode(){
        compositeDisposable.add(
                Observable.just(Unit)
                        .flatMap {
                            if(isDeviceConnected())
                                Observable.just(it)
                            else
                                Observable.error(Throwable("device not connected"))
                        }
                        .flatMap {
                            airConditionerRepository.getAirConditionerMode(bleConnection!!)}
                        .subscribe(mAirConditionerMode::postValue) {

                        })
    }
    fun getAirConditionerTemp(){
        compositeDisposable.add(
                Observable.just(Unit)
                        .flatMap {
                            if(isDeviceConnected())
                                Observable.just(it)
                            else
                                Observable.error(Throwable("device not connected"))
                        }
                        .flatMap {
                            airConditionerRepository.getAirConditionerTemp(bleConnection!!)}
                        .subscribe(mAirConditionerTemp::postValue) {
                            mBluetoothErrorStatus.postValue(Status.BLUETOOTH_CONNECTION_LOST)
                        })
    }

    fun setAirConditionerPowerState(state:BroadLinkProfile.AirConditionerProfile.State) {
        compositeDisposable.add(
                Observable.just(state)
                        .flatMap {
                            if(isDeviceConnected())
                                Observable.just(it)
                            else
                                Observable.error(Throwable("device not connected"))
                        }
                        .flatMap {
                            airConditionerRepository.setAirConditionerPowerState(bleConnection!!,state)}
                        .subscribe(mAirConditionerPowerState::postValue) {
                        })
    }

    fun setAirConditionerMode(mode: BroadLinkProfile.AirConditionerProfile.Mode){
        compositeDisposable.add(
                Observable.just(mode)
                        .flatMap {
                            if(isDeviceConnected())
                                Observable.just(it)
                            else
                                Observable.error(Throwable("device not connected"))
                        }
                        .flatMap {
                            airConditionerRepository.setAirConditionerMode(bleConnection!!,it)}
                        .subscribe(mAirConditionerMode::postValue) {
                        })
    }

    fun setAirConditionerTemp(temperature:Int) {
        compositeDisposable.add(
                Observable.just(temperature)
                        .flatMap {
                            if (isDeviceConnected())
                            Observable.just(it)
                            else
                            Observable.error(Throwable("device not connected"))
                        }
                        .flatMap {
                            airConditionerRepository.setAirConditionerTemp(bleConnection!!, it)
                        }
                        .subscribe(mAirConditionerTemp::postValue) {
                        })
    }


    fun getAirConditionerConnectionStateLiveData():LiveData<RxBleConnection.RxBleConnectionState> = mAirConditionerConnectionState
    fun getAirConditionerConnectionErrorLiveData():LiveData<String> = mBluetoothErrorStatus
    fun getAirConditionerPowerStateLiveData():LiveData<BroadLinkProfile.AirConditionerProfile.State> = mAirConditionerPowerState
    fun getAirConditionerModeLiveData():LiveData<BroadLinkProfile.AirConditionerProfile.Mode> = mAirConditionerMode
    fun getAirConditionerTempLiveData():LiveData<Int> = mAirConditionerTemp

    private fun isDeviceConnected() = mAirConditionerConnectionState.value == RxBleConnection.RxBleConnectionState.CONNECTED

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}