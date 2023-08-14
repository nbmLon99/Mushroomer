package com.nbmlon.mushroomer.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
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
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.FragmentCameraBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraFragment : Fragment(), ImageListner, AnalyzeStartListener {
    companion object {
        private const val TAG = "CameraFragment"
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                }
            }.toTypedArray()
    }
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var picturesAdapter: PicturesAdapter
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null


    private val cameraViewModel: CameraViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        picturesAdapter = PicturesAdapter(this@CameraFragment as ImageListner)
        cameraExecutor = Executors.newSingleThreadExecutor()

        cameraViewModel.capturedImages.observe(viewLifecycleOwner) { itemList ->
            picturesAdapter.submitList(itemList.toList())
            binding.pictureRV.smoothScrollToPosition(0)
        }

        binding.apply {
            pictureRV.adapter = picturesAdapter
            shootBtn.setOnClickListener { takePhoto() }
            startBtn.setOnClickListener {
                cameraViewModel.capturedImages.value?.let {
                    if(it.size <= 0) { showMinimumToast() }
                    if(it.size < resources.getInteger(R.integer.recommended_pic_count)) { showAlertMessage() }
                    else{
                        savePictures()
                        startAnalyze()
                    }
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
                Toast.makeText(context,resources.getText(R.string.TOAST_FAIL_TO_SAVE),Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireActivity(), resources.getText(R.string.TOAST_pictureMaximum), Toast.LENGTH_SHORT).show()
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }



    override fun deleteImage(idx : Int) {
        cameraViewModel.delPicture(idx)
    }


    private fun showAlertMessage(){
        cameraViewModel.capturedImages.value?.let {
            val dialogFragment = CameraFragment_alert().apply {
                arguments = Bundle().apply {
                    putInt(CameraFragment_alert.ITEM_COUNT, cameraViewModel.capturedImages.value!!.size)
                }
            }
            dialogFragment.show(parentFragmentManager, CameraFragment_alert.TAG)
        }
    }

    private fun showMinimumToast(){
        Toast.makeText(requireActivity(),getString(R.string.TOAST_pictureMinimum),Toast.LENGTH_SHORT).show()
    }


    override fun startAnalyze() {
        cameraViewModel.startAnalysis()
    }
}
