package com.cerist.summer.virtualassistant.ViewModels

import android.arch.lifecycle.ViewModel
import com.cerist.summer.virtualassistant.Entities.BroadLinkProfile
import com.cerist.summer.virtualassistant.Repositories.TvRepository
import com.cerist.summer.virtualassistant.Utils.toLiveData

class TvViewModel(private val tvRepository:TvRepository):ViewModel(){

    val tvBleConnectionState by lazy {
        tvRepository.getTvConnectionState().toLiveData()
    }

    fun getTvPowerState() = tvRepository.getTvPowerState().toLiveData()
    fun getTvVolumLevel()= tvRepository.getTvVolumeLevel().toLiveData()

    fun setTvPowerState(state:BroadLinkProfile.TvProfile.State)
            = tvRepository.setTvPowerState(state).toLiveData()

    fun setTvVolumLevel(level:Int)
            = tvRepository.setTvVolumeLevel(level).toLiveData()
}