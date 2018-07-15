package com.cerist.summer.virtualassistant.Views.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import com.cerist.summer.virtualassistant.R
import com.cerist.summer.virtualassistant.Utils.Repositories
import com.cerist.summer.virtualassistant.Utils.getViewModel
import com.cerist.summer.virtualassistant.ViewModels.AirConditionerViewModel
import com.cerist.summer.virtualassistant.ViewModels.LampViewModel
import com.cerist.summer.virtualassistant.ViewModels.TvViewModel
import kotlinx.android.synthetic.main.dialog_fragment.*

class DialogFragment:Fragment(){


    private lateinit var mLampViewModel:LampViewModel
    private lateinit var mTvViewModel:TvViewModel
    private lateinit var mAirConditionerViewModel:AirConditionerViewModel
    private lateinit var onAudioRecordingButtonClickListener: AudioRecordingButtonTouchListener



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.dialog_fragment,container,false)!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        onAudioRecordingButtonClickListener = context as AudioRecordingButtonTouchListener

        mLampViewModel  =  getViewModel(Repositories.LAMP_REPOSITORY) as LampViewModel
        mTvViewModel  =  getViewModel( Repositories.TV_REPOSITORY) as TvViewModel
        mAirConditionerViewModel =  getViewModel(Repositories.AIR_CONDITIONER_REPOSITORY) as AirConditionerViewModel

        button.setOnClickListener {
           onAudioRecordingButtonClickListener.onAudioRecordingButtonTouch(MotionEvent.ACTION_DOWN)
        }

        button2.setOnClickListener {
            onAudioRecordingButtonClickListener.onAudioRecordingButtonTouch(MotionEvent.ACTION_UP)
        }

    }


    interface AudioRecordingButtonTouchListener{
        fun onAudioRecordingButtonTouch(action:Int)
    }


}