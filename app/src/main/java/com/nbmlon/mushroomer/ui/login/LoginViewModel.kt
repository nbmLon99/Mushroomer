package com.nbmlon.mushroomer.ui.login

import android.provider.ContactsContract.CommonDataKinds.Nickname
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.model.User

/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.util.Patterns
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.nbmlon.mushroomer.model.RegisterRequest

class LoginViewModel : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginWithPasswordFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()               //로그인
    val loginResult: LiveData<LoginResult> = _loginResult   
    private val _registerResult = MutableLiveData<LoginResult>()            //회원가입
    val registerResult: LiveData<LoginResult> = _registerResult
    private val _nicknameUsable = MutableLiveData<LoginResult>()            //닉네임 중복 검사
    val nicknameUsable: LiveData<LoginResult> = _nicknameUsable

    private val _findIDinResult = MutableLiveData<LoginResult>()
    val findIDinResult: LiveData<LoginResult> = _findIDinResult             //ID찾기
    private val _findPWResult = MutableLiveData<LoginResult>()
    val findPWResult: LiveData<LoginResult> = _findPWResult                 //PW찾기

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
            _findIDinResult.value = LoginResult(true)
        } else {
            _findIDinResult.value = LoginResult(false)
        }
    }
    fun requestFindPW(){
        //실패
        if(TODO("비밀번호찾기")){
            _findPWResult.value = LoginResult(true)
        } else {
            _findPWResult.value = LoginResult(false)
        }

    }
    fun requestCheckNickname(nickname: String){
        if(TODO("회원가입")){
            _registerResult.value = LoginResult(true)
        } else {
            _registerResult.value = LoginResult(false)
        }
    }
    fun requestRegister(request : RegisterRequest){
        if(TODO("회원가입")){
            _registerResult.value = LoginResult(true)
        } else {
            _registerResult.value = LoginResult(false)
        }
    }



    /** id / 비밀번호 / 자동로그인 check -> Login**/
    fun login(username: String, password: String, autoLogin : Boolean = false) {
        if (isUserNameValid(username) && isPasswordValid(password)) {
            //send And Get Tokens
            val testUser = User.getDummy()

            // Normally this method would asynchronously send this to your server and your sever
            // would return a token. For high sensitivity apps such as banking, you would keep that
            // token in transient memory similar to my SampleAppUser object. This way the user
            // must login each time they start the app.
            // In this sample, we don't call a server. Instead we use a fake token that we set
            // right here:

            AppUser.token = null
            AppUser.user = testUser

            _loginResult.value = LoginResult(true)
        } else {
            _loginResult.value = LoginResult(false)
        }
    }



}