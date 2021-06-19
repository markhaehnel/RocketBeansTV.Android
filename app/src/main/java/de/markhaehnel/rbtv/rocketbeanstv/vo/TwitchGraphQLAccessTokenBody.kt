package de.markhaehnel.rbtv.rocketbeanstv.vo

/*{
  "operationName": "PlaybackAccessToken",
  "extensions": {
    "persistedQuery": {
      "version": 1,
      "sha256Hash": "0828119ded1c13477966434e15800ff57ddacf13ba1911c129dc2200705b0712"
    }
  },
  "variables": {
    "isLive": true,
    "login": "rocketbeanstv",
    "isVod": false,
    "vodID": "",
    "playerType": "embed"
  }
}
*/


data class TwitchGraphQLAccessTokenBody(
    val operationName: String = "PlaybackAccessToken",
    val extensions: TwitchGraphQLAccessTokenBodyExtensions = TwitchGraphQLAccessTokenBodyExtensions(TwitchGraphQLAccessTokenBodyExtensionsPersistedQuery()),
    val variables: TwitchGraphQLAccessTokenBodyVariables = TwitchGraphQLAccessTokenBodyVariables()
)

data class TwitchGraphQLAccessTokenBodyExtensions(
    val persistedQuery: TwitchGraphQLAccessTokenBodyExtensionsPersistedQuery
)

data class TwitchGraphQLAccessTokenBodyExtensionsPersistedQuery(
    val version: Int = 1,
    val sha256Hash: String = "0828119ded1c13477966434e15800ff57ddacf13ba1911c129dc2200705b0712"
)

data class TwitchGraphQLAccessTokenBodyVariables(
    val isLive: Boolean = true,
    val login: String = "rocketbeanstv",
    val isVod: Boolean = false,
    val vodID: String = "",
    val playerType: String = "embed"
)