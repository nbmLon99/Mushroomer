package com.nbmlon.mushroomer.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.data.user.UserRepository
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest.FindIdRequestDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest.FindPwRequestDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest.LoginRequestDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest.RegisterRequestDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest.TokenLoginRequestDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseResponse
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginWithPasswordFormState: LiveData<LoginFormState> = _loginForm

    val _response = MutableLiveData<LoginUseCaseResponse>()
    val response : LiveData<LoginUseCaseResponse> = _response
    val request : (LoginUseCaseRequest) -> Unit

    init {
        request = { domain ->

            viewModelScope.launch {
                when(domain){
                    //로그인
                    is LoginRequestDomain -> {
                        if ((isUserNameValid(domain.email) && isPasswordValid(domain.password))) {
                            _response.value = repository.login(domain)
                        }
                    }
                    //토큰로그인
                    is TokenLoginRequestDomain -> {
                        _response.value = repository.loginWithToken(domain)
                    }

                    //회원가입
                    is RegisterRequestDomain -> {
                        _response.value =  repository.register(domain)
                    }
                    //아이디찾기
                    is FindIdRequestDomain -> {
                        _response.value = repository.findID(domain)
                    };
                    //비밀번호찾기
                    is FindPwRequestDomain -> {
                        _response.value = repository.findPW(domain)
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