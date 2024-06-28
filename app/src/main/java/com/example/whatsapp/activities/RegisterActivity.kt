package com.example.whatsapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.whatsapp.databinding.ActivityRegisterBinding
import com.example.whatsapp.model.User
import com.example.whatsapp.utils.Constants
import com.example.whatsapp.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityRegisterBinding.inflate( layoutInflater )
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        initializeToolbar()
        initializeClickEvents()
    }

    private fun fieldValidation(): Boolean {

        name = binding.editName.text.toString()
        email = binding.editEmail.text.toString()
        password = binding.editPassword.text.toString()

        if (name.isNotEmpty()) {
            binding.textInputLayoutName.error = null

            if (email.isNotEmpty()){
                binding.textInputLayoutEmail.error = null

                if (password.isNotEmpty()){
                    binding.textInputLayoutPassword.error = null
                    return true
                }else{
                    binding.textInputLayoutPassword.error = "Preencha sua senha"
                    return false
                }
            }else{
                binding.textInputLayoutEmail.error = "Preencha seu e-mail"
                return false
            }
        }else {
            binding.textInputLayoutName.error = "Preencha o seu nome!"
            return false
        }
    }

    private fun initializeClickEvents() {
        binding.btnRegister.setOnClickListener {
            if ( fieldValidation() ) {
                registerUser(name, email, password)
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(
            email, password
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                val userId = it.result.user?.uid
                if ( userId != null ){
                    val user = User( userId, name, email )
                    saveUserOnFirestore( user )
                }
            }
        }.addOnFailureListener { exception ->
            try {
                throw exception
            }catch ( weakPasswordError: FirebaseAuthWeakPasswordException ){
                weakPasswordError.printStackTrace()
                showMessage("Senha fraca")
            }catch ( existingUserError: FirebaseAuthUserCollisionException ){
                existingUserError.printStackTrace()
                showMessage("Já existe um usuário com este e-mail")
            }catch ( invalidCredentialError: FirebaseAuthInvalidCredentialsException ){
                invalidCredentialError.printStackTrace()
                showMessage("E-mail inválido, digite um outro e-mail")
            }
        }
    }

    private fun saveUserOnFirestore(user: User) {
        firestore
            .collection(Constants.DB_USERS)
            .document( user.id )
            .set( user )
            .addOnSuccessListener {
                showMessage("Usuário cadastrado com sucesso")
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
            }.addOnFailureListener {
                showMessage("Erro ao cadastrar usuário")
            }
    }

    private fun initializeToolbar() {
        val toolbar = binding.includeToolbar.tbMain
        setSupportActionBar( toolbar )
        supportActionBar?.apply {
            title = "Faça seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}