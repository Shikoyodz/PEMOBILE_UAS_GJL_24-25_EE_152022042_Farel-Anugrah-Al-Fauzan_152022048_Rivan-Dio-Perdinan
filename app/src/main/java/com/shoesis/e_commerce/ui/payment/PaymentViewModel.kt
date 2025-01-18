package com.shoesis.e_commerce.ui.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.shoesis.e_commerce.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val ioDispatcher: CoroutineContext,
    private val productRepository: ProductRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    fun reduceStock(product_id: Int, newStock: Int) {
        CoroutineScope(ioDispatcher).launch {
            try {
                // Query untuk mencari produk berdasarkan ID produk
                val query = firestore.collection("products").whereEqualTo("id", product_id)
                query.get().addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Log.e("ProductRepository", "Product with ID $product_id not found.")
                    } else {
                        // Hanya ada 1 produk dengan ID tersebut, kita update stoknya
                        for (document in documents) {
                            document.reference.update("stock", newStock)
                                .addOnSuccessListener {
                                    Log.d("ProductRepository", "Stock updated successfully for product $product_id")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("ProductRepository", "Failed to update stock for product $product_id: ${e.message}")
                                }
                        }
                    }
                }.addOnFailureListener { e ->
                    Log.e("ProductRepository", "Error getting documents: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e("PaymentViewModel", "Error in updateStock: ${e.message}")
            }
        }
    }


    fun deleteFromBasket(product_name: String) {
        CoroutineScope(ioDispatcher).launch {
            productRepository.deleteFromBasket(product_name)
        }
    }

    fun addToOrders(product_name: String, hashMap: HashMap<Any, Any>) {
        CoroutineScope(ioDispatcher).launch {
            productRepository.addToOrders(product_name, hashMap)
        }
    }
}