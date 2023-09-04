package com.nbmlon.mushroomer.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.ActivityLoginBinding
import com.nbmlon.mushroomer.databinding.DialogFindIdBinding
import com.nbmlon.mushroomer.databinding.DialogFindPwBinding
import com.nbmlon.mushroomer.databinding.DialogRegisterBinding
import com.nbmlon.mushroomer.model.User
import com.nbmlon.mushroomer.ui.MainActivity
import com.nbmlon.mushroomer.utils.CryptographyManager
import taimoor.sultani.sweetalert2.Sweetalert

class LoginActivity : AppCompatActivity() {
   companion object{
       const val TAG = "LoginActivity"
   }

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModel>()

    private lateinit var biometricPrompt: BiometricPrompt
    private val cryptographyManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            applicationContext,
            SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CIPHERTEXT_WRAPPER
        )
    private lateinit var loading :Sweetalert

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupForLoginWithPassword()

        loading= Sweetalert(this,Sweetalert.PROGRESS_TYPE).apply {
            setTitleText(R.string.loading)
            setCancelable(false)
        }
        binding.apply {
            btnRegister.setOnClickListener { openRegisterDialog() }
            btnFindID.setOnClickListener { openFindIdDialog() }
            btnFindPW.setOnClickListener { openFindPwDialog() }
        }
    }



    /**
     * The logic is kept inside onResume instead of onCreate so that authorizing biometrics takes
     * immediate effect.
     */
    override fun onResume() {
        super.onResume()

        if (ciphertextWrapper != null) {
            if (AppUser.token == null) {
                showBiometricPromptForDecryption()
            } else {
                // The user has already logged in, so proceed to the rest of the app
                // this is a todo for you, the developer
                updateApp()
            }
        }
    }

    // USERNAME + PASSWORD SECTION
    private fun setupForLoginWithPassword() {
        loginViewModel.loginWithPasswordFormState.observe(this, Observer { formState ->
            val loginState = formState ?: return@Observer
            when (loginState) {
                is SuccessfulLoginFormState -> binding.login.isEnabled = loginState.isDataValid
                is FailedLoginFormState -> {
                    loginState.usernameError?.let { binding.username.error = getString(it) }
                    loginState.passwordError?.let { binding.password.error = getString(it) }
                }
            }
        })
        loginViewModel.loginResult.observe(this, Observer {
            val loginResult = it ?: return@Observer
            if (loginResult.success) {
                Log.d(TAG,
                    "You successfully signed up using password as: user " +
                            "${AppUser.user?.email} with fake token ${AppUser.token}")
                updateApp()
            }
        })
        binding.username.doAfterTextChanged {
            loginViewModel.onLoginDataChanged(
                binding.username.text.toString(),
                binding.password.text.toString()
            )
        }
        binding.password.doAfterTextChanged {
            loginViewModel.onLoginDataChanged(
                binding.username.text.toString(),
                binding.password.text.toString()
            )
        }
//        binding.password.setOnEditorActionListener { _, actionId, _ ->
//            when (actionId) {
//                EditorInfo.IME_ACTION_DONE ->
//                    loginViewModel.login(
//                        binding.username.text.toString(),
//                        binding.password.text.toString()
//                    )
//            }
//            false
//        }
        binding.login.setOnClickListener {
            loginViewModel.login(
                binding.username.text.toString(),
                binding.password.text.toString(),
                binding.ckboxAutoLogin.isChecked
            )
            Log.d(TAG, "Username ${AppUser.user?.email}; fake token ${AppUser.token}")
        }
        Log.d(TAG, "Username ${AppUser.user?.email}; fake token ${AppUser.token}")
    }

    private fun updateApp() {
        AppUser.user = User(1L,binding.username.text.toString(),"닉네임","")
        AppUser.percent = 50
        startActivity(Intent(this,MainActivity::class.java))
        this@LoginActivity.finish()
    }




    // BIOMETRICS SECTION

    /**지문 복호화 프롬포트 **/
    private fun showBiometricPromptForDecryption() {
        ciphertextWrapper?.let { textWrapper ->
            val secretKeyName = getString(R.string.secret_key_name)
            val cipher = cryptographyManager.getInitializedCipherForDecryption(
                secretKeyName, textWrapper.initializationVector
            )
            val biometricPrompt =
                BiometricPromptUtils.createBiometricPrompt(
                    this,
                    ::decryptServerTokenFromStorage
                )
            val promptInfo = BiometricPromptUtils.createPromptInfo(this)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun decryptServerTokenFromStorage(authResult: BiometricPrompt.AuthenticationResult) {
        ciphertextWrapper?.let { textWrapper ->
            authResult.cryptoObject?.cipher?.let {
                val plaintext =
                    cryptographyManager.decryptData(textWrapper.ciphertext, it)
                AppUser.token = plaintext
                // Now that you have the token, you can query server for everything else
                // the only reason we call this fakeToken is because we didn't really get it from
                // the server. In your case, you will have gotten it from the server the first time
                // and therefore, it's a real token.

                updateApp()
            }
        }
    }


    private fun openFindIdDialog(){
        val dialog = Sweetalert(this, Sweetalert.NORMAL_TYPE)
        val dialogBinding = DialogFindIdBinding.inflate(layoutInflater).apply {
            btnFindId.setOnClickListener {
                loginViewModel.findIDinResult.observeOnce(this@LoginActivity, Observer { result ->
                    if(result.success){

                    }
                    else{

                    }
                })
//                    loading.show()
//                    loginViewModel.requestFindID()
                dialog.dismissWithAnimation()
            }
        }
        dialog.apply {
            setCustomView(dialogBinding.root)
            show()
        }
    }

    private fun openFindPwDialog(){


        val dialog = Sweetalert(this, Sweetalert.NORMAL_TYPE)
        val dialogBinding = DialogFindPwBinding.inflate(layoutInflater).apply {
            btnFindPw.setOnClickListener {
                loginViewModel.findPWResult.observeOnce(this@LoginActivity, Observer { result ->
                    if(result.success){

                    }
                    else{

                    }
                })
//                    loading.show()
//                    loginViewModel.requestFindPW()
                dialog.dismissWithAnimation()
            }
        }
        dialog.apply {
            setCustomView(dialogBinding.root)
            show()
        }
    }

    private fun openRegisterDialog(){
        val dialog = Sweetalert(this, Sweetalert.NORMAL_TYPE)
        val dialogBinding = DialogRegisterBinding.inflate(layoutInflater).apply {
            //회원가입 시도
            btnRegister.setOnClickListener {
                loginViewModel.registerResult.observeOnce(this@LoginActivity, Observer { result ->
                    if(result.success){

                    }
                    else{

                    }
                })
//                    loading.show()
//                    loginViewModel.requesRegister()
                dialog.dismissWithAnimation()
            }
            
            //닉네임 중복 검사
            btnNicknameCheck.setOnClickListener {
                loginViewModel.registerResult.observeOnce(this@LoginActivity, Observer { result ->
                    //중복없음 -> 사용가능
                    if(result.success){
                        etNickname.error = null
                        Toast.makeText(this@LoginActivity,"사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show()
                    }
                    //중복 -> 사용불가
                    else{
                        etNickname.error = "중복된 닉네임입니다."
                    }
                })
//                    loginViewModel.requesNickNameCheck()
            }
        }
        dialog.apply {
            setCustomView(dialogBinding.root)
            show()
        }
    }


    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T) {
                observer.onChanged(t)
                removeObserver(this) // 옵저버를 한 번 호출한 후 제거
            }
        })
    }

}