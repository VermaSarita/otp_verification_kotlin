package com.ggpl.chatappkotlin
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ggpl.chatappkotlin.databinding.ActivityOtpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
    private var binding: ActivityOtpBinding? = null
    var verificationId :String? = null
    private var dialog: ProgressDialog? = null // Declare the dialog variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        dialog = ProgressDialog(this@OtpActivity)
        dialog!!.setMessage("Sending OTP....")
        dialog!!.setCancelable(false)
        dialog!!.show()
        val auth = FirebaseAuth.getInstance()
        supportActionBar!!.hide()
        val phoneNumber = intent.getStringExtra("phoneNumber")
        binding!!.phoneno.text="Verify $phoneNumber"



        val options = PhoneAuthOptions.newBuilder(auth!!)
            .setPhoneNumber(phoneNumber!!)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this@OtpActivity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {

                    showToast("Verification failed. ${e.message}")
                }

                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken
                ) {

                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showToast("Verification successful!")
                    val intent =Intent(this@OtpActivity,ProfileActivity::class.java)
                    startActivity(intent)
                    finishAffinity()

                } else {
                    showToast("Verification failed. Please try again.")
                }
            }
    }

    fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}
