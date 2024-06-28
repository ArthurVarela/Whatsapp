package com.example.whatsapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.whatsapp.databinding.ActivityLoginBinding
import com.example.whatsapp.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate( layoutInflater )
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        initializeClickEvents()
    }

    override fun onStart() {
        super.onStart()
        verifyLoggedUser()
    }

    private fun verifyLoggedUser() {
        val currentUser = firebaseAuth.currentUser
        if ( currentUser != null ){
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }

    private fun initializeClickEvents() {
        binding.textRegister.setOnClickListener {
            startActivity( Intent( this, RegisterActivity::class.java ) )
        }

        binding.btnLogin.setOnClickListener {
            if ( fieldValidation() ) {
                loginUser()
            }

        }
    }

    private fun fieldValidation(): Boolean {
        email = binding.editLoginEmail.text.toString()
        password = binding.editLoginPassword.text.toString()

        if ( email.isNotEmpty() ) {
            binding.textInputLayoutLoginEmail.error = null
            if ( password.isNotEmpty() ){
                binding.textInputLayoutLoginPassword.error = null
                return true
            }else{
                binding.textInputLayoutLoginPassword.error = "Preencha sua senha"
                return false
            }
        }else{
            binding.textInputLayoutLoginEmail.error = "Preencha seu e-mail"
            return false
        }
    }

    private fun loginUser() {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                showMessage("Usuario Logado com sucesso!")
                startActivity(
                    Intent(this, MainActivity::class.java)
                )
            }.addOnFailureListener { exception ->

                try {
                    throw exception
                }catch ( invalidEmail: FirebaseAuthInvalidUserException ){
                    invalidEmail.printStackTrace()
                    showMessage("E-mail não cadastrado")
                }catch ( invalidCredentials: FirebaseAuthInvalidCredentialsException  ){
                    invalidCredentials.printStackTrace()
                    showMessage("Email ou senha estão incorretos!")
                }
            }
    }
}