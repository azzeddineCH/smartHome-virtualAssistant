package com.cerist.summer.virtualassistant.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.cerist.summer.virtualassistant.Repositories.LampRepository
import com.cerist.summer.virtualassistant.Utils.Data.Status
import com.polidea.rxandroidble2.RxBleConnection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class LampViewModel(private val lampRepository:LampRepository):ViewModel(){

    companion object {
        val TAG = "LampViewModel"
    }


    private val  mLampBleConnectionState:MutableLiveData<RxBleConnection.RxBleConnectionState> = MutableLiveData()
    private val  mBluetoothErrorStatus:MutableLiveData<String> = MutableLiveData()

    private val  mLampPowerState:MutableLiveData<LampProfile.State> = MutableLiveData()
    private val  mLampLuminosityLevel:MutableLiveData<LampProfile.Luminosity> = MutableLiveData()

    private var  bleConnection:RxBleConnection ? = null
    private val  compositeDisposable = CompositeDisposable()


    init {

        compositeDisposable.add(lampRepository.lampBleConnection.subscribe({
            Log.d(TAG,"subscribing to the RxBleConnectionState")
                    bleConnection = it
        },{
            Log.d(TAG,"error while subscribing to the RxBleConnectionState ${it.message}")
            mBluetoothErrorStatus.postValue(Status.BLUETOOTH_CONNECTION_LOST)
        }))


        compositeDisposable.add(lampRepository.lampConnectionState.subscribe(
                mLampBleConnectionState::postValue) {
                   mBluetoothErrorStatus.postValue(Status.BLUETOOTH_CONNECTION_LOST)
                })


    }




    fun getLampLuminosityLevel(){
        compositeDisposable.add(
                Observable.just(Unit)
                        .flatMap {
                            if(isDeviceConnected())
                                Observable.just(it)
                            else
                                Observable.error(Throwable(Status.BLUETOOTH_CONNECTION_LOST))
                        }
                        .flatMap {
                                    lampRepository.getLampLuminosityLevel(bleConnection!!)}
                        .subscribe(mLampLuminosityLevel::postValue) {
                            mBluetoothErrorStatus.postValue(Status.OPERATION_ERROR)})
    }


    fun getLampPowerState(){
        compositeDisposable.add(
                Observable.just(Unit)
                        .flatMap {
                            if(isDeviceConnected())
                                Observable.just(it)
                            else
                                Observable.error(Throwable(Status.BLUETOOTH_CONNECTION_LOST))
                        }
                        .flatMap {
                          lampRepository.getLampPowerState(bleConnection!!)}
                        .subscribe(mLampPowerState::postValue){
                            mBluetoothErrorStatus.postValue(Status.OPERATION_ERROR)})
    }



    fun setLampPowerState(state: LampProfile.State){
        compositeDisposable.add(
                Observable.just(state)
                          .flatMap {
                            if(isDeviceConnected())
                                Observable.just(it)
                            else
                                Observable.error(Throwable(Status.BLUETOOTH_CONNECTION_LOST))
                        }
                          .flatMap {
                              lampRepository.setLampPowerState(bleConnection!!,it) }
                          .subscribe(mLampPowerState::postValue) {
                              mBluetoothErrorStatus.postValue(Status.OPERATION_ERROR)})
    }

    fun setLampLuminosityLevel(level: LampProfile.Luminosity){
        compositeDisposable.add(
                Observable.just(level)
                         .flatMap {
                           if(isDeviceConnected())
                                Observable.just(it)
                            else
                               Observable.error(Throwable(Status.BLUETOOTH_CONNECTION_LOST))
                        }
                         .flatMap {
                            lampRepository.setLampLuminosityLevel(bleConnection!!,it)
                        }
                         .subscribe(mLampLuminosityLevel::postValue) {
                             mBluetoothErrorStatus.postValue(Status.OPERATION_ERROR)})
    }


    fun getLampPowerStateLiveData(): LiveData<LampProfile.State> = mLampPowerState
    fun getLampLuminosityLevelLiveData(): LiveData<LampProfile.Luminosity> = mLampLuminosityLevel
    fun getLampConnectionStateLiveData():LiveData<RxBleConnection.RxBleConnectionState> = mLampBleConnectionState
    fun getLampConnectionErrorLiveData():LiveData<String> = mBluetoothErrorStatus




    private fun isDeviceConnected() = mLampBleConnectionState.value == RxBleConnection.RxBleConnectionState.CONNECTED



    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }





}