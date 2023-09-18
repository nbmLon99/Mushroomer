package com.nbmlon.mushroomer.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.api.ResponseCodeConstants.NETWORK_ERROR_CODE
import com.nbmlon.mushroomer.api.ResponseCodeConstants.NICKNAME_DUPLICATED
import com.nbmlon.mushroomer.databinding.ActivityLoginBinding
import com.nbmlon.mushroomer.databinding.DialogFindIdBinding
import com.nbmlon.mushroomer.databinding.DialogFindPwBinding
import com.nbmlon.mushroomer.databinding.DialogRegisterBinding
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest.LoginRequestDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest.TokenLoginRequestDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseResponse
import com.nbmlon.mushroomer.domain.LoginUseCaseResponse.FindResponseDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseResponse.GenerateTokenResponseDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseResponse.LoginResponseDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseResponse.SuccessResponseDomain
import com.nbmlon.mushroomer.model.User
import com.nbmlon.mushroomer.ui.MainActivity
import com.nbmlon.mushroomer.utils.BiometricPromptUtils
import com.nbmlon.mushroomer.utils.CHECKED_STATE_AUTO_LOGIN
import com.nbmlon.mushroomer.utils.CIPHERTEXT_WRAPPER
import com.nbmlon.mushroomer.utils.CryptographyManager
import com.nbmlon.mushroomer.utils.REFRESH_TOKEN_ENCRYPTION_KEY
import com.nbmlon.mushroomer.utils.SHARED_PREFS_FILENAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import taimoor.sultani.sweetalert2.Sweetalert

class LoginActivity : AppCompatActivity() {
   companion object{
       const val TAG = "LoginActivity"
   }

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModel>()

