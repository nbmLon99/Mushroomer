package com.nbmlon.mushroomer.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.ActivityMainBinding
import com.nbmlon.mushroomer.ui.camera.CameraFragment
import com.nbmlon.mushroomer.ui.commu.CommuFragment_home
import com.nbmlon.mushroomer.ui.commu.CommuFragment_search
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

        bottomNavigationView.setOnItemSelectedListener  { menuItem ->
            clearBackStack()
            when (menuItem.itemId) {
                R.id.dogam -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.FragmentContainer, DogamFragment(), DogamFragment.TAG)
                        .commit()
                    true
                }
                R.id.map -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.FragmentContainer, MapFragment(), MapFragment.TAG)
                        .commit()
                    true
                }
                R.id.camera -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.FragmentContainer, CameraFragment(), CameraFragment.TAG)
                        .commit()
                    true
                }

                R.id.community -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.FragmentContainer, CommuFragment_home(), CommuFragment_home.TAG)
                        .commit()
                    true
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.FragmentContainer, ProfileFragment(), ProfileFragment.TAG)
                        .commit()
                    true
                }
                else -> false
            }

        }

        // 초기 프래그먼트를 설정
        supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, CameraFragment(),CameraFragment.TAG)
            .commit()
    }
    private fun clearBackStack(){
        val fragmentManager = supportFragmentManager
        for (i in 0 until fragmentManager.backStackEntryCount) {
            val fragment = fragmentManager.findFragmentByTag(fragmentManager.getBackStackEntryAt(i).name)
            fragment?.let {
                val transaction = fragmentManager.beginTransaction()
                transaction.remove(it)
                transaction.commitNow() // 또는 commit()
            }
        }
    }
}
