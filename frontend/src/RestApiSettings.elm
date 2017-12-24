module RestApiSettings exposing(..)

import Model exposing (..)
import Uuid as U exposing (..)


tokenHeaderName = "X-TOKEN-HEADER"
userIdHeaderName = "X-USER-ID-HEADER"

baseApiUri = "http://localhost:9000/api"
baseSocketApiUri = "ws://localhost:9000/api"

loginUri = baseApiUri ++ "/login"
logoutUri = baseApiUri ++ "/logout"

subscriptionsUri = baseApiUri ++ "/subscriptions"

subscriptionSocketUri : SubscriptionId -> UserId -> Token -> String
subscriptionSocketUri subId userId token =
  baseSocketApiUri
    ++ "/socket/subscriptionFeed/"
    ++ (U.toString subId)
    ++ "?u="
    ++ (U.toString userId)
    ++ "&t="
    ++ token
