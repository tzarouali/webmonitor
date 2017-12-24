module Model exposing (..)

import Navigation
import Routes exposing (..)
import Date exposing (..)
import Uuid exposing (..)


type alias Model =
  { commonModel : CommonModel
  , loginPageModel : LoginPageModel
  , homePageModel : HomePageModel
  }

type alias CommonModel =
  { routeHistory : List (Maybe Route)
  , userSession : UserSession
  }

type alias LoginPageModel =
  { loginPageError : Maybe LoginPageError
  , email : Maybe Email
  , password : Maybe Password
  }

type alias HomePageModel =
  { homePageError : Maybe HomePageError
  , subscriptions : List UserSubscription
  }

type alias Email = String
type alias Password = String
type alias Token = String
type alias UserId = Uuid
type alias SubscriptionId = Uuid

type alias UserSession =
  { token : Maybe Token
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
  , cssSelector : String
  , userId : UserId
  , name : String
  , data : Maybe SubscriptionValue
  }

type alias SubscriptionValue =
  { subId : SubscriptionId
  , value : String
  , lastUpdated : Date
  , changed : Bool
  }

initModel : Model
initModel =
  { commonModel = initCommonModel
  , loginPageModel = initLoginPageModel
  , homePageModel = initHomePageModel
  }

initCommonModel : CommonModel
initCommonModel =
  { routeHistory = []
  , userSession = { token = Nothing
                  , userId = Nothing
                  }
  }

initLoginPageModel : LoginPageModel
initLoginPageModel =
  { loginPageError = Nothing
  , email = Nothing
  , password = Nothing
  }

initHomePageModel : HomePageModel
initHomePageModel =
  { homePageError = Nothing
  , subscriptions = []
  }

updateRouteHistory : List (Maybe Route) -> Model -> Model
updateRouteHistory routes model =
  let
    commonModel = model.commonModel
    updatedCommonModel = {commonModel | routeHistory = routes}
  in
    {model | commonModel = updatedCommonModel}

updateUserEmail : Email -> Model -> Model
updateUserEmail email model =
  let
    loginPageModel = model.loginPageModel
    updatedLoginPageModel = {loginPageModel | email = Just email}
  in
    {model | loginPageModel = updatedLoginPageModel}

updateUserPassword : Password -> Model -> Model
updateUserPassword pass model =
  let
    loginPageModel = model.loginPageModel
    updatedLoginPageModel = {loginPageModel | password = Just pass}
  in
    {model | loginPageModel = updatedLoginPageModel}

updateUserToken : Token -> Model -> Model
updateUserToken token model =
  let
    commonModel = model.commonModel
    userSession = commonModel.userSession
    sessionWithToken = {userSession | token = Just token}
    updatedCommonModel = {commonModel | userSession = sessionWithToken}
  in
    {model | commonModel = updatedCommonModel}

updateUserId : UserId -> Model -> Model
updateUserId userId model =
  let
    commonModel = model.commonModel
    userSession = commonModel.userSession
    sessionWithUserId = {userSession | userId = Just userId}
    updatedCommonModel = {commonModel | userSession = sessionWithUserId}
  in
    {model | commonModel = updatedCommonModel}

updateLoginPageError : Maybe LoginPageError -> Model -> Model
updateLoginPageError maybeError model =
  let
    loginPageModel = model.loginPageModel
    updatedLoginPageModel = {loginPageModel | loginPageError = maybeError}
  in
    {model | loginPageModel = updatedLoginPageModel}

updateHomePageError : Maybe HomePageError -> Model -> Model
updateHomePageError maybeError model =
  let
    homePageModel = model.homePageModel
    updatedHomePageModel = {homePageModel | homePageError = maybeError}
  in
    {model | homePageModel = updatedHomePageModel}

updateUserSubscriptions : List UserSubscription -> Model -> Model
updateUserSubscriptions ss model =
  let
    homePageModel = model.homePageModel
    updatedHomePageModel = {homePageModel | subscriptions = ss}
  in
    {model | homePageModel = updatedHomePageModel}
