package com.cerist.summer.virtualassistant.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.BroadLinkProfile
import com.cerist.summer.virtualassistant.Repositories.TvRepository
import com.polidea.rxandroidble2.RxBleConnection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class TvViewModel(private val tvRepository:TvRepository):ViewModel(){

    companion object {
        val TAG = "TvViewModel"
    }

    private val  mTvBleConnectionState :MutableLiveData<RxBleConnection.RxBleConnectionState> = MutableLiveData()
    private val  mBluetoothErrorStatus:MutableLiveData<Int> = MutableLiveData()

    private val mTvPowerState: MutableLiveData<BroadLinkProfile.TvProfile.State> = MutableLiveData()
    private val mTvVolumeLevel: MutableLiveData<Int> = MutableLiveData()

    private var bleConnection:RxBleConnection ? = null
    private val compositeDisposable = CompositeDisposable()


   init {

       compositeDisposable.add(tvRepository.broadLinkConnection.subscribe({
           Log.d(TAG,"subscribing to the RxBleConnectionState")
           bleConnection = it
       },{
           Log.d(TAG,"error while subscribing to the RxBleConnectionState${it.message}")
       }))


       compositeDisposable.add(tvRepository.broadLinkConnectionState.subscribe(
               mTvBleConnectionState::postValue,{
           Log.d(TAG,"error while subscribing to the RxBleConnectionState${it.message}")
       }))



   }



    fun getTvPowerState(){
        compositeDisposable.add(
                Observable.just(Unit)
                        .flatMap {
                            if(isDeviceConnected())
                                Observable.just(it)
                            else
                                Observable.error(Throwable("device not connected"))
                        }
                        .flatMap {
                            tvRepository.getTvPowerState(bleConnection!!)}
                        .subscribe(mTvPowerState::postValue,{

                        }))
    }
    fun getTvVolumeLevel(){
        compositeDisposable.add(
                Observable.just(Unit)
                        .flatMap {
                            if(isDeviceConnected())
                                Observable.just(it)
                            else
                                Observable.error(Throwable("device not connected"))
                        }
                        .flatMap {
                            tvRepository.getTvVolume(bleConnection!!)}
                        .subscribe(mTvVolumeLevel::postValue,{
                        })

        )
    }

    fun setTvPowerState(state:BroadLinkProfile.TvProfile.State) {
        compositeDisposable.add(
                Observable.just(state)
                        .flatMap {
                            if(isDeviceConnected())
                                Observable.just(it)
                            else
                                Observable.error(Throwable("device not connected"))
                        }
                        .flatMap {
                            tvRepository.setTvPowerState(bleConnection!!,it)}
                        .subscribe(mTvPowerState::postValue,{
                        }))
    }
    fun setTvVolumLevel(level:Int) {
        compositeDisposable.add(
                Observable.just(level)
                        .flatMap {
                            if(isDeviceConnected())
                                Observable.just(it)
                            else
                                Observable.error(Throwable("device not connected"))}
                        .flatMap {
                            tvRepository.setTvVolumeLevel(bleConnection!!,it)
                        }
                        .subscribe(mTvVolumeLevel::postValue,{
                        }))
    }

    fun getTvConnectionStateLiveData():LiveData<RxBleConnection.RxBleConnectionState> = mTvBleConnectionState
    fun getTvPowerStateLiveDate():LiveData<BroadLinkProfile.TvProfile.State> = mTvPowerState
    fun getTvVolumeLevelLiveData():LiveData<Int> = mTvVolumeLevel
    fun getTvConnectionErrorLiveData():LiveData<Int> = mBluetoothErrorStatus

    private fun isDeviceConnected() = mTvBleConnectionState.value == RxBleConnection.RxBleConnectionState.CONNECTED

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}