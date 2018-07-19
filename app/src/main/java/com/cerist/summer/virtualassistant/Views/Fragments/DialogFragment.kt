package com.cerist.summer.virtualassistant.Views.Fragments

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.cerist.summer.virtualassistant.R
import com.cerist.summer.virtualassistant.Utils.Repositories
import com.cerist.summer.virtualassistant.Utils.getViewModel
import com.cerist.summer.virtualassistant.Utils.take
import com.cerist.summer.virtualassistant.ViewModels.AirConditionerViewModel
import com.cerist.summer.virtualassistant.ViewModels.LampViewModel
import com.cerist.summer.virtualassistant.ViewModels.TvViewModel
import kotlinx.android.synthetic.main.dialog_fragment.*
import java.util.logging.Level

class DialogFragment:Fragment(){

    companion object {
        val TAG = "DialogFragment"
    }


    private lateinit var mLampViewModel:LampViewModel
    private lateinit var onAudioRecordingButtonClickListener: AudioRecordingButtonTouchListener



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.dialog_fragment,container,false)!!



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onAudioRecordingButtonClickListener = context as AudioRecordingButtonTouchListener


        mLampViewModel  =  getViewModel(Repositories.LAMP_REPOSITORY) as LampViewModel



        button.setOnClickListener {
            onAudioRecordingButtonClickListener.onAudioRecordingButtonTouch(MotionEvent.ACTION_DOWN)
        }
        button2.setOnClickListener {
            onAudioRecordingButtonClickListener.onAudioRecordingButtonTouch(MotionEvent.ACTION_UP)
        }


        button3.setOnClickListener {
            mLampViewModel.setLampPowerState(LampProfile.State.OFF)
        }

        button4.setOnClickListener {
            mLampViewModel.setLampPowerState(LampProfile.State.ON)

        }

        button5.setOnClickListener {
            mLampViewModel.getLampPowerState()

        }


        button6.setOnClickListener {
            mLampViewModel.getLampLuminosityLevel()

        }


        button7.setOnClickListener {
                mLampViewModel.setLampLuminosityLevel(LampProfile.Luminosity.LOW)

        }




    }


    interface AudioRecordingButtonTouchListener{
        fun onAudioRecordingButtonTouch(action:Int)
    }


}