    private val cryptographyManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            applicationContext,
            SHARED_PREFS_FILENAME,              // 파일명
            Context.MODE_PRIVATE,               // 모드
            CIPHERTEXT_WRAPPER                  // 프리퍼런스 키
        )
    private lateinit var loading : Sweetalert
    private lateinit var sharedPrefs : SharedPreferences
    private var registerDialog : Sweetalert? = null
    private var registerBinding : DialogRegisterBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupForLoginWithPassword()


        sharedPrefs = this@LoginActivity.getSharedPreferences(
            SHARED_PREFS_FILENAME,  // 파일 이름
            Context.MODE_PRIVATE    // 모드
        )

        loginViewModel.response.observe(this, ::responseObserver )


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


        val autoLoginEnabled = sharedPrefs.getBoolean("autoLoginEnabled", false)
        //자동로그인
        if(ciphertextWrapper != null && !autoLoginEnabled){
            if (AppUser.refreshToken == null) {
                autoLogin()
            } else {
                // The user has already logged in, so proceed to the rest of the app
                // this is a todo for you, the developer
                updateApp()
            }
        }
        //지문로그인
        else if (ciphertextWrapper != null) {
            if (AppUser.refreshToken == null) {
                showBiometricPromptForDecryption()
            } else {
                // The user has already logged in, so proceed to the rest of the app
                // this is a todo for you, the developer
                updateApp()
            }
        }
    }
    private fun responseObserver(response : LoginUseCaseResponse){
        if( loading.isShowing )
            loading.dismissWithAnimation()
        when(response){
            is LoginResponseDomain ->{
                if(response.success){
                    AppUser.refreshToken = response.refreshToken
                    AppUser.token = response.token
                    AppUser.user = response.loginUser
                    AppUser.percent = response.percentage


                    //자동로그인 토큰 저장
                    if(sharedPrefs.getBoolean("autoLoginEnabled", false)){
                        encryptAndStoreServerTokenForAutoLogin()
                    }
                    updateApp()
                }
            }
            is FindResponseDomain ->{
                if(response.success)
                    TODO("아이디 비번 찾기 결과값 표시")

            }
            is SuccessResponseDomain ->{
                if(response.success){
                    Toast.makeText(this@LoginActivity,"성공",Toast.LENGTH_SHORT).show()
                    if(registerDialog != null){
                        registerDialog!!.dismissWithAnimation()
                        registerDialog = null
                        registerBinding = null
                    }
                }else if(response.code == NICKNAME_DUPLICATED){
                    Toast.makeText(this@LoginActivity,"닉네임 중복",Toast.LENGTH_SHORT).show()
                    if(registerDialog != null){
                        registerBinding!!.etNickname.error = "닉네임 중복입니다."
                    }

                }else if(response.code == NETWORK_ERROR_CODE){
                    Toast.makeText(this@LoginActivity,"네트워크 연결을 확인해주세요",Toast.LENGTH_SHORT).show()

                } else{
                    Toast.makeText(this@LoginActivity,"다시 시도해주세요",Toast.LENGTH_SHORT).show()
                }
            }
            is GenerateTokenResponseDomain -> {

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
        binding.login.setOnClickListener {
            // autologin여부 저장
            val ableAutoLogin = binding.ckboxAutoLogin.isChecked
            sharedPrefs.edit()
                .putBoolean(CHECKED_STATE_AUTO_LOGIN, ableAutoLogin)  // "autoLoginEnabled" 키에 true 값을 저장
                .apply()


            //라이브데이터로 변경된 부분
            loginViewModel.request(
                    LoginRequestDomain(
                        email =    binding.username.text.toString(),
                        password = binding.password.text.toString()
                    )
            )
            loading.show()



        }
    }

    private fun updateApp() {
        AppUser.user = User(1,binding.username.text.toString(),"","닉네임","","01000000000")
        AppUser.percent = 50
        startActivity(Intent(this,MainActivity::class.java))
        this@LoginActivity.finish()
    }




    // BIOMETRICS SECTION
    /**지문 복호화 프롬포트 **/
    private fun showBiometricPromptForDecryption() {
        ciphertextWrapper?.let { textWrapper ->
            val secretKeyName = REFRESH_TOKEN_ENCRYPTION_KEY
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
            CoroutineScope(Dispatchers.Default).launch {
                authResult.cryptoObject?.cipher?.let {
                    val plaintext =
                        cryptographyManager.decryptData(textWrapper.ciphertext, it)

                    //라이브 데이터로 변경된 내용
                    loginViewModel.request(TokenLoginRequestDomain(plaintext))
                    //----------------------
                    loading.show()
                }
            }
        }
    }


    /** AUTO_LOGIN SECTION **/
    private fun autoLogin(){
        ciphertextWrapper?.let { textWrapper ->
            CoroutineScope(Dispatchers.Default).launch{
                val secretKeyName = REFRESH_TOKEN_ENCRYPTION_KEY
                val cipher = cryptographyManager.getInitializedCipherForDecryption(
                    secretKeyName, textWrapper.initializationVector
                )
                val plaintext =
                    cryptographyManager.decryptData(textWrapper.ciphertext, cipher)

                //라이브 데이터로 변경된 내용
                loginViewModel.request(TokenLoginRequestDomain(plaintext))
                //----------------------
                loading.show()
            }
        }
    }

    //자동로그인을 위한 refreshToken 저장
    private fun encryptAndStoreServerTokenForAutoLogin(){
        CoroutineScope(Dispatchers.Default).launch {
            AppUser.refreshToken?.let{
                val cipher = cryptographyManager.getInitializedCipherForEncryption(
                    REFRESH_TOKEN_ENCRYPTION_KEY)
                val encryptedServerTokenWrapper = cryptographyManager.encryptData(it, cipher)
                cryptographyManager.persistCiphertextWrapperToSharedPrefs(
                    encryptedServerTokenWrapper,
                    this@LoginActivity.applicationContext,
                    SHARED_PREFS_FILENAME,
                    Context.MODE_PRIVATE,
                    CIPHERTEXT_WRAPPER
                )
            }
        }
    }







    private fun openFindIdDialog(){
        val dialog = Sweetalert(this, Sweetalert.NORMAL_TYPE)
        val dialogBinding = DialogFindIdBinding.inflate(layoutInflater).apply {
            btnFindId.setOnClickListener {

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

                dialog.dismissWithAnimation()
            }
        }
        dialog.apply {
            setCustomView(dialogBinding.root)
            show()
        }
    }

    private fun openRegisterDialog(){
        registerDialog = Sweetalert(this, Sweetalert.NORMAL_TYPE)
        registerBinding = DialogRegisterBinding.inflate(layoutInflater).apply {
            //회원가입 시도
            btnRegister.setOnClickListener {
                if(etName.text.toString().isEmpty() ){
                    etName.error ="빈칸을 채워주세요"
                }else if(etPhoneNumber.text.toString().isEmpty()){
                    etPhoneNumber.error ="빈칸을 채워주세요"
                }else if(etNickname.text.toString().isEmpty()){
                    etNickname.error = "빈칸을 채워주세요"
                }else if(!(etEmail.text.toString().contains('@') && Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches())){
                    etEmail.error = "이메일 형식을 확인해 주세요"
                }else if(etPassword.text.toString().length <=5 ) {
                    etPassword.error = "비밀번호는 최소 6글자입니다."
                }else if(!etPassword.text.equals(etPasswordConfirm.text)){
                    etPasswordConfirm.error = "비밀번호가 일치하지 않습니다!"
                }else{
                    loginViewModel.request(
                        LoginUseCaseRequest.RegisterRequestDomain(
                            name = etName.text.toString(),
                            email = etEmail.text.toString(),
                            password = etPassword.text.toString(),
                            nickname = etNickname.text.toString(),
                            cellphone = etPhoneNumber.text.toString()
                        )
                    )
                    loading.show()
                }


            }
        }
        registerDialog!!.apply {
            setCustomView(registerBinding!!.root)
            show()
        }
    }

}