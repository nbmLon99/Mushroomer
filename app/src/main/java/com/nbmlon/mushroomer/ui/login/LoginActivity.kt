package com.nbmlon.mushroomer.ui.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.databinding.ActivityLoginBinding
import com.nbmlon.mushroomer.ui.MainActivity
import com.nbmlon.mushroomer.utils.CryptographyManager

class LoginActivity : AppCompatActivity() {
   companion object{
       const val TAG = "LoginActivity"
   }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var cryptographyManager: CryptographyManager
    private val loginViewModel by viewModels<LoginViewModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupForLoginWithPassword()
    }



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
                            "${AppUser.user?.name} with fake token ${AppUser.token}")
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
                binding.password.text.toString()
            )
            Log.d(TAG, "Username ${AppUser.user?.name}; fake token ${AppUser.token}")
        }
        Log.d(TAG, "Username ${AppUser.user?.name}; fake token ${AppUser.token}")
    }

    private fun updateApp() {
        startActivity(Intent(this,MainActivity::class.java))
    }





    private fun showBiometricPromptForEncryption() {
        val canAuthenticate = BiometricManager.from(applicationContext).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            val secretKeyName = getString(R.string.secret_key_name)
            cryptographyManager = CryptographyManager()
            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
            val biometricPrompt =
                BiometricPromptUtils.createBiometricPrompt(this, ::encryptAndStoreServerToken)
            val promptInfo = BiometricPromptUtils.createPromptInfo(this)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun encryptAndStoreServerToken(authResult: BiometricPrompt.AuthenticationResult) {
        authResult.cryptoObject?.cipher?.apply {
            AppUser.token?.let { token ->
                Log.d(TAG, "The token from server is $token")
                val encryptedServerTokenWrapper = cryptographyManager.encryptData(token, this)
                cryptographyManager.persistCiphertextWrapperToSharedPrefs(
                    encryptedServerTokenWrapper,
                    applicationContext,
                    SHARED_PREFS_FILENAME,
                    Context.MODE_PRIVATE,
                    CIPHERTEXT_WRAPPER
                )
            }
        }
        finish()
    }

}