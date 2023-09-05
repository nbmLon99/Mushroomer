package com.nbmlon.mushroomer.ui.map

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.R
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.nbmlon.mushroomer.databinding.FragmentMapBinding
import com.nbmlon.mushroomer.model.MushHistory
import com.nbmlon.mushroomer.ui.dogam.PictureDialogFragment
import com.nbmlon.mushroomer.ui.dogam.PictureDialogFrom


/**
 * 지도 프래그먼트
 */
class MapFragment() : Fragment() {
    companion object {
        const val TAG = "MapFragment"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001

        @JvmStatic
        fun getFocusedFor(mushHistory: MushHistory) =
            MapFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    // TODO: Rename and change types of parameters
    private var _binding : FragmentMapBinding? = null
    private val binding get() = _binding!!
    var target_history : MushHistory? = null
    private var kakaoMap : KakaoMap? = null

    // 라벨과 연관된 mushHistory를 저장하는 맵
    val labelToMushHistoryMap = mutableMapOf<Label, MushHistory>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            target_history = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getSerializable(MUSH_HISTORY_FOR_FOCUS, MushHistory::class.java)
            } else {
                it.getSerializable(MUSH_HISTORY_FOR_FOCUS) as MushHistory
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (!checkLocationPermission()) {
            // 위치 권한을 요청합니다.
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(layoutInflater)
        val viewModel by viewModels<MapViewModel>()

        binding.mapView.start( object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                // 인증 후 API가 정상적으로 실행될 때 호출됨
                this@MapFragment.kakaoMap = kakaoMap
                viewModel.markers.observe(viewLifecycleOwner){histories ->
                    histories.forEach{
                        binding.setMarker(it)
                    }
                }
                kakaoMap.setOnLabelClickListener { _, _, label ->
                    labelToMushHistoryMap[label]?.let {
                        PictureDialogFragment.getInstance(target= it,callFrom= PictureDialogFrom.DogamFrag)
                            .show(requireActivity().supportFragmentManager,PictureDialogFragment.TAG)
                    }
                }
            }

            override fun getPosition(): LatLng {
                target_history?.let {
                    return LatLng.from(it.lat, it.lon);
                }

                if (checkLocationPermission()) {
                    // 위치 권한이 이미 허용된 경우 위치 정보를 요청할 수 있습니다.
                    requestLocationUpdates()
                }
                return super.getPosition()
            }

            override fun getZoomLevel(): Int {
                return 12
            }
        })




        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnTest.setOnClickListener { testPictureDialog() }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun testPictureDialog(){
        PictureDialogFragment.getInstance(MushHistory.getDummy(),PictureDialogFrom.MapFrag)
            .show(requireActivity().supportFragmentManager, PictureDialogFragment.TAG)
    }

    private fun FragmentMapBinding.setMarker(history : MushHistory){
        // 1. LabelStyles 생성하기 - Icon 이미지 하나만 있는 스타일
        val styles = kakaoMap?.labelManager!!
            .addLabelStyles(
                LabelStyles.from(
                    LabelStyle.from(R.drawable.ic_dialog_map)
                        .setAnchorPoint(0.5f,0.5f)
                )
            )

        // 2. LabelOptions 생성하기
        val options = LabelOptions.from(LatLng.from(history.lat, history.lon))
            .setStyles(styles)
            .setTexts(history.mushroom.name)
            .setClickable(true)



        // 3. LabelLayer 가져오기 (또는 커스텀 Layer 생성)
        val layer = kakaoMap?.labelManager!!.layer


        // 4. LabelLayer 에 LabelOptions 을 넣어 Label 생성하기
        val label = layer.addLabel(options)
        labelToMushHistoryMap[label] = history
    }


    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates()  {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    // 여기에서 위도와 경도를 사용하세요.
                    //카메라 중심점 이동
                    kakaoMap?.moveCamera( CameraUpdateFactory.newCenterPosition(LatLng.from(latitude,longitude)))
                }
            }
            .addOnFailureListener {
                // 위치 정보를 가져오는 데 실패한 경우 처리하세요.
                Toast.makeText(requireActivity(),"위치 정보를 가져오는 데 실패했습니다.",Toast.LENGTH_SHORT).show()
            }
    }
}

private const val MUSH_HISTORY_FOR_FOCUS = "center_marker_history"
