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

    val _response = MutableLiveData<LoginResponse>()
    val response : LiveData<LoginResponse> = _response
    val request : (LoginRequest) -> Unit

    init {
        request = { requestType ->
            val dto = requestType.dto
            viewModelScope.launch {
                when(requestType){
                    //로그인
                    is LoginRequest.Login -> {
                        if ((isUserNameValid(requestType.dto.email) && isPasswordValid(requestType.dto.password))) {
                            _response.value = LoginResponse.Login(repository.login(dto))
                        }
                    }
                    //토큰로그인
                    is LoginRequest.LoginWithToken -> {
                        _response.value = LoginResponse.Login(repository.loginWithToken(dto))
                    }

                    //회원가입
                    is LoginRequest.Register -> {
                        _response.value = LoginResponse.Register( repository.register(dto))
                    }
                    //아이디찾기
                    is LoginRequest.FindPW -> {
                        _response.value = LoginResponse.FindIdPw(repository.findID(dto))
                    };
                    //비밀번호찾기
                    is LoginRequest.FindID -> {
                        _response.value = LoginResponse.FindIdPw( repository.findPW(dto) )
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

sealed class LoginRequest() {
    abstract val dto: UserRequestDTO
    data class Login(override val dto : LoginRequestDTO) : LoginRequest()
    data class LoginWithToken(override val dto : TokenLoginRequestDTO) : LoginRequest()

    data class FindPW(override val dto : FindPwRequestDTO) : LoginRequest()
    data class FindID(override val dto : FindIdRequestDTO) : LoginRequest()
    data class Register(override val dto : RegisterRequestDTO) : LoginRequest()
}

sealed class LoginResponse{
    abstract val dto : UserResponseDTO
    data class Login(override val dto : LoginResponseDTO) : LoginResponse()

    data class FindIdPw(override val dto : FindResponseDTO) : LoginResponse()
    data class Register(override val dto : SuccessResponse) : LoginResponse()
}