package com.cerist.summer.virtualassistant.ViewModels

import android.arch.lifecycle.ViewModel
import com.cerist.summer.virtualassistant.Entities.BroadLinkProfile
import com.cerist.summer.virtualassistant.Repositories.AirConditionarRepository
import com.cerist.summer.virtualassistant.Utils.toLiveData

class AirConditionerViewModel(private val airConditionerRepository: AirConditionarRepository):ViewModel(){

    val airConditionerBleConnectionState by lazy {
        airConditionerRepository.getAirConditionConnectionState().toLiveData()
    }

    fun getAirConditionerPowerState() = airConditionerRepository.getAirConditionerPowerState().toLiveData()
    fun getAirConditionerMode() = airConditionerRepository.getAirConditionerMode().toLiveData()
    fun getAirConditionerTemp() = airConditionerRepository.getAirConditionerTemp().toLiveData()


    fun setAirConditionerPowerState(state:BroadLinkProfile.AirConditionerProfile.State)
            = airConditionerRepository.setAirConditionerPowerState(state).toLiveData()

    fun setAirConditionerMode(mode: BroadLinkProfile.AirConditionerProfile.Mode)
            = airConditionerRepository.setAirConditionerMode(mode).toLiveData()

    fun setAirConditionerTemp(temperature:Int)
            = airConditionerRepository.setAirConditionerTemp(temperature).toLiveData()
}