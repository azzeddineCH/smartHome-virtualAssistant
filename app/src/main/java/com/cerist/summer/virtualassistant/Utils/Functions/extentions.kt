package com.cerist.summer.virtualassistant.Utils.Functions

import android.arch.lifecycle.*
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.cerist.summer.virtualassistant.Repositories.AirConditionerRepository
import com.cerist.summer.virtualassistant.Repositories.DialogRepository
import com.cerist.summer.virtualassistant.Repositories.LampRepository
import com.cerist.summer.virtualassistant.Repositories.TvRepository
import com.cerist.summer.virtualassistant.Utils.Repositories
import com.cerist.summer.virtualassistant.Utils.ServiceLocator
import com.cerist.summer.virtualassistant.ViewModels.AirConditionerViewModel
import com.cerist.summer.virtualassistant.ViewModels.DialogViewModel
import com.cerist.summer.virtualassistant.ViewModels.LampViewModel
import com.cerist.summer.virtualassistant.ViewModels.TvViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable

fun <T> Observable<T>.toLiveData(backPressureStrategy: BackpressureStrategy =
                                                    BackpressureStrategy.LATEST) :  LiveData<T> {

    return LiveDataReactiveStreams.fromPublisher(this.toFlowable(backPressureStrategy))
}

fun <T> LiveData<T>.take(count:Int): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    var takenCount = 0
    mutableLiveData.addSource(this, {
        if(takenCount<count) {
            mutableLiveData.value = it
            takenCount++
        }
    })
    return mutableLiveData
}


fun Fragment.getViewModel(type: Repositories): ViewModel {

    return ViewModelProviders.of(activity!!, object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repo = ServiceLocator.instance(activity!!)
                    .getRepository(type)
            @Suppress("UNCHECKED_CAST")
            return when(type){
                Repositories.LAMP_REPOSITORY -> LampViewModel(repo as LampRepository) as T
                Repositories.TV_REPOSITORY -> TvViewModel(repo as TvRepository) as T
                Repositories.AIR_CONDITIONER_REPOSITORY -> AirConditionerViewModel(repo as AirConditionerRepository) as T
                Repositories.DIALOG_REPOSITORY -> DialogViewModel(repo as DialogRepository) as T
            }
        }
    })[when(type){
        Repositories.LAMP_REPOSITORY ->LampViewModel::class.java
        Repositories.TV_REPOSITORY -> TvViewModel::class.java
        Repositories.AIR_CONDITIONER_REPOSITORY -> AirConditionerViewModel::class.java
        Repositories.DIALOG_REPOSITORY -> DialogViewModel::class.java
    }]

}

fun AppCompatActivity.getViewModel(activity:FragmentActivity,type: Repositories): ViewModel {

    return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repo = ServiceLocator.instance(activity)
                    .getRepository(type)
            @Suppress("UNCHECKED_CAST")
            return when(type){
                Repositories.LAMP_REPOSITORY -> LampViewModel(repo as LampRepository) as T
                Repositories.TV_REPOSITORY -> TvViewModel(repo as TvRepository) as T
                Repositories.AIR_CONDITIONER_REPOSITORY -> AirConditionerViewModel(repo as AirConditionerRepository) as T
                Repositories.DIALOG_REPOSITORY -> DialogViewModel(repo as DialogRepository) as T
            }
            }
    })[when(type){
        Repositories.LAMP_REPOSITORY ->LampViewModel::class.java
        Repositories.TV_REPOSITORY -> TvViewModel::class.java
        Repositories.AIR_CONDITIONER_REPOSITORY -> AirConditionerViewModel::class.java
        Repositories.DIALOG_REPOSITORY -> DialogViewModel::class.java
    }]

}

fun <A, B> LiveData<A>.switchMap(function: (A) -> LiveData<B>): LiveData<B> = Transformations.switchMap(this, function)