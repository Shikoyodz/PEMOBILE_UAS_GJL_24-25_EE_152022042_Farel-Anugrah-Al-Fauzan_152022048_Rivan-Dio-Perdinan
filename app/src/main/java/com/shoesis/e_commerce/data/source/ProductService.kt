package com.shoesis.e_commerce.data.source

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.shoesis.e_commerce.data.dto.ProductDto

class ProductService {

    private val firestore = FirebaseFirestore.getInstance()

    // Mengambil semua produk dari koleksi "products"
    fun getAllProducts(callback: (List<ProductDto>?, Int?) -> Unit) {
        firestore.collection("products")
            .get()
            .addOnSuccessListener { result ->
                val productList = mutableListOf<ProductDto>()
                for (document in result) {
                    val product = document.toObject(ProductDto::class.java)
                    productList.add(product)
                }
                callback(productList, null)  // Success, no error code
            }
            .addOnFailureListener { exception ->
                callback(null, 1)  // Error, status code 1 for failure
            }
    }


    // Get products by category from Firestore (e.g., "woman", "man", etc.)
    fun getProductsByCategory(category: String, callback: (List<ProductDto>?) -> Unit) {
        firestore.collection("products")
            .whereEqualTo("category", category)  // Query products by category
            .get()
            .addOnSuccessListener { result ->
                val productList = mutableListOf<ProductDto>()
                for (document in result.documents) {
                    val product = document.toObject(ProductDto::class.java)
                    product?.let { productList.add(it) }  // Avoid adding null products
                }
                callback(productList)  // Return the list of products by category
            }
            .addOnFailureListener { exception ->
                Log.e("ProductService", "Error fetching products for category $category: ${exception.message}")
                callback(null)  // If there's an error, return null
            }
    }

    // Search products by name from Firestore
    fun searchProductsByName(productName: String, callback: (List<ProductDto>?) -> Unit) {
        firestore.collection("products")
            .whereEqualTo("productName", productName)  // Search for products by name
            .get()
            .addOnSuccessListener { result ->
                val productList = mutableListOf<ProductDto>()
                for (document in result) {
                    val product = document.toObject(ProductDto::class.java)
                    productList.add(product)
                }
                callback(productList)  // Return the list of matching products
            }
            .addOnFailureListener {
                callback(null)  // If there's an error, return null
            }
    }

    // Menambahkan produk baru ke Firestore dengan ID khusus
    fun addProduct(productDto: ProductDto, callback: (Boolean, String?) -> Unit) {
        firestore.collection("products")
            .add(productDto)  // Menambahkan produk baru
            .addOnSuccessListener { documentReference ->
                // Jika produk berhasil ditambahkan, kita bisa mengambil ID dokumen yang dihasilkan oleh Firestore
                val productId = documentReference.id

                // Mengonversi ID Firestore yang berupa String menjadi Int
                val productIdInt = productId.toIntOrNull() ?: System.currentTimeMillis().toInt()  // Jika gagal konversi, gunakan timestamp

                // Menambahkan ID produk ke objek ProductDto
                productDto.id = productIdInt  // Pastikan ProductDto memiliki properti `id` bertipe Int

                callback(true, null)  // Berhasil menambah produk
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message)  // Gagal menambah produk dengan pesan kesalahan
            }
    }


    // Menambahkan produk baru ke Firestore dengan ID yang sudah ditentukan (misal menggunakan ID produk yang dibuat secara manual)
    fun addProductWithId(productDto: ProductDto, callback: (Boolean, String?) -> Unit) {
        firestore.collection("products")
            .document(productDto.id.toString())  // Menyimpan produk dengan ID yang ditentukan
            .set(productDto)  // Menyimpan produk
            .addOnSuccessListener {
                callback(true, null)  // Berhasil menambah produk
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message)  // Gagal menambah produk dengan pesan kesalahan
            }
    }

    // Update product stock in Firestore
    fun updateProductStock(productId: Int, newStock: Int, callback: (Boolean) -> Unit) {
        val productRef = firestore.collection("products").document(
            productId.toString()
        )
        productRef.update("stock", newStock)  // Update the stock of the product
            .addOnSuccessListener {
                callback(true)  // Stock updated successfully
            }
            .addOnFailureListener {
                callback(false)  // Failed to update stock
            }
    }

    // Delete a product from Firestore
    fun deleteProduct(productId: String, callback: (Boolean) -> Unit) {
        firestore.collection("products")
            .document(productId)  // Get the product document by ID
            .delete()  // Delete the product from Firestore
            .addOnSuccessListener {
                callback(true)  // Product deleted successfully
            }
            .addOnFailureListener {
                callback(false)  // Failed to delete product
            }
    }
}
