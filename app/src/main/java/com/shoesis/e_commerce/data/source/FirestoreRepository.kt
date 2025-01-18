package com.shoesis.e_commerce.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.shoesis.e_commerce.data.dto.ProductDto

class FirestoreRepository {

    private val firestore = FirebaseFirestore.getInstance()

    // Mengambil semua produk dari koleksi "products"
    fun getProducts(callback: (List<ProductDto>?) -> Unit) {
        firestore.collection("products")
            .get()  // Mengambil semua dokumen dari koleksi "products"
            .addOnSuccessListener { result ->
                val productList = mutableListOf<ProductDto>()
                for (document in result) {
                    val product = document.toObject(ProductDto::class.java)
                    productList.add(product)
                }
                callback(productList)  // Mengembalikan daftar produk
            }
            .addOnFailureListener { exception ->
                callback(null)  // Menangani jika terjadi kegagalan
            }
    }

    // Menambahkan produk baru ke Firestore
    fun addProduct(productDto: ProductDto, callback: (Boolean, String?) -> Unit) {
        firestore.collection("products")
            .add(productDto)  // Menambahkan produk baru
            .addOnSuccessListener { documentReference ->
                // Jika berhasil, kirimkan true ke callback
                callback(true, null)
            }
            .addOnFailureListener { exception ->
                // Jika gagal, kirimkan false dan pesan kesalahan
                callback(false, exception.message)
            }
    }

    // Mengambil ID produk terakhir yang ditambahkan untuk keperluan update
    fun getLastAddedProductId(callback: (String?) -> Unit) {
        firestore.collection("products")
            .orderBy("name") // Bisa menggunakan field apa pun yang cocok untuk mengurutkan data
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if (result.documents.isNotEmpty()) {
                    // Mengambil ID dari produk terakhir
                    val lastDocument = result.documents.first()
                    callback(lastDocument.id)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    // Fungsi untuk memperbarui produk yang ada di Firestore
    fun updateProduct(updatedProduct: ProductDto, callback: (Boolean, String?) -> Unit) {
        // Menggunakan ID produk untuk mencari dokumen yang ingin diperbarui
        firestore.collection("products")
            .document(updatedProduct.id.toString()) // Menggunakan ID produk sebagai identifier
            .set(updatedProduct) // Mengupdate produk dengan data baru
            .addOnSuccessListener {
                callback(true, null)  // Jika update berhasil
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message)  // Jika gagal, kirimkan pesan kesalahan
            }
    }
}

