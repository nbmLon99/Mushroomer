package com.nbmlon.mushroomer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.ActivityMainBinding
import com.nbmlon.mushroomer.model.MushHistory
import com.nbmlon.mushroomer.ui.camera.CameraFragment
import com.nbmlon.mushroomer.ui.commu.BoardType
import com.nbmlon.mushroomer.ui.commu.CommuFragmentBoard_img
import com.nbmlon.mushroomer.ui.commu.CommuFragment_home
import com.nbmlon.mushroomer.ui.commu.CommuFragment_write
import com.nbmlon.mushroomer.ui.dogam.DogamFragment
import com.nbmlon.mushroomer.ui.dogam.goPicBoardBtnClickListener
import com.nbmlon.mushroomer.ui.map.MapFragment
import com.nbmlon.mushroomer.ui.profile.ProfileFragment


class MainActivity : AppCompatActivity(), goPicBoardBtnClickListener {
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

    override fun goPicBoardBtnClicked(mushHistory: MushHistory) {
        binding.bottomNavigationView.selectedItemId = R.id.community
        val picBoard_idx = BoardType.PicBoard.ordinal

        supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, CommuFragmentBoard_img.getInstance(picBoard_idx), CommuFragmentBoard_img.TAG)
            .addToBackStack(null)
            .commit()

        supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, CommuFragment_write.getInstance(picBoard_idx, mushHistory = mushHistory), CommuFragment_write.TAG)
            .addToBackStack(null)
            .commit()
    }
}
