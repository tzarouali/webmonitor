module Model exposing (..)

import Navigation
import Routes exposing (..)


type alias Model =
  { history : List (Maybe Route)
  , userDetails : UserDetails
  , error : Maybe ApplicationError
  }

type alias Email = String
type alias Password = String
type alias Token = String
type alias UserId = String

type alias UserDetails =
  { email : Maybe Email
  , password : Maybe Password
  , token : Maybe Token
  , userId : Maybe String
  }

type ApplicationError =
  LoginError LoginErrorType

type LoginErrorType =
    EmailOrPasswordEmpty
  | FailedLogin

type alias UserLoginData =
  { userId : UserId
  , token: Token
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

updateError : Maybe ApplicationError -> Model -> Model
updateError maybeError model =
  {model | error = maybeError}