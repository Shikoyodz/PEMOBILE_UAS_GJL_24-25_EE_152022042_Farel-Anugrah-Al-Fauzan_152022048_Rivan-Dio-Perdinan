package com.shoesis.e_commerce.ui.admin

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.adapters.ViewBindingAdapter.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.shoesis.e_commerce.R
import com.shoesis.e_commerce.data.dto.ProductDto
import com.shoesis.e_commerce.data.source.FirestoreRepository
import com.shoesis.e_commerce.data.source.ImgbbUploader
import com.shoesis.e_commerce.databinding.ActivityAdminPanelBinding
import com.shoesis.e_commerce.ui.adapter.ProductAdapter

class AdminPanelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminPanelBinding
    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<ProductDto>()

    private val IMAGE_REQUEST_CODE = 1000
    private var selectedImageUri: Uri? = null

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val categories = listOf("sneakers", "boots", "workshoes", "sportshoes", "sandals") // Static list of categories

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fetch products from Firestore saat Activity dimulai
        fetchProductsFromFirestore()

        // Initialize RecyclerView with ProductAdapter
        binding.productsRecyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(productDtoList = productList, context = this, layoutInflater = layoutInflater)
        binding.productsRecyclerView.adapter = productAdapter

        // Initialize Spinner with category options
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = categoryAdapter


        // CRUD operations for products
        binding.addProductButton.setOnClickListener {
            addProduct()
        }

        binding.updateProductButton.setOnClickListener {
            val productId = binding.productIdEditText.text.toString().toIntOrNull()
            if (productId != null) {
                updateProduct(productId)
            } else {
                Toast.makeText(this, "Invalid product ID", Toast.LENGTH_SHORT).show()
            }
        }

        binding.deleteProductButton.setOnClickListener {
            val productId = binding.productIdEditText.text.toString().toIntOrNull()
            if (productId != null) {
                Log.d("AdminPanel", "Attempting to delete product with ID: $productId")
                deleteProduct(productId)
            } else {
                Toast.makeText(this, "Invalid product ID", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle image selection
        binding.selectImageButton.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            // Display selected image in the ImageView
            binding.imageView.setImageURI(selectedImageUri)
        }
    }

    private fun addProduct() {
        val productName = binding.productNameEditText.text.toString()
        val price = binding.priceEditText.text.toString().toIntOrNull()
        val brand = binding.brandEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val stock = binding.stockEditText.text.toString().toIntOrNull()
        val category = binding.categorySpinner.selectedItem.toString() // Get selected category

        // Ensure all fields are filled and an image is selected
        if (productName.isNotEmpty() && price != null && stock != null && selectedImageUri != null) {
            // If selectedImageUri is not null, proceed with image upload
            val imgbbUploader = ImgbbUploader()
            imgbbUploader.uploadImage(selectedImageUri!!, contentResolver) { imageUrl ->
                if (imageUrl != null) {
                    val newProduct = ProductDto(
                        id = generateProductId(),  // Generate a new unique product ID
                        productName = productName,
                        price = price,
                        brand = brand,
                        picUrl = imageUrl,  // URL of the uploaded image
                        description = description,
                        stock = stock,
                        category = category,  // Add category here
                        piece = 1,
                        rate = 0
                    )

                    // Perform the action (e.g., save the product to Firestore)
                    val firestoreRepository = FirestoreRepository()
                    firestoreRepository.addProduct(newProduct) { success, message ->
                        if (success) {
                            // Product added successfully
                            productList.add(newProduct)
                            productAdapter.notifyDataSetChanged()
                            Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to add product: $message", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Please fill all required fields and select an image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProduct(productId: Int) {
        val productName = binding.productNameEditText.text.toString()
        val price = binding.priceEditText.text.toString().toIntOrNull()
        val brand = binding.brandEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val stock = binding.stockEditText.text.toString().toIntOrNull()
        val category = binding.categorySpinner.selectedItem.toString() // Get selected category

        // Ensure all fields are filled and an image is selected
        if (productName.isNotEmpty() && price != null && stock != null && selectedImageUri != null) {
            // Upload image to ImgBB
            val imgbbUploader = ImgbbUploader()
            imgbbUploader.uploadImage(selectedImageUri!!, contentResolver) { imageUrl ->
                if (imageUrl != null) {
                    val updatedProduct = ProductDto(
                        id = productId,  // Existing product ID
                        productName = productName,
                        price = price,
                        brand = brand,
                        picUrl = imageUrl,  // Use image URL from ImgBB
                        description = description,
                        stock = stock,
                        category = category,  // Update the category
                        piece = 1,
                        rate = 0
                    )

                    // Update product in Firestore with the existing product ID
                    val firestoreRepository = FirestoreRepository()
                    firestoreRepository.updateProduct(updatedProduct) { success, message ->
                        if (success) {
                            // Update product in the list and notify adapter
                            val index = productList.indexOfFirst { it.id == productId }
                            if (index != -1) {
                                productList[index] = updatedProduct
                                productAdapter.notifyDataSetChanged()
                            }
                            Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to update product: $message", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // If image upload failed
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // If fields are missing or image is not selected
            Toast.makeText(this, "Please fill all required fields and select an image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteProduct(productId: Int) {
        firestore.collection("products").document(productId.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    firestore.collection("products").document(productId.toString())
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show()
                            productAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to delete product from Firestore", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Product not found in Firestore", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to find product in Firestore", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateProductId(): Int {
        return (1000..9999).random()
    }

    // Fungsi untuk mengambil data produk dari Firestore
    private fun fetchProductsFromFirestore() {
        firestore.collection("products").get()
            .addOnSuccessListener { result ->
                productList.clear() // Kosongkan daftar produk sebelum menambah data baru
                for (document in result) {
                    val product = document.toObject(ProductDto::class.java)
                    productList.add(product)
                }
                // Setelah data produk diambil, tampilkan di TableLayout
                displayProducts()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching products: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    // Fungsi untuk menampilkan data produk di dalam TableLayout
    private fun displayProducts() {
        binding.productTable.removeAllViews()  // Hapus semua baris yang ada

        // Buat baris pertama (header) dengan judul
        val headerRow = TableRow(this)
        val columns = arrayOf("No", "ID", "Name", "Category", "Stock")

        for (column in columns) {
            val headerTextView = TextView(this).apply {
                text = column
                setTypeface(null, Typeface.BOLD)
                setPadding(8, 8, 8, 8)
                background = ContextCompat.getDrawable(this@AdminPanelActivity, R.drawable.border)
            }
            headerRow.addView(headerTextView)
        }
        binding.productTable.addView(headerRow)

        // Tampilkan data produk
        var index = 1
        for (product in productList) {
            val tableRow = TableRow(this)

            val noTextView = TextView(this).apply {
                text = index.toString()
                setPadding(8, 8, 8, 8)
                background = ContextCompat.getDrawable(this@AdminPanelActivity, R.drawable.border)
            }

            val idTextView = TextView(this).apply {
                text = product.id.toString()
                setPadding(8, 8, 8, 8)
                background = ContextCompat.getDrawable(this@AdminPanelActivity, R.drawable.border)
            }

            val nameTextView = TextView(this).apply {
                text = product.productName
                setPadding(8, 8, 8, 8)
                background = ContextCompat.getDrawable(this@AdminPanelActivity, R.drawable.border)
            }

            val categoryTextView = TextView(this).apply {
                text = product.category
                setPadding(8, 8, 8, 8)
                background = ContextCompat.getDrawable(this@AdminPanelActivity, R.drawable.border)
            }

            val stockTextView = TextView(this).apply {
                text = product.stock.toString()
                setPadding(8, 8, 8, 8)
                background = ContextCompat.getDrawable(this@AdminPanelActivity, R.drawable.border)
            }

            tableRow.addView(noTextView)
            tableRow.addView(idTextView)
            tableRow.addView(nameTextView)
            tableRow.addView(categoryTextView)
            tableRow.addView(stockTextView)

            binding.productTable.addView(tableRow)
            index++ // Increment untuk "No"
        }
    }

}
