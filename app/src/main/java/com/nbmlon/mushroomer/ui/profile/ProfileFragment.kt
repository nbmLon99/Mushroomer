package com.nbmlon.mushroomer.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.AppUserManager
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.api.EndConverter
import com.nbmlon.mushroomer.api.ResponseCodeConstants.NETWORK_ERROR_CODE
import com.nbmlon.mushroomer.databinding.DialogLoginMethodBinding
import com.nbmlon.mushroomer.databinding.DialogModifyNicknameBinding
import com.nbmlon.mushroomer.databinding.DialogModifyPasswordBinding
import com.nbmlon.mushroomer.databinding.DialogWithdrawalBinding
import com.nbmlon.mushroomer.databinding.FragmentProfileBinding
import com.nbmlon.mushroomer.domain.ProfileUseCaseRequest
import com.nbmlon.mushroomer.domain.ProfileUseCaseResponse
import com.nbmlon.mushroomer.ui.login.LoginActivity
import com.nbmlon.mushroomer.utils.BiometricPromptUtils
import com.nbmlon.mushroomer.utils.CIPHERTEXT_WRAPPER
import com.nbmlon.mushroomer.utils.CryptographyManager
import com.nbmlon.mushroomer.utils.REFRESH_TOKEN_ENCRYPTION_KEY
import com.nbmlon.mushroomer.utils.SHARED_PREFS_FILENAME
import taimoor.sultani.sweetalert2.Sweetalert


/**
 * 내 설정 화면
 */
class ProfileFragment : Fragment(), DialogListener {
    companion object {
        const val TAG = "ProfileFragment"
    }

    private var _binding : FragmentProfileBinding?  = null
    private val binding get() = _binding!!
    private val profileViewModel by viewModels<ProfileViewModel>()
    private val cryptographyManager = CryptographyManager()
    private var onSuccessResponse : ((ProfileUseCaseResponse) -> (Unit))? = null
    private lateinit var loading : Sweetalert

