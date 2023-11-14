package com.nbmlon.mushroomer.api

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.nbmlon.mushroomer.AppUser
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.security.MessageDigest

object EndConverter {

    /** Uri To MultiPart **/
    // 내부 Uri를 MultipartBody.Part로 변환하는 함수
    fun uriToPart(context: Context, uri: Uri, index: Int = 0): MultipartBody.Part {
        val inputStream = context.contentResolver.openInputStream(uri)
        val byteArray = inputStream?.readBytes()

        val requestBody: RequestBody? = byteArray?.let {
            RequestBody.create("image/*".toMediaTypeOrNull(), it)
        }

        return requestBody?.let {
            MultipartBody.Part.createFormData("image[$index]", "image_$index.jpg", it)
        } ?: throw IllegalArgumentException("Failed to create RequestBody")
    }

    // List<Uri>을 MultipartBody.Part로 변환하는 함수
    fun urisToParts(context: Context, uris: List<Uri>): List<MultipartBody.Part> {
        val parts = mutableListOf<MultipartBody.Part>()
        uris.forEachIndexed { index, uri ->
            val part = uriToPart(context, uri, index)
            parts.add(part)
        }
        return parts
    }




    /** Bitmap To MultiPart **/
    // name : "icon"
    // filename : "icon_${AppUser.user?.id ?: -1}.jpg"
    fun bitmapToBody(bitmap: Bitmap, name : String, filename : String): MultipartBody.Part {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), byteArrayOutputStream.toByteArray())
        val body = MultipartBody.Part.createFormData(name, filename, requestBody)
        return body
    }

    // List<Bitmap>을 MultipartBody.Part로 변환하는 함수
    fun bitmapsToParts(bitmaps: List<Bitmap>): List<MultipartBody.Part> {
        val parts = mutableListOf<MultipartBody.Part>()
        bitmaps.forEachIndexed { index, bitmap ->
            val part = bitmapToBody(bitmap,"image[$index]","image_$index.jpg")
            parts.add(part)
        }
        return parts
    }




    /** Password Hash **/
    fun sha256(input: String): String {
        val bytes = input.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}

