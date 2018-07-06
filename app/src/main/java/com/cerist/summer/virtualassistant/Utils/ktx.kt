package com.cerist.summer.virtualassistant.Utils

import android.app.Activity
import android.arch.lifecycle.*
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.cerist.summer.virtualassistant.Repositories.LampRepository
import com.cerist.summer.virtualassistant.ViewModels.LampViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable

fun <T> Observable<T>.toLiveData(backPressureStrategy: BackpressureStrategy =
                                                    BackpressureStrategy.LATEST) :  LiveData<T> {
    Log.d("RxJava",this.toString())
    return LiveDataReactiveStreams.fromPublisher(this.toFlowable(backPressureStrategy))
}

fun Fragment.getViewModel(type:Repositories): ViewModel {

    return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repo = ServiceLocator.instance(activity!!)
                    .getRepository(type)
            @Suppress("UNCHECKED_CAST")
            return when(type){
                Repositories.LAMP_REPOSITORY -> LampViewModel(repo as LampRepository) as T
                Repositories.BROAD_LINK_REPOSITORY -> LampViewModel(repo as LampRepository) as T
            }

        }
    })[when(type){
        Repositories.LAMP_REPOSITORY ->LampViewModel::class.java
        Repositories.BROAD_LINK_REPOSITORY -> LampViewModel::class.java
    }]

}

fun AppCompatActivity.getViewModel(activity:FragmentActivity,type:Repositories): ViewModel {

    return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repo = ServiceLocator.instance(activity)
                    .getRepository(type)
            @Suppress("UNCHECKED_CAST")
            return when(type){
                Repositories.LAMP_REPOSITORY -> LampViewModel(repo as LampRepository) as T
                Repositories.BROAD_LINK_REPOSITORY -> LampViewModel(repo as LampRepository) as T
            }

        }
    })[when(type){
        Repositories.LAMP_REPOSITORY ->LampViewModel::class.java
        Repositories.BROAD_LINK_REPOSITORY -> LampViewModel::class.java
    }]

}