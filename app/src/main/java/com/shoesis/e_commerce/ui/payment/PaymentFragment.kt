package com.shoesis.e_commerce.ui.payment

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.shoesis.e_commerce.R
import com.shoesis.e_commerce.databinding.FragmentPaymentBinding
import com.shoesis.e_commerce.ui.MainActivity
import com.shoesis.e_commerce.ui.shopping_cart.ShoppingCartViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PaymentViewModel by viewModels()
    private val shoppingCartViewModel: ShoppingCartViewModel by viewModels()

    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val current = LocalDate.now()

    private val CHANNEL_ID = "channelID"
    private val CHANNEL_NAME = "channelName"
    private val NOTIF_ID = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shoppingCartViewModel.basketList.observe(viewLifecycleOwner) {
            var totalPrice = 0
            for (indices in it.indices) {
                totalPrice += it[indices].price * it[indices].piece
            }
            binding.paymentTotalPrice.text = totalPrice.toString()
        }

        binding.btnCompletePayment.setOnClickListener {

            if (binding.btnCompletePayment.text == "Buy Now !!") {

                if (binding.creditCartCardCvv.editText?.text?.length!! == 3 &&
                    binding.creditCartCardYear.editText?.text?.length!! == 2 &&
                    binding.creditCartCardMonth.editText?.text?.length!! == 2 &&
                    binding.creditCartCardNumber.editText?.text?.length!! == 16 &&
                    binding.creditCartCardName.editText?.text.toString().isNotEmpty()
                ) {

                    shoppingCartViewModel.basketList.observe(viewLifecycleOwner) {
                        for (item in it) {
                            val orderHashMap = hashMapOf<Any, Any>(
                                "id" to item.id,
                                "productName" to item.productName,
                                "price" to item.price,
                                "brand" to item.brand,
                                "picUrl" to item.picUrl,
                                "description" to item.description,
                                "order_status" to "Preparing",
                                "order_time" to current.format(formatter).toString(),
                                "piece" to item.piece,
                                "rate" to item.rate,
                                "stock" to item.stock
                            )
                            val liveStock = item.stock - item.piece

                            // Pastikan reduceStock dipanggil untuk mengurangi stok
                            viewModel.reduceStock(item.id, liveStock)

                            // Hapus item dari keranjang
                            viewModel.deleteFromBasket(item.productName)

                            // Tambahkan item ke pesanan
                            viewModel.addToOrders(item.productName, orderHashMap)
                        }
                    }

                    val dialog = LayoutInflater.from(requireContext())
                        .inflate(R.layout.payment_done_dialog, null)
                    val builder = AlertDialog.Builder(context, R.style.MyDialogStyle)
                    builder.setView(dialog)
                    dialog.setBackgroundColor(Color.TRANSPARENT)

                    val alertDialog = builder.create()

                    val anim =
                        dialog.findViewById<LottieAnimationView>(R.id.paymentCompletedAnim)
                    val buttonOk = dialog.findViewById<Button>(R.id.btnPaymentCompleted)


                    createNotificationChannel()

                    val intent = Intent(requireContext(), MainActivity::class.java)
                    val pendingIntent = TaskStackBuilder.create(requireContext()).run {
                        addNextIntentWithParentStack(intent)
                        getPendingIntent(0, PendingIntent.FLAG_MUTABLE)
                    }

                    val notification = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                        .setContentTitle("We have received your order")
                        .setContentText("It will be on its way to be delivered to you as soon as possible")
                        .setSmallIcon(R.mipmap.ic_launcher_shoeses_round)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .build()

                    val notificationManager = NotificationManagerCompat.from(requireContext())

                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return@setOnClickListener
                    }
                    notificationManager.notify(NOTIF_ID, notification)

                    buttonOk.setOnClickListener {
                        findNavController().navigate(R.id.main_graph)
                        alertDialog.dismiss()
                    }

                    anim.playAnimation()

                    alertDialog.show()

                } else {

                    with(binding.creditCartCardName.editText?.text.toString()) {
                        if (this.isEmpty()) {
                            binding.creditCartCardName.error = "Please Enter Your Name"
                        } else {
                            binding.creditCartCardName.error = null
                        }
                    }

                    with(binding.creditCartCardNumber.editText?.text) {
                        if (this?.length!! < 16) {
                            binding.creditCartCardNumber.error = "Please Enter Card Number"
                        } else {
                            binding.creditCartCardNumber.error = null
                        }
                    }

                    with(binding.creditCartCardMonth.editText?.text) {
                        if (this?.length!! < 2 || 13 < Integer.parseInt(this.toString()) || Integer.parseInt(
                                this.toString()
                            ) < 0 || isEmpty()
                        ) {
                            binding.creditCartCardMonth.error = "Please Enter Card Month"
                        } else {
                            binding.creditCartCardMonth.error = null
                        }
                    }

                    with(binding.creditCartCardYear.editText?.text) {
                        if (this?.length!! < 2 || 30 < Integer.parseInt(this.toString()) || Integer.parseInt(
                                this.toString()
                            ) < 23 || isEmpty()
                        ) {
                            binding.creditCartCardYear.error = "Please Enter Card Year"
                        } else {
                            binding.creditCartCardYear.error = null
                        }
                    }

                    with(binding.creditCartCardCvv.editText?.text) {
                        if (this?.length!! < 3 || Integer.parseInt(this.toString()) < 100 || isEmpty()) {
                            binding.creditCartCardCvv.error = "Please Enter Cvv"
                        } else {
                            binding.creditCartCardCvv.error = null
                        }
                    }

                    binding.btnCompletePayment.setText(R.string.confirm)
                    return@setOnClickListener
                }
            }

            if (binding.btnCompletePayment.text == "Confirm") {
                // Validasi kartu kredit yang dimasukkan
                with(binding.creditCartCardName.editText?.text.toString()) {
                    if (this.isEmpty()) {
                        binding.creditCartCardName.error = "Please Enter Your Name"
                    } else {
                        binding.creditCartCardName.error = null
                    }
                }

                with(binding.creditCartCardNumber.editText?.text) {
                    if (this?.length!! == 16 ) {
                        binding.creditCartCardNumber.error = null
                    } else {
                        binding.creditCartCardNumber.error = "Please Enter Valid Card Number"
                    }
                }

                with(binding.creditCartCardMonth.editText?.text) {
                    if (this?.length!! < 2 || 13 < Integer.parseInt(this.toString()) || Integer.parseInt(
                            this.toString()
                        ) < 0 || isEmpty()
                    ) {
                        binding.creditCartCardMonth.error = "Please Enter Card Month"
                    } else {
                        binding.creditCartCardMonth.error = null
                    }
                }

                with(binding.creditCartCardYear.editText?.text) {
                    if (this?.length!! < 2 || 30 < Integer.parseInt(this.toString()) || Integer.parseInt(
                            this.toString()
                        ) < 23 || isEmpty()
                    ) {
                        binding.creditCartCardYear.error = "Please Enter Card Year"
                    } else {
                        binding.creditCartCardYear.error = null
                    }
                }

                with(binding.creditCartCardCvv.editText?.text) {
                    if (this?.length!! < 3 || Integer.parseInt(this.toString()) < 100 || isEmpty()) {
                        binding.creditCartCardCvv.error = "Please Enter Cvv"
                    } else {
                        binding.creditCartCardCvv.error = null
                    }
                }

                if (binding.creditCartCardNumber.error == null &&
                    binding.creditCartCardName.error == null &&
                    binding.creditCartCardMonth.error == null &&
                    binding.creditCartCardYear.error == null &&
                    binding.creditCartCardCvv.error == null
                ) {
                    // Menampilkan brand kartu kredit
                    with(binding.creditCartCardNumber.editText?.text.toString()) {
                        if (this[0].toString() == "5") {
                            com.squareup.picasso.Picasso.get()
                                .load("https://upload.wikimedia.org/wikipedia/commons/thumb/a/a4/Mastercard_2019_logo.svg/800px-Mastercard_2019_logo.svg.png")
                                .resize(1400, 1000)
                                .into(binding.imageCreditCartBrand)
                        }
                        if (this[0].toString() == "4") {
                            com.squareup.picasso.Picasso.get()
                                .load("https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Visa_Inc._logo.svg/2560px-Visa_Inc._logo.svg.png")
                                .resize(2560, 829)
                                .into(binding.imageCreditCartBrand)
                        }
                        if (this[0].toString() == "9") {
                            com.squareup.picasso.Picasso.get()
                                .load("https://upload.wikimedia.org/wikipedia/commons/2/25/Troy_logo.png")
                                .resize(1000, 500)
                                .into(binding.imageCreditCartBrand)
                        }
                        if (this[0].toString() == "3") {
                            com.squareup.picasso.Picasso.get()
                                .load("https://upload.wikimedia.org/wikipedia/commons/thumb/f/fa/American_Express_logo_%282018%29.svg/2052px-American_Express_logo_%282018%29.svg.png")
                                .resize(2000, 2000)
                                .into(binding.imageCreditCartBrand)
                        }
                    }

                    binding.tvCreditCardName.text =
                        binding.creditCartCardName.editText?.text.toString()
                    binding.tvCreditCardNumber.text =
                        binding.creditCartCardNumber.editText?.text.toString()
                    binding.tvCreditCardMonth.text =
                        binding.creditCartCardMonth.editText?.text.toString()
                    binding.tvCreditCardYear.text =
                        binding.creditCartCardYear.editText?.text.toString()

                    if (binding.tvCreditCardMonth.text.isNotEmpty() && binding.tvCreditCardYear.text.isNotEmpty()) {
                        binding.tvCreditCardSlash.visibility = View.VISIBLE
                    }
                    binding.btnCompletePayment.setText(R.string.buy)
                } else {
                    return@setOnClickListener
                }
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            lightColor = Color.GREEN
            enableLights(true)
        }
        val manager =
            ContextCompat.getSystemService(requireContext(), NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