    private val iconLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result->
        onIconResult(result)
    }

    private fun onIconResult(result: ActivityResult?) {
        if (result?.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                if (data.data != null) {
                    // 단일 이미지 선택 시
                    val imageUri = data.data!!
                    profileViewModel.request(
                        ProfileUseCaseRequest.ModifyIconRequestDomain(
                            icon = EndConverter.uriToPart(requireContext(), uri = imageUri)
                        )
                    )
                }
            }
        }else{
            Toast.makeText(context,getString(R.string.error_msg),Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater)
        loading = Sweetalert(context, Sweetalert.PROGRESS_TYPE).apply { setCancelable(false) }
        profileViewModel.response.observe(viewLifecycleOwner){response ->
            if(loading.isShowing)
                loading.dismissWithAnimation()
            if(response.success){
                onSuccessResponse?.let { it(response) }
            }else if(response.code == NETWORK_ERROR_CODE) {
                Toast.makeText(context, getString(R.string.network_error_msg),Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, getString(R.string.error_msg),Toast.LENGTH_SHORT).show()
            }

            onSuccessResponse = null
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            user = AppUser.user
            btnChgNickname.setOnClickListener { showModifyNicknameDialog() }
            btnChgPassword.setOnClickListener { showModifyPasswordDialog() }
            btnWithdrawal.setOnClickListener { showWithdrawalDialog() }
            btnChgLoginMethod.setOnClickListener { showDialogLoginMethod() }

            iconFrame.setOnClickListener {
                iconLauncher.launch(
                    Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "image/*"
                    }
                )
            }
            btnLogout.setOnClickListener { showLogoutDialog() }
        }
    }


    private fun showDialogLoginMethod(){
        Sweetalert(context, Sweetalert.NORMAL_TYPE).apply {
            val dialogBinding = DialogLoginMethodBinding.inflate(layoutInflater)
            titleText = getString(R.string.chg_app_login)
            setCustomView(dialogBinding.root)
            setNeutralButton("취소"){ it.dismissWithAnimation()}
            setCancelButton("변경"){
                dialogBinding.methodRadio.checkedRadioButtonId?.let {
                    loginMethodModify(it)
                }}
            show()
        }
    }

    private fun logout(){
        AppUser.token = null
        AppUser.user = null
        if(AppUserManager.isAppUserSaved(requireContext())){
            AppUserManager.clearAppUser(requireContext())
        }
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showLogoutDialog(){
        Sweetalert(context, Sweetalert.NORMAL_TYPE).apply {
            titleText = "로그아웃 하시겠습니까?"
            setNeutralButton("취소"){ it.dismissWithAnimation()}
            setCancelButton("네"){
                logout()
                it.dismissWithAnimation()
            }
            show()
        }
    }

    //회원 탈퇴
    private fun showWithdrawalDialog(){
        val dialog = Sweetalert(context, Sweetalert.NORMAL_TYPE)
        val dialogBinding = DialogWithdrawalBinding.inflate(layoutInflater).apply {
            btnStart.setOnClickListener {
                if(!(etEmail.text.toString().contains('@') && Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches())){
                    etEmail.error = "이메일 형식을 확인해 주세요"
                }else if(etPassword.text.toString().length <=5 ) {
                    etPassword.error = "비밀번호는 최소 6글자입니다."
                }else if(!etPassword.text.equals(etPasswordConfirm.text)){
                    etPasswordConfirm.error = "비밀번호가 일치하지 않습니다!"
                }else{
                    onSuccessResponse ={
                        Sweetalert(context,Sweetalert.NORMAL_TYPE).apply {
                            titleText = "회원 탈퇴가 성공적으로 처리되었습니다!\n이용해 주셔서 갑사합니다."
                            setCancelButton("확인"){
                                it.dismissWithAnimation()
                                logout()
                            }
                            setCancelable(false)
                            show()
                        }
                    }
                    profileViewModel.request(ProfileUseCaseRequest.WithdrawalRequestDomain(
                        email = etEmail.text.toString(), password = etPassword.text.toString()
                    ))
                }
            }
        }
        dialog.apply {
            setCustomView(dialogBinding.root)
            show()
        }
    }

    //닉네임 변경
    private fun showModifyNicknameDialog(){
        val dialog = Sweetalert(context, Sweetalert.NORMAL_TYPE)
        val dialogBinding = DialogModifyNicknameBinding.inflate(layoutInflater).apply {
            btnStart.setOnClickListener {
                if(etNickname.text.toString().length < 3){
                    etNickname.error = "닉네임은 최소 3글자입니다."
                }else{
                    val my = AppUser.user!!
                    val newNickname = etNickname.text.toString()
                    profileViewModel.request(ProfileUseCaseRequest.ModifyNicknameRequestDomain(
                        nickname = newNickname
                    ))
                    onSuccessResponse = {
                        Sweetalert(context,Sweetalert.NORMAL_TYPE).apply {
                            titleText = "닉네임 변경이 성공적으로 처리되었습니다!"
                            setCancelButton("확인"){
                                AppUser.user = AppUser.user!!.getModifiedNickname(newNickname)
                                binding.user = AppUser.user!!
                                it.dismissWithAnimation()
                            }
                            setCancelable(false)
                            show()
                        }
                    }
                }
            }
        }
        dialog.apply {
            setCustomView(dialogBinding.root)
            show()
        }
    }

    //비밀번호 변경
    private fun showModifyPasswordDialog(){
        val dialog = Sweetalert(context, Sweetalert.NORMAL_TYPE)
        val dialogBinding = DialogModifyPasswordBinding.inflate(layoutInflater).apply {
            btnStart.setOnClickListener {
                if(etNewPassword.text.toString().length <=5 ) {
                    etNewPassword.error = "비밀번호는 최소 6글자입니다."
                }else if(!etNewPassword.text.equals(etNewPasswordConfirm.text)){
                    etNewPasswordConfirm.error = "비밀번호가 일치하지 않습니다!"
                }else{
                    profileViewModel.request(ProfileUseCaseRequest.ModifyPwdRequestDomain(
                        password = etOldPassword.text.toString(),
                        modifiedPwd = etNewPassword.text.toString(),
                        modified = AppUser.user!!
                        )
                    )
                    onSuccessResponse = {
                        Sweetalert(context,Sweetalert.NORMAL_TYPE).apply {
                            titleText = "비밀번호 변경이 성공적으로 처리되었습니다!"
                            setCancelButton("확인"){ it.dismissWithAnimation() }
                            setCancelable(false)
                            show()
                        }
                    }
                }
            }
        }
        dialog.setCustomView(dialogBinding.root).show()
    }




    override fun loginMethodModify(radioCheckedId: Int?) {
        radioCheckedId?.let { id->
            //deleteServerToken()
            when(id){
                R.id.login_biometric -> setAutoLogin()
                R.id.login_password-> noAutoLogin() ;
            }
        }
    }
    
    //자동로그인 설정
    private fun setAutoLogin(){
        AppUserManager.saveAppUser(requireContext(), AppUser)
    }
    
    //자동로그인 해제
    private fun noAutoLogin(){
        AppUserManager.clearAppUser(requireContext())
    }




    /**  지문으로 토큰 등록  **/
    fun showBiometricPromptForEncryption() {
        val canAuthenticate = BiometricManager.from(requireActivity().applicationContext).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            val secretKeyName = REFRESH_TOKEN_ENCRYPTION_KEY
            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
            val biometricPrompt =
                BiometricPromptUtils.createBiometricPrompt(requireActivity()as AppCompatActivity, ::encryptAndStoreServerToken)
            val promptInfo = BiometricPromptUtils.createPromptInfo(requireActivity() as AppCompatActivity)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }


    /** 지문 인증을 위한 서버 토큰 저장 **/
    private fun encryptAndStoreServerToken(authResult: BiometricPrompt.AuthenticationResult) {
        authResult.cryptoObject?.cipher?.apply {
            AppUser.token?.let { token ->
                //Log.d(TAG, "The token from server is $token")
                val encryptedServerTokenWrapper = cryptographyManager.encryptData(token, this)
                cryptographyManager.persistCiphertextWrapperToSharedPrefs(
                    encryptedServerTokenWrapper,
                    requireActivity().applicationContext,
                    SHARED_PREFS_FILENAME,
                    Context.MODE_PRIVATE,
                    CIPHERTEXT_WRAPPER
                )
            }
        }
    }



    fun deleteServerToken(){
        val sharedPreferences = requireActivity().getSharedPreferences(
            SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit().remove(CIPHERTEXT_WRAPPER).apply()
    }

    fun modifyImage(){
        TODO("프로필 이미지 변경 연결")
    }
}


interface DialogListener{
    fun loginMethodModify(radioCheckedId : Int?)
}