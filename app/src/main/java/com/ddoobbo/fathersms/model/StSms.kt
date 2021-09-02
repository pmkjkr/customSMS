package com.ddoobbo.fathersms.model

data class StSms(
    var stAddress: String,
    var latestTimestamp: Long,
    var smsList: ArrayList<SmsInfo>
    )