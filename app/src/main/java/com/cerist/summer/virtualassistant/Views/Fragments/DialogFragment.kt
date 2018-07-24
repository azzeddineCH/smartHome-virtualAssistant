package com.cerist.summer.virtualassistant.Views.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.cerist.summer.virtualassistant.R
import com.cerist.summer.virtualassistant.Utils.Repositories
import com.cerist.summer.virtualassistant.Utils.Functions.getViewModel
import com.cerist.summer.virtualassistant.ViewModels.LampViewModel
import kotlinx.android.synthetic.main.dialog_fragment.*

class DialogFragment:Fragment(){

    companion object {
        val TAG = "DialogFragment"
    }


    private lateinit var onAudioRecordingButtonClickListener: AudioRecordingButtonTouchListener



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.dialog_fragment,container,false)!!



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onAudioRecordingButtonClickListener = context as AudioRecordingButtonTouchListener






        button.setOnClickListener {
            onAudioRecordingButtonClickListener.onAudioRecordingButtonTouch()
        }


    }


    interface AudioRecordingButtonTouchListener{
        fun onAudioRecordingButtonTouch()
    }


}