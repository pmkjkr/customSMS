package com.ddoobbo.fathersms.model

data class SmsInfo(
    var timestamp: Long,
    var address: String,
    var text: String
)