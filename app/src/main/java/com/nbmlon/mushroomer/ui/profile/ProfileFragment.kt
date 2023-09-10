package com.nbmlon.mushroomer.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.DialogEdittextBinding
import com.nbmlon.mushroomer.databinding.DialogLoginMethodBinding
import com.nbmlon.mushroomer.databinding.FragmentProfileBinding
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

    enum class SettingType {
        MODIFY_PASSWORD,
        MODIFY_NICKNAME,
        WITHDRAWAL,

    }

    private var _binding : FragmentProfileBinding?  = null
    private val binding get() = _binding!!

    private val profileViewModel by viewModels<ProfileViewModel>()
    private val cryptographyManager = CryptographyManager()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            user = AppUser.user
            btnChgNickname.setOnClickListener { showDialogWithEditText(SettingType.MODIFY_NICKNAME, this@ProfileFragment as DialogListener) }
            btnChgPassword.setOnClickListener { showDialogWithEditText(SettingType.MODIFY_PASSWORD, this@ProfileFragment as DialogListener) }
            btnWithdrawal.setOnClickListener { showDialogWithEditText(SettingType.WITHDRAWAL, this@ProfileFragment as DialogListener) }
            btnChgLoginMethod.setOnClickListener { showDialogLoginMethod() }

            btnLogout.setOnClickListener { showLogoutDialog() }
        }
    }

    private fun showDialogWithEditText( settingType: SettingType, dialogListener: DialogListener,){
        var title : String; var content : String; var btnText : String
        when(settingType){
            SettingType.MODIFY_NICKNAME->{
                title = resources.getString(R.string.dialog_title_modify_nickname)
                content = resources.getString(R.string.dialog_content_modify_nickname)
                btnText = resources.getString(R.string.dialog_btnText_modify_nickname)
            }
            SettingType.MODIFY_PASSWORD -> {
                title = resources.getString(R.string.dialog_title_modify_password)
                content = resources.getString(R.string.dialog_content_modify_password)
                btnText = resources.getString(R.string.dialog_btnText_modify_password)
            }
            SettingType.WITHDRAWAL -> {
                title = resources.getString(R.string.dialog_title_withdrawal)
                content = resources.getString(R.string.dialog_content_withdrawal)
                btnText = resources.getString(R.string.dialog_btnText_withdrawal)
            }
            else -> error("타입 오류")
        }


        Sweetalert(context, Sweetalert.NORMAL_TYPE).apply {
            val dialogBinding = DialogEdittextBinding.inflate(layoutInflater).apply {
                tvContent.text = content
            }
            val editText = dialogBinding.editText
            titleText = title
            setCustomView(dialogBinding.root)
            setCancelButton(btnText){
                dialogListener.dialogEditDone(etString = editText.text.toString(), settingType, it)
                it.dismissWithAnimation()
            }
            setNeutralButton(resources.getString(R.string.cancel)){
                it.dismissWithAnimation()
            }
            show()
        }
    }

    private fun showDialogLoginMethod(){
        Sweetalert(context, Sweetalert.NORMAL_TYPE).apply {
            val dialogBinding = DialogLoginMethodBinding.inflate(layoutInflater)
            titleText = "로그인 방법 변경"
            setCustomView(dialogBinding.root)
            setNeutralButton("취소"){ it.dismissWithAnimation()}
            setCancelButton("확인"){
                dialogBinding.methodRadio.checkedRadioButtonId?.let {
                    loginMethodModify(it)
                }}
            show()
        }
    }

    private fun logout(){
        AppUser.token = null
        AppUser.user = null
        AppUser.percent = null

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




    override fun dialogEditDone(etString: String, settingType: SettingType, dialog : Sweetalert) {
        dialog.dismissWithAnimation()
        return ;

        when(settingType){
            SettingType.MODIFY_NICKNAME-> TODO("닉네임 변경")
            SettingType.MODIFY_PASSWORD -> TODO("비밀번호 변경")
            SettingType.WITHDRAWAL -> TODO("회원탈퇴")
            else -> error("타입 오류")
        }
    }

    override fun loginMethodModify(radioCheckedId: Int?) {
        Log.d(TAG, radioCheckedId.toString())
        return ;

        radioCheckedId?.let { id->
            deleteServerToken()
            when(id){
                R.id.login_biometric -> showBiometricPromptForEncryption() ;
                R.id.login_pattern-> TODO("패턴인증") ;
                R.id.login_password-> TODO("로그인") ;
                else -> error("잘못된 인증 방식 선택")
            }
        }
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
}


interface DialogListener{
    fun dialogEditDone(etString : String, settingType: ProfileFragment.SettingType, dialog: Sweetalert)
    fun loginMethodModify(radioCheckedId : Int?)
}