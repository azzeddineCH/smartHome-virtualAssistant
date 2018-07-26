package com.cerist.summer.virtualassistant.Utils.Data

import ai.api.model.AIOutputContext
import com.cerist.summer.virtualassistant.Entities.ChatBotProfile
import com.google.gson.JsonElement
import java.util.HashMap


data class ResponseParametersListing(
        val device:ChatBotProfile.Device,
        val powerState:ChatBotProfile.State? = null,
        val luminosity: ChatBotProfile.Luminosity? = null,
        val airMode: ChatBotProfile.AirMode? = null,
        val volume: Int? = null,
        val timer: Int? = null
)

data class ResponseIntentListing(
        val action : String,
        val parameters: HashMap<String, JsonElement>,
        val outputContexts: Map<String,AIOutputContext>
)