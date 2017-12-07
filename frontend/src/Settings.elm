module Settings exposing(..)

tokenHeaderName = "X-TOKEN-HEADER"
userIdHeaderName = "X-USER-ID-HEADER"

baseApiUri = "http://localhost:9000/api"
baseSocketApiUri = "ws://localhost:9000/api"

loginUri = baseApiUri ++ "/login"
logoutUri = baseApiUri ++ "/logout"

subscriptionsUri = baseApiUri ++ "/subscriptions"

subscriptionSocketUri : String -> String -> String -> String
subscriptionSocketUri subId userId token =
  baseSocketApiUri ++ "/socket/subscriptionFeed/" ++ subId ++ "?u=" ++ userId ++ "&t=" ++ token