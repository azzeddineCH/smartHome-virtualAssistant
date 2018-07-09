package com.cerist.summer.virtualassistant.ViewModels

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.BroadLinkProfile
import com.cerist.summer.virtualassistant.Repositories.TvRepository
import com.cerist.summer.virtualassistant.Utils.toLiveData

class TvViewModel(private val tvRepository:TvRepository):ViewModel(){

    companion object {
        val TAG = "TvViewModel"
    }

    val tvBleConnectionState by lazy {
        tvRepository.broadLinkConnectionState.toLiveData()
    }

    val dispsable = tvRepository.broadLinkConnection.subscribe {
            Log.d(TAG,"Tv connection moves to ${it.readRssi()}")
    }

    val  tvPowerState = tvRepository.tvPowerState.toLiveData()
    val  tvVolumeLevel  = tvRepository.tvVolumeLevel.toLiveData()

    fun setTvPowerState(state:BroadLinkProfile.TvProfile.State)
                     = tvRepository.setTvPowerState(state).toLiveData()

    fun setTvVolumLevel(level:Int)
                    = tvRepository.setTvVolumeLevel(level).toLiveData()

    override fun onCleared() {
        super.onCleared()
        dispsable.dispose()
    }
}