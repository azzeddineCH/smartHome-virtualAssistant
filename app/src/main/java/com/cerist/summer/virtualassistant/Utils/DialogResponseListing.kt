package com.cerist.summer.virtualassistant.Utils

import com.cerist.summer.virtualassistant.Entities.ChatBotProfile
import com.google.gson.JsonElement
import java.util.HashMap


data class ResponseParametersListing(
        val device:ChatBotProfile.Device,
        val powerState:ChatBotProfile.State? = null,
        val luminosity: ChatBotProfile.Luminosity? = null,
        val airMode: ChatBotProfile.AirMode? = null,
        val volume: Int? = null
)

data class ResponseIntentListing(
        val action : String,
        val parameters: HashMap<String, JsonElement>
)