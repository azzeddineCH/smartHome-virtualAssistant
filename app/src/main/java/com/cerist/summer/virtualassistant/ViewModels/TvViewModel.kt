package com.cerist.summer.virtualassistant.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.BroadLinkProfile
import com.cerist.summer.virtualassistant.Repositories.TvRepository
import com.cerist.summer.virtualassistant.Utils.toLiveData
import com.polidea.rxandroidble2.RxBleConnection
import io.reactivex.disposables.CompositeDisposable

class TvViewModel(private val tvRepository:TvRepository):ViewModel(){

    companion object {
        val TAG = "TvViewModel"
    }

    private val mTvBleConnectionState :MutableLiveData<RxBleConnection.RxBleConnectionState> = MutableLiveData()
    private val mTvPowerState: MutableLiveData<BroadLinkProfile.TvProfile.State> = MutableLiveData()
    private val mTvVolumeLevel: MutableLiveData<Int> = MutableLiveData()
    private val compositeDisposable = CompositeDisposable()


   init {
       compositeDisposable.add(tvRepository.broadLinkConnectionState.subscribe(
               mTvBleConnectionState::postValue,{}
       ))

       compositeDisposable.add(tvRepository.tvPowerState.subscribe(
               mTvPowerState::postValue,{}
       ))

       compositeDisposable.add(tvRepository.tvVolumeLevel.subscribe(
               mTvVolumeLevel::postValue,{}
       ))

       compositeDisposable.add(tvRepository.broadLinkConnection.subscribe({},{
        Log.d(TAG,"connection error")
       }))
   }


    fun getTvConnectionState():LiveData<RxBleConnection.RxBleConnectionState> = mTvBleConnectionState
    fun getTvPowerState():LiveData<BroadLinkProfile.TvProfile.State> = mTvPowerState
    fun getTvVolumeLevel():LiveData<Int> = mTvVolumeLevel

    fun setTvPowerState(state:BroadLinkProfile.TvProfile.State) {
        compositeDisposable.add(tvRepository.setTvPowerState(state)
                .subscribe({

                },{

                }))
    }
    fun setTvVolumLevel(level:Int) {
        compositeDisposable.add(tvRepository.setTvVolumeLevel(level)
                .subscribe({

                },{

                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}