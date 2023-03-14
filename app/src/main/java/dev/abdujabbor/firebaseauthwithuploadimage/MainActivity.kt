package dev.abdujabbor.firebaseauthwithuploadimage
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dev.abdujabbor.firebaseauthwithuploadimage.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        binding.signout.setOnClickListener {
            auth.signOut()
            binding.tvLoggedIn.text = "You are not logged in"
        }
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.btnUpdateProfile.setOnClickListener {
            updateUser()
        }
    }
    private fun registerUser() {
        binding.apply {
            val email = etEmailRegister.text.toString()
            val password = etPasswordRegister.text.toString()
            if (email.isNotEmpty()&&password.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        auth.createUserWithEmailAndPassword(email, password)

                        withContext(Dispatchers.Main){
                            checkLoggedState()
                        }

                    }catch (e:java.lang.Exception){
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }

        }
    }

    private fun loginUser() {
        binding.apply {
            val email = etEmailLogin.text.toString()
            val password = etPasswordLogin.text.toString()
            if (email.isNotEmpty()&&password.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        auth.signInWithEmailAndPassword(email, password)

                        withContext(Dispatchers.Main){
                            checkLoggedState()
                        }

                    }catch (e:java.lang.Exception){
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }

        }
    }

    private fun updateUser(){
        auth.currentUser?.let {user->
            val username = binding.etUsername.text.toString()
            val photoUri = Uri.parse("android.resource://$packageName/${R.drawable.baseline_logo_dev_24}")
            val profilUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(photoUri)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {

                    user.updateProfile(profilUpdate).await()
                    withContext(Dispatchers.Main){
                        checkLoggedState()
                        Toast.makeText(this@MainActivity, "Succesfully updated user profile", Toast.LENGTH_SHORT).show()
                    }

                }catch (e:java.lang.Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun checkLoggedState() {
        val user = auth.currentUser
        if (user==null){
            binding.tvLoggedIn.text = "You are not logged in "
        }else{
            binding.tvLoggedIn.text = "You are logged in "
        }
        binding.tvLoggedIn.text = "You are not logged in"
        binding.etUsername.setText(user?.displayName)
        binding.ivProfilePicture.setImageURI(user?.photoUrl)
    }

    override fun onStart() {
        super.onStart()
        checkLoggedState()
    }
}