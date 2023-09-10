package com.nbmlon.mushroomer.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.api.dto.UserRequestDTO
import com.nbmlon.mushroomer.api.dto.UserRequestDTO.FindIdRequestDTO
import com.nbmlon.mushroomer.api.dto.UserRequestDTO.FindPwRequestDTO
import com.nbmlon.mushroomer.api.dto.UserRequestDTO.LoginRequestDTO
import com.nbmlon.mushroomer.api.dto.UserRequestDTO.RegisterRequestDTO
import com.nbmlon.mushroomer.api.dto.UserRequestDTO.TokenLoginRequestDTO
import com.nbmlon.mushroomer.api.dto.UserResponseDTO
import com.nbmlon.mushroomer.api.dto.UserResponseDTO.FindResponseDTO
import com.nbmlon.mushroomer.api.dto.UserResponseDTO.LoginResponseDTO
import com.nbmlon.mushroomer.api.dto.UserResponseDTO.SuccessResponse
import com.nbmlon.mushroomer.data.user.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginWithPasswordFormState: LiveData<LoginFormState> = _loginForm

    val _response = MutableLiveData<UserResponse>()
    val response : LiveData<UserResponse> = _response
    val request : (UserRequest) -> Unit

    init {
        request = { requestType ->
            val dto = requestType.dto
            viewModelScope.launch {
                when(requestType){
                    //로그인
                    is UserRequest.Login -> {
                        if ((isUserNameValid(requestType.dto.email) && isPasswordValid(requestType.dto.password))) {
                            _response.value = repository.login(dto)
                        }
                    }
                    //토큰로그인
                    is UserRequest.LoginWithToken -> {
                        _response.value = repository.loginWithToken(dto)
                    }

                    //회원가입
                    is UserRequest.Register -> {
                        _response.value =  repository.register(dto)
                    }
                    //아이디찾기
                    is UserRequest.FindPW -> {
                        _response.value = repository.findID(dto)
                    };
                    //비밀번호찾기
                    is UserRequest.FindID -> {
                        _response.value = repository.findPW(dto)
                    };
                }
            }

        }
    }

    fun onLoginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = FailedLoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = FailedLoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = SuccessfulLoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            //username.isNotBlank()
            false;
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}

sealed class UserRequest() {
    abstract val dto: UserRequestDTO
    data class Login(override val dto : LoginRequestDTO) : UserRequest()
    data class LoginWithToken(override val dto : TokenLoginRequestDTO) : UserRequest()
    data class FindPW(override val dto : FindPwRequestDTO) : UserRequest()
    data class FindID(override val dto : FindIdRequestDTO) : UserRequest()
    data class Register(override val dto : RegisterRequestDTO) : UserRequest()
}

sealed class UserResponse{
    abstract val dto : UserResponseDTO
    data class Login(override val dto : LoginResponseDTO) : UserResponse()
    data class FindIdPw(override val dto : FindResponseDTO) : UserResponse()
    data class Register(override val dto : SuccessResponse) : UserResponse()
}