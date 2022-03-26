package co.touchlab.kampkit.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kampkit.injectLogger
import co.touchlab.kampkit.models.DataState
import co.touchlab.kampkit.models.UserModel
import co.touchlab.kampkit.response.User
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class UserViewModel : ViewModel(),KoinComponent {

    private val log: Logger by injectLogger("UserViewModel")
    private val scope = viewModelScope
    private val userModel = UserModel()
    private val _userFlow : MutableStateFlow<DataState<User>> = MutableStateFlow(DataState(loading = true, empty = true))
    val userFlow : StateFlow<DataState<User>> = _userFlow


    init {
        observeUser()
    }

    private fun observeUser(){
        scope.launch {
            log.v("observing user")
            userModel.getUserFromCache().collect{
                data ->
                _userFlow.emit(data)
            }
        }
    }

    fun login(email : String, password : String){
        scope.launch {
            log.v("login")
            val data = userModel.getUserFromNetwork(email, password)
            _userFlow.value = data
        }
    }
}