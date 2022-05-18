package co.touchlab.kampkit

const val FIREBASE_PROJECT_NAME = "multimenu"
const val FIREBASE_PATH_WORKERS = "https://firestore.googleapis.com/v1/projects/$FIREBASE_PROJECT_NAME/databases/(default)/documents/workers"
const val FIREBASE_PATH_PLAYERS = "https://firestore.googleapis.com/v1/projects/$FIREBASE_PROJECT_NAME/databases/(default)/documents/players"
const val FIREBASE_PATH_TEAMS = "https://firestore.googleapis.com/v1/projects/$FIREBASE_PROJECT_NAME/databases/(default)/documents/teams"
const val FIREBASE_APIKEY = "AIzaSyB0VsAl3izr4S7fs1MxPoW_KxHD7WQJIhE"
const val FIREBASE_AUTH_SIGNUP = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=$FIREBASE_APIKEY"
const val FIREBASE_AUTH_REFRESH = "https://securetoken.googleapis.com/v1/token?key=$FIREBASE_APIKEY"
const val FIREBASE_AUTH_SIGNIN = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=$FIREBASE_APIKEY"
const val FIREBASE_AUTH_DELETE = "https://identitytoolkit.googleapis.com/v1/accounts:delete?key=$FIREBASE_APIKEY"
const val KEY_USER_CACHE = "key_user_cache"
const val BASE_URL = "https://nasone.herokuapp.com/"
const val WORKER_URL = "test/worker"
const val BADGE_URL = "test/badge"
const val QUERY_PARAM_UUID = "uuid"
const val QUERY_PARAM_DATE ="date"