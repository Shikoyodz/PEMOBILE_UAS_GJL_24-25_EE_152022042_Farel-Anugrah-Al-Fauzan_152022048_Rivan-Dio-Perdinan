package com.shoesis.e_commerce.domain.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.shoesis.e_commerce.data.dto.ProductDto
import com.shoesis.e_commerce.data.source.ProductService
import com.shoesis.e_commerce.domain.model.data.Address
import com.shoesis.e_commerce.domain.model.data.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val firestoreProductService: ProductService,
    @ApplicationContext val appContext: Context
) {

    suspend fun addToFavorites(productDto: ProductDto, hashMap: HashMap<Any, Any>) =
        firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.email.toString())
            .collection("favorites").document(productDto.productName).set(hashMap).await()

    suspend fun deleteFromFavorites(productDto: ProductDto) =
        firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.email.toString())
            .collection("favorites").document(productDto.productName).delete().await()

    suspend fun updateUser(hashMap: HashMap<String, Any>) =
        firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.email.toString())
            .update(hashMap).await()

    // Fetch products from different categories

    fun getWorkshoes(list: MutableLiveData<List<ProductDto>>) {
        firestoreProductService.getProductsByCategory("workshoes") { products ->
            products?.let {
                list.value = it
            }
        }
    }

    fun getSandals(list: MutableLiveData<List<ProductDto>>) {
        firestoreProductService.getProductsByCategory("sandals") { products ->
            products?.let {
                list.value = it
            }
        }
    }

    fun getBoots(list: MutableLiveData<List<ProductDto>>) {
        firestoreProductService.getProductsByCategory("boots") { products ->
            products?.let {
                list.value = it
            }
        }
    }

    fun getSneakers(list: MutableLiveData<List<ProductDto>>) {
        firestoreProductService.getProductsByCategory("sneakers") { products ->
            products?.let {
                list.value = it
            }
        }
    }

    fun getSportshoes(list: MutableLiveData<List<ProductDto>>) {
        firestoreProductService.getProductsByCategory("sportshoes") { products ->
            products?.let {
                list.value = it
            }
        }
    }

    fun getNewest(list: MutableLiveData<List<ProductDto>>) {
        firestoreProductService.getAllProducts { products, statusCode ->
            products?.let {
                list.value = it  // Set product list if successful
            }
            statusCode?.let {
                // Handle error (statusCode is an Int)
                if (it == 1) {
                    Log.e("Error", "Failed to load products.")
                }
            }
        }
    }


    fun getFavorites(
        list: MutableLiveData<List<ProductDto>>
    ) = firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.email.toString())
        .collection("favorites").addSnapshotListener { querySnapshot, firestoreException ->
            firestoreException?.let {
                Toast.makeText(appContext, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            querySnapshot?.let {
                val favoritesList: ArrayList<ProductDto> = ArrayList()
                for (document in it) {
                    val favorites = document.toObject<ProductDto>()
                    favoritesList.add(favorites)
                    list.postValue(favoritesList)
                }
            }
        }

    fun getUser(_user: MutableLiveData<User>) =
        firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.email.toString())
            .get().addOnSuccessListener {
                if (it.exists()) {
                    val user = it.toObject<User>()!!
                    _user.postValue(user)
                } else {
                    Log.e("Get User Error: ", "User doesn't exist")
                }
            }.addOnFailureListener {
                Log.e("Get User Exception: ", it.message.orEmpty())
            }

    fun signOut() {
        firebaseAuth.signOut()
    }

    // Search for products by name
    fun searchProducts(productName: String, list: MutableLiveData<List<ProductDto>>) {
        firestoreProductService.searchProductsByName(productName) { products ->
            products?.let {
                list.postValue(it)
            }
        }
    }

    // Update stock for a product
    fun updateStock(product_id: Int, product_stock: Int) {
        firestoreProductService.updateProductStock(product_id, product_stock) { success ->
            if (success) {
                Log.e("Update Stock Success", "Product stock updated")
            } else {
                Log.e("Update Stock Failure", "Failed to update stock")
            }
        }
    }

    suspend fun addToBasket(productDto: ProductDto, hashMap: HashMap<Any, Any>) =
        firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.email.toString())
            .collection("basket").document(productDto.productName).set(hashMap).await()

    fun getBasket(list: MutableLiveData<List<ProductDto>>) =
        firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.email.toString())
            .collection("basket").addSnapshotListener { querySnapshot, firestoreException ->
                firestoreException?.let {
                    Toast.makeText(appContext, it.message, Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                querySnapshot?.let {
                    val basketList: ArrayList<ProductDto> = ArrayList()
                    for (document in it) {
                        val basket = document.toObject<ProductDto>()
                        basketList.add(basket)
                        list.postValue(basketList)
                    }
                }
            }

    suspend fun updateBasketItem(productDto: ProductDto, data: Any, path: String) =
        firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.email.toString())
            .collection("basket").document(productDto.productName).update(
                hashMapOf<String, Any>(
                    path to data
                )
            ).await()

    suspend fun deleteFromBasket(product_name: String) =
        firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.email.toString())
            .collection("basket").document(product_name).delete().await()

    suspend fun addToOrders(product_name: String, hashMap: HashMap<Any, Any>) =
        firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.email.toString())
            .collection("orders").document(product_name).set(hashMap).await()

    fun getOrders(orderList: MutableLiveData<List<ProductDto>>) =
        firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.email.toString())
            .collection("orders").addSnapshotListener { querySnapshot, firestoreException ->
                firestoreException?.let {
                    Toast.makeText(appContext, it.message, Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                querySnapshot?.let {
                    val _orderList: ArrayList<ProductDto> = ArrayList()
                    for (document in it) {
                        val order = document.toObject<ProductDto>()
                        _orderList.add(order)
                        orderList.postValue(_orderList)
                    }
                }
            }

    suspend fun addAddress(address: HashMap<Any, Any>, addressName: String) =
        firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.email.toString())
            .collection("address").document(addressName).set(address).await()

    suspend fun deleteAddress(addressName: String) =
        firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.email.toString())
            .collection("address").document(addressName).delete().await()

    fun getAllAddress(addressList: MutableLiveData<List<Address>>) =
        firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.email.toString())
            .collection("address").addSnapshotListener { querySnapshot, firestoreException ->
                firestoreException?.let {
                    Toast.makeText(appContext, it.message, Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                querySnapshot?.let {
                    val _addressList: ArrayList<Address> = ArrayList()
                    for (document in it) {
                        val address = document.toObject<Address>()
                        _addressList.add(address)
                        addressList.postValue(_addressList)
                    }
                }
            }

    suspend fun updateAddress(addressName: String, path: String, data: Any) =
        firebaseFirestore.collection("users").document(firebaseAuth.currentUser?.email.toString())
            .collection("address").document(addressName).update(
                hashMapOf(
                    path to data
                ) as Map<String, Any>
            ).await()
}
