package com.nbmlon.mushroomer.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
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
import com.nbmlon.mushroomer.api.RequestCodeConstants
import com.nbmlon.mushroomer.api.RequestCodeConstants.CAMERA_PERMISSION_REQUEST_CODE
import com.nbmlon.mushroomer.databinding.FragmentCameraBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import taimoor.sultani.sweetalert2.Sweetalert
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment(), ImageDeleteListner, AnalyzeStartListener {
    companion object {
        const val TAG = "CameraFragment"
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
            ).toTypedArray()
    }
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var picturesAdapter: PicturesAdapter
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private val cameraViewModel: CameraViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat : Double? = null; private var lon : Double? = null;
    private var pictureAdded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (!checkLocationPermission()) {
            // 위치 권한을 요청합니다.
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                RequestCodeConstants.LOCATION_PERMISSION_REQUEST_CODE
            )
        }
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

        cameraViewModel.capturedImages.observe(viewLifecycleOwner) { itemList ->
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
                cameraViewModel.capturedImages.value?.let {
                    if(it.size <= 0) { showAlertMessage(R.string.TOAST_pictureMinimum, null) }
                    else if(it.size < resources.getInteger(R.integer.recommended_pic_count)) { showAlertForAdditionalPicture() }
                    else{
                        savePictures()
                        startAnalyze()
                    }
                }
            }
            closeBtn.setOnClickListener{
                cameraViewModel.capturedImages.value?.let{
                    if (it.size > 0){
                        //Positive가 안떠서 negative를 positive로함
                        Sweetalert(requireActivity(),Sweetalert.NORMAL_TYPE)
                            .setTitleText(resources.getString(R.string.ALERT_CANCEL_TITLE))
                            .setContentText(resources.getString(R.string.ALERT_CANCEL_CONTENT))
                            .setCancelButton(resources.getString(R.string.YES)) { dialog ->
                                cameraViewModel.clearImages()
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

    private fun savePictures() {
        CoroutineScope(Dispatchers.Main).launch {
            val success = cameraViewModel.savePictureFromBitmaps(requireActivity())
            if (success) {
                Log.d(TAG, "SUCCESS_TO_SAVE")
            } else {
                showAlertMessage(R.string.TOAST_FAIL_TO_SAVE,null)
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
        if (cameraViewModel.capturedImages.value!!.size < 5) {
            // Get a stable reference of the modifiable image capture use case
            val imageCapture = imageCapture ?: return

            imageCapture.takePicture(
                ContextCompat.getMainExecutor(requireContext()),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        pictureAdded = true
                        cameraViewModel.addPicture(image)
                        image.close()
                        Log.d("CAMERA_TEST",cameraViewModel.capturedImages.value!!.size.toString())
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
        cameraViewModel.delPicture(id)
    }


    private fun showAlertForAdditionalPicture(){
        cameraViewModel.capturedImages.value?.let {
            val dialogFragment = CameraFragment_alert().apply {
                arguments = Bundle().apply {
                    putInt(CameraFragment_alert.ITEM_COUNT, cameraViewModel.capturedImages.value!!.size)
                    putSerializable(CameraFragment_alert.START_ANALYZE_LISTENER, this@CameraFragment as AnalyzeStartListener )
                }
            }
            dialogFragment.show(parentFragmentManager, CameraFragment_alert.TAG)
        }
    }

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


    override fun startAnalyze() {
        // 위치 권한이 이미 허용된 경우 위치 정보 업데이트
        if (checkLocationPermission()) {
            requestLocationUpdates()
        }
        cameraViewModel.startAnalysis()
        val loading = Sweetalert(requireActivity(),Sweetalert.PROGRESS_TYPE).apply {
            titleText = resources.getString(R.string.ANLAYZE_IN_PROGRESS)
            setCancelable(false)
            show()
        }

        cameraViewModel.analysisResult.observe(viewLifecycleOwner){response ->
            loading.dismissWithAnimation()
            CameraFragment_result.getInstance(response = response)
                .show(requireActivity().supportFragmentManager, CameraFragment_result.TAG)
        }
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
}
