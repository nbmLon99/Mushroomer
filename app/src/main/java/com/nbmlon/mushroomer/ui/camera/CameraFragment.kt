package com.nbmlon.mushroomer.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.api.EndConverter
import com.nbmlon.mushroomer.api.RequestCodeConstants
import com.nbmlon.mushroomer.api.RequestCodeConstants.CAMERA_PERMISSION_REQUEST_CODE
import com.nbmlon.mushroomer.api.ResponseCodeConstants.BITMAP_SAVE_ERROR
import com.nbmlon.mushroomer.api.ResponseCodeConstants.LOW_ACCURACY_ERROR
import com.nbmlon.mushroomer.api.ResponseCodeConstants.NETWORK_ERROR_CODE
import com.nbmlon.mushroomer.data.analyze.TFModule
import com.nbmlon.mushroomer.databinding.FragmentCameraBinding
import com.nbmlon.mushroomer.domain.AnalyzeUseCaseRequest
import com.nbmlon.mushroomer.domain.AnalyzeUseCaseResponse
import com.nbmlon.mushroomer.domain.toResultModel
import com.nbmlon.mushroomer.model.AnalyzeResult
import taimoor.sultani.sweetalert2.Sweetalert
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment(), ImageDeleteListner, AnalyzeStartListener {
    companion object {
        const val TAG = "CameraFragment"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
            ).toTypedArray()
    }
    private val viewModel: CameraViewModel by viewModels()

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var picturesAdapter: PicturesAdapter
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var loading : Sweetalert
    private var imageCapture: ImageCapture? = null
    private var lat : Double? = null; private var lon : Double? = null;
    private var pictureAdded = false
    private var analyzeResult : AnalyzeResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (!checkLocationPermission()) {
            // 위치 권한을 요청합니다.
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                RequestCodeConstants.LOCATION_PERMISSION_REQUEST_CODE
            )
        }
        loading = Sweetalert(requireActivity(),Sweetalert.PROGRESS_TYPE).apply {
            titleText = resources.getString(R.string.ANLAYZE_IN_PROGRESS)
            setCancelable(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        viewModel.response.observe(viewLifecycleOwner, ::responseObserver)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        picturesAdapter = PicturesAdapter(this@CameraFragment as ImageDeleteListner)
        cameraExecutor = Executors.newSingleThreadExecutor()

        viewModel.capturedImages.observe(viewLifecycleOwner) { itemList ->
            picturesAdapter.submitList(itemList.toList())
            if(pictureAdded){
                binding.pictureRV.smoothScrollToPosition(0)
                pictureAdded = false
            }
        }

        binding.apply {
            pictureRV.adapter = picturesAdapter
            shootBtn.setOnClickListener { takePhoto() }
            startBtn.setOnClickListener {
                viewModel.capturedImages.value?.let {
                    if(it.size <= 0) { showAlertMessage(R.string.TOAST_pictureMinimum, null) }
                    else if(it.size < resources.getInteger(R.integer.recommended_pic_count)) { showAlertForAdditionalPicture() }
                    else{
                        startAnalyze()
                    }
                }
            }
            closeBtn.setOnClickListener{
                viewModel.capturedImages.value?.let{
                    if (it.size > 0){
                        //Positive가 안떠서 negative를 positive로함
                        Sweetalert(requireActivity(),Sweetalert.NORMAL_TYPE)
                            .setTitleText(resources.getString(R.string.ALERT_CANCEL_TITLE))
                            .setContentText(resources.getString(R.string.ALERT_CANCEL_CONTENT))
                            .setCancelButton(resources.getString(R.string.YES)) { dialog ->
                                viewModel.clearImages()
                                dialog.dismissWithAnimation()
                            }
                            .setNeutralButton(resources.getString(R.string.NO)) { dialog ->
                                dialog.dismissWithAnimation()
                            }
                            .show()
                    }
                    else{
                        Sweetalert(requireActivity(),Sweetalert.NORMAL_TYPE)
                            .setTitleText(resources.getString(R.string.TOAST_CANCEL_IMPOSSIBLE))
                            .setContentText(resources.getString(R.string.TOAST_CANCEL_IMPOSSIBLE_SUB))
                            .setNeutralButton(resources.getString(R.string.CONFIRM)) { dialog ->
                                dialog.dismissWithAnimation()
                            }
                            .show()}
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onPause() {
        super.onPause()
        cameraExecutor.shutdown()
    }

    private fun startCamera() {
        //ProcessCameraProvider 인스턴스를 만듭니다. 이는 카메라의 수명 주기를 수명 주기 소유자와 바인딩하는 데 사용합니다. CameraX가 수명 주기를 인식하므로 카메라를 열고 닫는 작업이 필요하지 않게 됩니다.
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }



    private fun takePhoto() {
        if (viewModel.capturedImages.value!!.size < 5) {
            // Get a stable reference of the modifiable image capture use case
            val imageCapture = imageCapture ?: return

            imageCapture.takePicture(
                ContextCompat.getMainExecutor(requireContext()),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        pictureAdded = true
                        viewModel.addPicture(image)
                        image.close()
                        Log.d("CAMERA_TEST",viewModel.capturedImages.value!!.size.toString())
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e(CameraFragment.TAG, "Photo capture failed: ${exception.message}", exception)
                    }
                }
            )
        } else {
            showAlertMessage(R.string.TOAST_pictureMaximum,null)
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }



    override fun deleteImage(id : Int) {
        viewModel.delPicture(id)
    }







    /** 검사 요청 **/
    override fun startAnalyze() {
        // 위치 권한이 이미 허용된 경우 위치 정보 업데이트
        if (checkLocationPermission()) {
            requestLocationUpdates()
        }
        viewModel.startAnalysis(
            AnalyzeUseCaseRequest.AnalyzeRequestDomain(
                viewModel.capturedImages.value!!,
                TFModule().getModel(requireContext())
            )
        )
        loading.show()
    }



    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates()  {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    this@CameraFragment.lat = location.latitude
                    this@CameraFragment.lon = location.longitude
                    // 여기에서 위도와 경도를 사용하세요.
                    //카메라 중심점 이동
                }
            }
            .addOnFailureListener {
                // 위치 정보를 가져오는 데 실패한 경우 처리하세요.
                Toast.makeText(requireActivity(),"위치 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }


    /** 요청 결과값 처리 **/
    private fun responseObserver(response : AnalyzeUseCaseResponse){
        when(response){
            is AnalyzeUseCaseResponse.AnalyzeResponseDomain ->{
                //분석 성공
                if(response.success){
                    analyzeResult = response.toResultModel()
                    viewModel.onSuccessAnalyze(requireActivity(), analyzeResult!!.mushroom, lat,lon)
                }else{
                    if(loading.isShowing)
                        loading.dismissWithAnimation()
                    Toast.makeText(requireActivity(), "알 수 없는 이유로 요청에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            //분석 마무리
            is AnalyzeUseCaseResponse.SuccessResponseDomain ->{
                if(loading.isShowing)
                    loading.dismissWithAnimation()
                if(response.success){
                    analyzeResult?.let {
                        CameraFragment_result.getInstance(response = it)
                            .show(requireActivity().supportFragmentManager, CameraFragment_result.TAG)
                    }
                    analyzeResult = null
                }else if(response.code == LOW_ACCURACY_ERROR){
                    Toast.makeText(requireActivity(), "정확도가 기준에 미치지 않습니다. 다른 각도로 촬영을 시도해보세요!", Toast.LENGTH_SHORT).show()
                }else if(response.code == NETWORK_ERROR_CODE){
                    Toast.makeText(requireActivity(), "네트워크 연결을 확인하세요!", Toast.LENGTH_SHORT).show()
                }else if(response.code == BITMAP_SAVE_ERROR){
                    Toast.makeText(requireActivity(), "사진 저장에 실패했습니다! 권한을 확인하세요", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireActivity(), "요청이 실패하였습니다. 잠시 후 다시 시도하세요!", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }


    /**사진 0개 혹은 5개 이상시, 메시지 표시 **/
    private fun showAlertMessage(titleId : Int, subid : Int?){
        Sweetalert(requireActivity(),Sweetalert.NORMAL_TYPE).apply {
            titleText = resources.getString(titleId)
            subid?.let { contentText = resources.getString(it) }
            setNeutralButton(resources.getString(R.string.CONFIRM)) { dialog ->
                dialog.dismissWithAnimation()
            }
            show()
        }
    }


    /** 추가 촬영 권고 (1<=사진<=3) **/
    private fun showAlertForAdditionalPicture(){
        viewModel.capturedImages.value?.let {
            val dialogFragment = CameraFragment_alert.getInstance(viewModel.capturedImages.value!!.size,  this@CameraFragment as AnalyzeStartListener)
            dialogFragment.show(parentFragmentManager, CameraFragment_alert.TAG)
        }
    }
}
