package com.cerist.summer.virtualassistant.ViewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.cerist.summer.virtualassistant.Entities.BroadLinkProfile
import com.cerist.summer.virtualassistant.Repositories.AirConditionerRepository
import com.cerist.summer.virtualassistant.Utils.toLiveData
import com.polidea.rxandroidble2.RxBleConnection
import io.reactivex.disposables.CompositeDisposable

class AirConditionerViewModel(private val airConditionerRepository: AirConditionerRepository):ViewModel(){

    private val mAirConditionerConnectionState:MutableLiveData<RxBleConnection.RxBleConnectionState> = MutableLiveData()
    private val mAirConditionerPowerState:MutableLiveData<BroadLinkProfile.AirConditionerProfile.State> = MutableLiveData()
    private val mAirConditionerMode:MutableLiveData<BroadLinkProfile.AirConditionerProfile.Mode> = MutableLiveData()
    private val mAirConditionerTemp:MutableLiveData<Int> = MutableLiveData()
    private val compositeDisposable = CompositeDisposable()


    init {

        compositeDisposable.add(airConditionerRepository.broadLinkConnectionState.subscribe(
                mAirConditionerConnectionState::postValue,{

        }))

        compositeDisposable.add(airConditionerRepository.airConditionerPowerState.subscribe(
                mAirConditionerPowerState::postValue,{

        }))

        compositeDisposable.add(airConditionerRepository.airConditionerMode.subscribe(
                mAirConditionerMode::postValue,{

        }))

        compositeDisposable.add(airConditionerRepository.airConditionerTemp.subscribe(
                mAirConditionerTemp::postValue,{

        }))

        compositeDisposable.add(airConditionerRepository.broadLinkConnection.subscribe(
                {},{

        }))

    }

    fun setAirConditionerPowerState(state:BroadLinkProfile.AirConditionerProfile.State) {
        compositeDisposable.add(airConditionerRepository.setAirConditionerPowerState(state)
                .subscribe({

                },{

                }))
    }

    fun setAirConditionerMode(mode: BroadLinkProfile.AirConditionerProfile.Mode){
        compositeDisposable.add(airConditionerRepository.setAirConditionerMode(mode)
                .subscribe({

                },{

                }))
    }

    fun setAirConditionerTemp(temperature:Int){
        compositeDisposable.add(airConditionerRepository.setAirConditionerTemp(temperature)
                .subscribe({

                },{

                }))}

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}