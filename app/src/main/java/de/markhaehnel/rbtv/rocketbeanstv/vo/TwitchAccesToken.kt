package de.markhaehnel.rbtv.rocketbeanstv.vo

/*{
    "data": {
        "streamPlaybackAccessToken": {
            "value": "{\"adblock\":false,\"authorization\":{\"forbidden\":false,\"reason\":\"\"},\"blackout_enabled\":false,\"channel\":\"rocketbeanstv\",\"channel_id\":47627824,\"chansub\":{\"restricted_bitrates\":[],\"view_until\":1924905600},\"ci_gb\":false,\"geoblock_reason\":\"\",\"device_id\":null,\"expires\":1624101638,\"extended_history_allowed\":false,\"game\":\"\",\"hide_ads\":false,\"https_required\":true,\"mature\":false,\"partner\":false,\"platform\":\"web\",\"player_type\":\"embed\",\"private\":{\"allowed_to_view\":true},\"privileged\":false,\"role\":\"\",\"server_ads\":true,\"show_ads\":true,\"subscriber\":false,\"turbo\":false,\"user_id\":null,\"user_ip\":\"84.119.130.17\",\"version\":2}",
            "signature": "34fb8614e66d04a68dc716915740b0f2c3d676cc",
            "__typename": "PlaybackAccessToken"
        }
    },
    "extensions": {
        "durationMilliseconds": 55,
        "operationName": "PlaybackAccessToken",
        "requestID": "01F8HYW0EWR1PYQN3WVSAZWMJD"
    }
}*/


data class TwitchAccesToken(
    val data: TwitchAccesTokenData
)

data class TwitchAccesTokenData(
    val streamPlaybackAccessToken: TwitchStreamPlaybackAccessToken
)

data class TwitchStreamPlaybackAccessToken(
    val value: String,
    val signature: String
)