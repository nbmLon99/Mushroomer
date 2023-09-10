package com.nbmlon.mushroomer.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.api.dto.LoginResponse
import com.nbmlon.mushroomer.api.dto.RegisterRequest
import com.nbmlon.mushroomer.api.dto.SuccessResponse
import com.nbmlon.mushroomer.data.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginWithPasswordFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResponse>()               //로그인
    val loginResult: LiveData<LoginResponse> = _loginResult
    private val _registerResult = MutableLiveData<SuccessResponse>()            //회원가입
    val registerResult: LiveData<SuccessResponse> = _registerResult
    private val _nicknameUsable = MutableLiveData<SuccessResponse>()            //닉네임 중복 검사
    val nicknameUsable: LiveData<SuccessResponse> = _nicknameUsable

    private val _findIDinResult = MutableLiveData<CallResult>()
    val findIDinResult: LiveData<CallResult> = _findIDinResult             //ID찾기
    private val _findPWResult = MutableLiveData<CallResult>()
    val findPWResult: LiveData<CallResult> = _findPWResult                 //PW찾기

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

    fun requestFindID(){
        // 성공
        if(TODO("아이디찾기")){
            _findIDinResult.value = CallResult(true)
        } else {
            _findIDinResult.value = CallResult(false)
        }
    }
    fun requestFindPW(){
        //실패
        if(TODO("비밀번호찾기")){
            _findPWResult.value = CallResult(true)
        } else {
            _findPWResult.value = CallResult(false)
        }

    }


    /** 회원가입 호출 이전에 반드시 닉네임 중복 검사 실시할것  **/
    fun requestRegister(request : RegisterRequest){
        viewModelScope.launch {
            repository.register(request).enqueue(object : Callback<SuccessResponse>{
                override fun onResponse(
                    call: Call<SuccessResponse>,
                    response: Response<SuccessResponse>
                ) {
                    _registerResult.value = response.body()
                }

                override fun onFailure(call: Call<SuccessResponse>, t: Throwable) {
                    _registerResult.value = SuccessResponse(false, 200, "")
                }

            })
        }
        if(TODO("회원가입")){
        } else {
        }
    }



    suspend fun loginWithToken(refreshToken : String){
        val call = withContext(Dispatchers.IO){
            repository.loginWithToken(refreshToken)
        }

        withContext(Dispatchers.Main){
            call.enqueue(object : Callback<LoginResponse>{
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    _loginResult.value = response.body()
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    _loginResult.value = LoginResponse(false)
                }
            })
        }
    }


    /** id / 비밀번호 / 자동로그인 check -> Login**/
    fun login(username: String, password: String) {
        if((isUserNameValid(username) && isPasswordValid(password))){
            viewModelScope.launch{
                    repository.login(username,password).enqueue(object : Callback<LoginResponse>{
                        override fun onResponse(
                            call: Call<LoginResponse>,
                            response: Response<LoginResponse>
                        ) {
                            _loginResult.value = response.body()
                        }
                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            _loginResult.value = LoginResponse(false)
                        }
                    })
            }
        }
    }



}