package com.cerist.summer.virtualassistant.Entities

import java.util.*

class LampProfile{

    companion object {
         const val DEVICE_MAC_ADDRESS:String = "E1:A8:5D:0E:86:10"


        const val LUMINOSITY_CHARACTERISTIC_UUID =  "0000beef-1212-EFDE-1523-785FEABCD123"
        const val STATE_CHARACTERISTIC_UUID =  "0000beef-1212-EFDE-1523-785FEABCD123"
    }

    enum class State(val value: Int) {
        ON(1),
        OFF(0)
    }

    enum class Luminosity(val value:Int) {
        NON(0),
        LOW(1),
        MEDIUM(2),
        HIGH(3),
        MAX(4)
    }




}