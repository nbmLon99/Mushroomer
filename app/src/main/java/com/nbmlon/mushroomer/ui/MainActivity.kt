package com.nbmlon.mushroomer.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.ActivityMainBinding
import com.nbmlon.mushroomer.ui.camera.CameraFragment
import com.nbmlon.mushroomer.ui.commu.CommuFragment_home
import com.nbmlon.mushroomer.ui.dogam.DogamFragment
import com.nbmlon.mushroomer.ui.map.MapFragment
import com.nbmlon.mushroomer.ui.profile.ProfileFragment
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding // 뷰 바인딩 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // 뷰 바인딩 초기화
        setContentView(binding.root) // setContentView에 뷰 바인딩의 루트 요소를 전달

//         뷰 바인딩을 통해 바텀 네비게이션 뷰 참조
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.selectedItemId = R.id.camera

//        try {
//            val information =
//                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
//            val signatures = information.signingInfo.apkContentsSigners
//            val md = MessageDigest.getInstance("SHA")
//            for (signature in signatures) {
//                val md: MessageDigest
//                md = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                var hashcode = String(Base64.encode(md.digest(), 0))
//                Log.d("hashcode", "" + hashcode)
//            }
//        } catch (e: Exception) {
//            Log.d("hashcode", "에러::" + e.toString())
//
//        }

        bottomNavigationView.setOnItemSelectedListener  { menuItem ->
            when (menuItem.itemId) {
                R.id.dogam -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.FragmentContainer, DogamFragment())
                        .commit()
                    true
                }
                R.id.map -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.FragmentContainer, MapFragment())
                        .commit()
                    true
                }
                R.id.camera -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.FragmentContainer, CameraFragment())
                        .commit()
                    true
                }

                R.id.community -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.FragmentContainer, CommuFragment_home())
                        .commit()
                    true
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.FragmentContainer, ProfileFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }

        // 초기 프래그먼트를 설정
        supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, CameraFragment())
            .commit()
    }
}
