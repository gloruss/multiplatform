package co.touchlab.kampkit.models

import co.touchlab.kampkit.DatabaseHelper
import co.touchlab.kampkit.injectLogger
import co.touchlab.kampkit.ktor.UserApi
import co.touchlab.kampkit.response.User
import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserModel : KoinComponent{
    private val dbHelper: DatabaseHelper by inject()
    private val settings: Settings by inject()
    private val userApi : UserApi by inject()
    private val log: Logger by injectLogger("UserModel")


    fun getUserFromCache() : Flow<DataState<User>> =
        dbHelper.selectUser()
            .mapNotNull { itemList ->
                if(itemList.isEmpty()){
                    DataState(empty = true)
                } else{
                    val user = itemList[0]
                    DataState(
                        data = User(idToken = user.idToken, email = user.email, refreshToken = user.refreshToken, uid = user.uid,
                        expiresIn = 0),
                        empty = false
                    )
                }

            }

    suspend fun getUserFromNetwork(email : String, password : String) :DataState<User> {
        return try {
            val user = userApi.login(email, password)
           dbHelper.insertUser(user)
            DataState(
                data = user
            )
        } catch (e: Exception) {
            log.e(e) { "Error login $email $password" }
            DataState<User>(exception = "Unable login")
        }
    }




}