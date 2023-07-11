import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mushroomer.R
import com.example.mushroomer.databinding.ActivityMainBinding
import com.example.mushroomer.ui.community.CommunityFragment
import com.example.mushroomer.ui.dogam.DogamFragment
import com.example.mushroomer.ui.map.MapFragment
import com.example.mushroomer.ui.profile.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding // 뷰 바인딩 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // 뷰 바인딩 초기화
        setContentView(binding.root) // setContentView에 뷰 바인딩의 루트 요소를 전달

        // 뷰 바인딩을 통해 바텀 네비게이션 뷰 참조
        val bottomNavigationView = binding.bottomNavigationView

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
                        .replace(R.id.FragmentContainer, CommunityFragment())
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
