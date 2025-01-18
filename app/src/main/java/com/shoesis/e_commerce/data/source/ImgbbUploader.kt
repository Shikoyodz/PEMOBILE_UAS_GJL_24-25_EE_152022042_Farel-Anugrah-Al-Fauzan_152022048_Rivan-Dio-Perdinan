package com.shoesis.e_commerce.data.source

import android.net.Uri
import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class ImgbbUploader {

    // API key untuk ImgBB
    private val apiKey = "ce3ce9a1ecbba91c190513a093f1b3fd"

    private val client = OkHttpClient()

    /**
     * Fungsi untuk mengupload gambar ke ImgBB dan mengembalikan URL gambar
     * @param imageUri URI gambar yang akan diunggah
     * @param callback Callback untuk menerima URL gambar setelah berhasil diupload
     */
    fun uploadImage(imageUri: Uri, contentResolver: android.content.ContentResolver, callback: (String?) -> Unit) {
        // Mengambil file dari URI
        val file = getFileFromUri(imageUri, contentResolver)

        // Jika file tidak ada atau tidak bisa diakses
        if (file == null || !file.exists()) {
            Log.e("ImgbbUploader", "File does not exist: ${imageUri.path}")
            callback(null)
            return
        }

        val mediaType = "image/jpg".toMediaTypeOrNull()

        // Membuat request body untuk upload
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", file.name, RequestBody.create(mediaType, file))
            .addFormDataPart("key", apiKey)
            .build()

        // Membuat request ke ImgBB
        val request = Request.Builder()
            .url("https://api.imgbb.com/1/upload")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ImgbbUploader", "Upload failed", e)
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val statusCode = response.code
                if (statusCode == 200) {
                    val responseBody = response.body?.string()
                    Log.d("ImgbbUploader", "Response: $responseBody")
                    val imageUrl = parseImageUrl(responseBody)
                    if (imageUrl != null) {
                        callback(imageUrl)
                    } else {
                        Log.e("ImgbbUploader", "Failed to parse image URL")
                        callback(null)
                    }
                } else {
                    Log.e("ImgbbUploader", "Error response from ImgBB: $statusCode, ${response.message}")
                    callback(null)
                }
            }
        })
    }

    /**
     * Mengambil file dari Uri
     * @param uri URI gambar
     * @param contentResolver ContentResolver untuk akses URI
     * @return File yang dihasilkan dari URI, atau null jika tidak ditemukan
     */
    private fun getFileFromUri(uri: Uri, contentResolver: android.content.ContentResolver): File? {
        val file = File.createTempFile("temp_image", ".jpg")

        try {
            // Membuka InputStream dari URI
            val inputStream = contentResolver.openInputStream(uri)
            inputStream?.let { input ->
                // Membuka OutputStream untuk menulis file sementara
                val outputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var length: Int
                // Membaca data dari input stream dan menulis ke output stream
                while (input.read(buffer).also { len -> length = len } != -1) {
                    outputStream.write(buffer, 0, length)
                }
                input.close()  // Menutup input stream
                outputStream.close()  // Menutup output stream
            }
        } catch (e: Exception) {
            Log.e("ImgbbUploader", "Error reading file from Uri", e)
            return null
        }

        return file
    }

    /**
     * Mengambil URL gambar dari response JSON
     * @param responseBody Body response dari ImgBB dalam bentuk string
     * @return URL gambar yang berhasil diunggah
     */
    private fun parseImageUrl(responseBody: String?): String? {
        if (responseBody.isNullOrEmpty()) return null

        try {
            Log.d("ImgbbUploader", "Parsing response: $responseBody")
            val json = JSONObject(responseBody)
            return json.getJSONObject("data").getString("url")
        } catch (e: Exception) {
            Log.e("ImgbbUploader", "Error parsing JSON", e)
        }
        return null
    }
}
