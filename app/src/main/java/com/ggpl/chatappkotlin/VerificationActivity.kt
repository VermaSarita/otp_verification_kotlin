package com.ggpl.chatappkotlin
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ggpl.chatappkotlin.databinding.ActivityVerificationBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class VerificationActivity : AppCompatActivity() {

    private var binding: ActivityVerificationBinding? = null
    private var auth: FirebaseAuth? = null
    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        FirebaseApp.initializeApp(this)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        if (auth?.currentUser != null) {
            val intent = Intent(this@VerificationActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


    supportActionBar!!.hide()
        binding!!.editnumber.requestFocus()
        binding!!.continuebtn.setOnClickListener(View.OnClickListener {
            val phoneNumber = binding!!.editnumber.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                initiatePhoneNumberVerification(phoneNumber)
            } else {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initiatePhoneNumberVerification(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60, // Timeout duration
            java.util.concurrent.TimeUnit.SECONDS,
            this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                        Toast.makeText(this@VerificationActivity, "Invalid request", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@VerificationActivity, "Verification failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    storedVerificationId = verificationId
                    resendToken = token

                    // TODO: Navigate to the OtpActivity with storedVerificationId
                    val intent = Intent(this@VerificationActivity, OtpActivity::class.java)
                    intent.putExtra("phoneNumber", phoneNumber)
                    intent.putExtra("verificationId", storedVerificationId)
                    startActivity(intent)
                }
            }
        )
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@VerificationActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@VerificationActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
