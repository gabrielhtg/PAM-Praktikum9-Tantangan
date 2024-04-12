package com.ifs21010.glostandfound.models

import com.google.gson.annotations.SerializedName

data class ListLostFounds(
    @SerializedName("lost_founds") val lostFounds: ArrayList<LostFound>
)