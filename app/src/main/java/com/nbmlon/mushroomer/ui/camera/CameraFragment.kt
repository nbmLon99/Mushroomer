package com.nbmlon.mushroomer.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.lifecycle.Observer
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.FragmentCameraBinding
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
        binding.pictureRV.adapter = picturesAdapter

        cameraViewModel.capturedImages.observe(viewLifecycleOwner) { itemList ->
            picturesAdapter.submitList(itemList)
            binding.pictureRV.adapter = picturesAdapter

            Log.d("CAMERA_TEST", itemList.toString())
        }

        binding.shootBtn.setOnClickListener { takePhoto() }
        binding.startBtn.setOnClickListener {
            if(cameraViewModel.capturedImages.value!!.size < 5) { showAlertMessage() }
            else{ startAnalyze() }
        }
        cameraExecutor = Executors.newSingleThreadExecutor()
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
                        cameraViewModel.saveProxyPicture(image)
                        //image.close()
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


//
//    private fun takePhoto() {
//        if(picturesAdapter.itemCount < 5){
//            // Get a stable reference of the modifiable image capture use case
//            val imageCapture =  imageCapture ?: return
//
//            // Create time stamped name and MediaStore entry.
//            val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
//                .format(System.currentTimeMillis())
//            val contentValues = ContentValues().apply {
//                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
//                }
//            }
//
//            // Create output options object which contains file + metadata
//            val outputOptions = ImageCapture.OutputFileOptions
//                .Builder(
//                    requireActivity().contentResolver,
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                    contentValues)
//                .build()
//
//            // Set up image capture listener, which is triggered after photo has
//            // been taken
//            imageCapture.takePicture(
//                outputOptions,
//                ContextCompat.getMainExecutor(requireContext()),
//                object : ImageCapture.OnImageSavedCallback {
//                    override fun onError(exc: ImageCaptureException) {
//                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
//                    }
//
//                    override fun onImageSaved(output: ImageCapture.OutputFileResults){
//                        val msg = "Photo capture succeeded: ${output.savedUri}"
//                        Log.d(TAG, msg)
//                        picturesAdapter.addPicture(output.savedUri!!)
//                        binding.pictureRV.smoothScrollToPosition(0)
//                    }
//                }
//            )
//        }else{
//            Toast.makeText(requireActivity(),resources.getText(R.string.TOAST_pictureMaximum),Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }



    override fun deleteImage(idx : Int) {
        cameraViewModel.delProxyPicture(idx)
    }


    private fun showAlertMessage(){
        val dialogFragment = CameraFragment_alert().apply {
            arguments = Bundle().apply {
                putInt(CameraFragment_alert.ITEM_COUNT, picturesAdapter.itemCount)
            }
        }
        dialogFragment.show(parentFragmentManager, CameraFragment_alert.TAG)
    }


    override fun startAnalyze() {
        cameraViewModel.startAnalysis()
    }
}