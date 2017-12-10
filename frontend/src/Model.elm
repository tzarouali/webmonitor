module Model exposing (..)

import Navigation
import Routes exposing (..)
import Date exposing (..)
import Uuid exposing (..)


type alias Model =
  { history : List (Maybe Route)
  , userDetails : UserDetails
  , subscriptions : List UserSubscription
  , loginPageError : Maybe LoginPageError
  , homePageError : Maybe HomePageError
  }

type alias Email = String
type alias Password = String
type alias Token = String
type alias UserId = Uuid
type alias SubscriptionId = Uuid

type alias UserDetails =
  { email : Maybe Email
  , password : Maybe Password
  , token : Maybe Token
  , userId : Maybe UserId
  }

type LoginPageError =
    LoginPageError LoginPageErrorType

type LoginPageErrorType =
    EmailOrPasswordEmpty
  | FailedLogin

type HomePageError =
  HomePageError HomePageErrorType

type HomePageErrorType =
  LoadingSubscriptionsError

type alias UserLoginData =
  { userId : UserId
  , token: Token
  }

type alias UserSubscription =
  { id : SubscriptionId
  , url : String
  , jqueryExtractor : String
  , userId : UserId
  , name : String
  , data : Maybe SubscriptionValue
  }

type alias SubscriptionValue =
  { subId : SubscriptionId
  , value : String
  , lastUpdated : Date
  }

updateUserEmail : Email -> Model -> Model
updateUserEmail email model =
  let
    userDetails = model.userDetails
    detailsWithEmail = {userDetails | email = Just email}
  in
    {model | userDetails = detailsWithEmail}

updateUserPassword : Password -> Model -> Model
updateUserPassword pass model =
  let
    userDetails = model.userDetails
    detailsWithPassword = {userDetails | password = Just pass}
  in
    {model | userDetails = detailsWithPassword}

updateUserToken : Token -> Model -> Model
updateUserToken token model =
  let
    userDetails = model.userDetails
    detailsWithToken = {userDetails | token = Just token}
  in
    {model | userDetails = detailsWithToken}

updateUserId : UserId -> Model -> Model
updateUserId userId model =
  let
    userDetails = model.userDetails
    detailsWithUserId = {userDetails | userId = Just userId}
  in
    {model | userDetails = detailsWithUserId}

updateLoginPageError : Maybe LoginPageError -> Model -> Model
updateLoginPageError maybeError model =
  {model | loginPageError = maybeError}

updateHomePageError : Maybe HomePageError -> Model -> Model
updateHomePageError maybeError model =
  {model | homePageError = maybeError}
