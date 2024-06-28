package com.example.whatsapp.activities

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.whatsapp.databinding.ActivityProfileBinding
import com.example.whatsapp.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityProfileBinding.inflate( layoutInflater )
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val storage by lazy {
        FirebaseStorage.getInstance()
    }

    private var haveCameraPermission = false
    private var haveGalleryPermission = false

    private val galleryManager = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){ uri ->
        if ( uri != null ){
            binding.imageProfile.setImageURI( uri )
            uploadImageStorage( uri )
        }else{
            showMessage("Nenhuma imagem selecionada")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        initializeToolbar()
        requestPermission()
        initializeClickEvents()

    }

    override fun onStart() {
        super.onStart()
        recoverInitialUserData()
    }

    private fun recoverInitialUserData() {

        val userId = firebaseAuth.currentUser?.uid
        if ( userId != null ){
            firestore
                .collection("users")
                .document( userId )
                .get()
                .addOnSuccessListener { documentSnapshot ->

                    val userData = documentSnapshot.data
                    if ( userData != null ){

                        val name = userData["name"] as String
                        val photo = userData["photo"] as String

                        binding.editProfileName.setText( name )

                        if ( photo.isNotEmpty() ){
                            Picasso.get()
                                .load ( photo )
                                .into( binding.imageProfile )
                        }
                    }
                }
        }
    }

    private fun uploadImageStorage(uri: Uri) {

        val userId = firebaseAuth.currentUser?.uid
        if ( userId != null ){

            storage
                .getReference("photos")
                .child("users")
                .child( userId )
                .child("profile.jpg")
                .putFile( uri )
                .addOnSuccessListener { task ->

                    showMessage("Sucesso ao fazer upload da imagem")
                    task.metadata
                        ?.reference
                        ?.downloadUrl
                        ?.addOnSuccessListener { url ->

                            val data = mapOf(
                                "photo" to url.toString()
                            )
                            updateProfileData( userId, data )
                        }

                }.addOnFailureListener {
                    showMessage("Erro ao fazer upload da imagem")
                }
        }
    }

    private fun updateProfileData(userId: String, data: Map<String, String>) {

        firestore
            .collection("users")
            .document( userId )
            .update( data )
            .addOnSuccessListener {
                showMessage("Sucesso ao atualizar perfil do usuario")
            }
            .addOnFailureListener {
                showMessage("Erro ao atualizar perfil do usuario")
            }
    }

    private fun initializeClickEvents() {

        binding.fabSelect.setOnClickListener {

            if ( haveGalleryPermission ){
                galleryManager.launch("image/*")
            }else{
                showMessage("Necessário permissão da galeria")
                requestPermission()
            }
        }

        binding.btnUpdate.setOnClickListener {

            val userName = binding.editProfileName.text.toString()
            if ( userName.isNotEmpty() ){

                val userId = firebaseAuth.currentUser?.uid
                if ( userId != null ){
                    val data = mapOf(
                        "name" to userName
                    )
                    updateProfileData( userId, data )
                }
            }else{
                showMessage("Digite um nome porfavor")
            }
        }
    }

    private fun requestPermission() {
        //verify if already have permission
        haveGalleryPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        haveGalleryPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        //Create a list of denied permissions
        val listDeniedPermissions = mutableListOf<String>()
        if (!haveCameraPermission){
            listDeniedPermissions.add( Manifest.permission.CAMERA )
        }
        if (!haveGalleryPermission){
            listDeniedPermissions.add( Manifest.permission.READ_MEDIA_IMAGES )
        }

        //request multiple permissions
        if ( listDeniedPermissions.isNotEmpty() ){
            val permissionsManager = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ){ permissions ->

                haveCameraPermission = permissions[Manifest.permission.CAMERA]
                    ?: haveCameraPermission

                haveGalleryPermission = permissions[Manifest.permission.READ_MEDIA_IMAGES]
                    ?: haveGalleryPermission

            }
            permissionsManager.launch( listDeniedPermissions.toTypedArray() )
        }
    }

    private fun initializeToolbar() {
        val toolbar = binding.includeProfileToolbar.tbMain
        setSupportActionBar( toolbar )
        supportActionBar?.apply {
            title = "Editar perfil"
            setDisplayHomeAsUpEnabled( true )
        }

    }
}