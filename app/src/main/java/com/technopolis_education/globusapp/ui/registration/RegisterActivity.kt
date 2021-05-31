package com.technopolis_education.globusapp.ui.registration

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.technopolis_education.globusapp.MainActivity
import com.technopolis_education.globusapp.R
import com.technopolis_education.globusapp.api.WebClient
import com.technopolis_education.globusapp.model.UserRegistrationRequest
import com.technopolis_education.globusapp.model.UserToken
import com.technopolis_education.globusapp.type.DialogType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private val webClient = WebClient().getApi()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        title = "Registration"

        val nameAndSurname = findViewById<EditText>(R.id.name_surname_field)
        val email = findViewById<EditText>(R.id.e_mail_field)
        val password = findViewById<EditText>(R.id.password_field)
        val confirmPassword = findViewById<EditText>(R.id.confirm_password_field)

        val confirm = findViewById<Button>(R.id.confirm_register)

        confirm.setOnClickListener {
            if (nameAndSurname.text.toString().isEmpty()) {
                nameAndSurname.hint = getString(R.string.hint)
                nameAndSurname.setHintTextColor(Color.RED)
            }

            if (email.text.toString().isEmpty()) {
                email.hint = getString(R.string.hint)
                email.setHintTextColor(Color.RED)
            }

            if (password.text.toString().isEmpty()) {
                password.hint = getString(R.string.hint)
                password.setHintTextColor(Color.RED)
            }

            if (confirmPassword.text.toString().isEmpty()) {
                confirmPassword.hint = getString(R.string.hint)
                confirmPassword.setHintTextColor(Color.RED)
            }

            if (confirmPassword.text.toString() != password.text.toString()) {
                val toast =
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.password_missmatch),
                        Toast.LENGTH_SHORT
                    )
                toast.show()
            }

            if (nameAndSurname.text.toString().isNotEmpty()
                && email.text.toString().isNotEmpty()
                && password.text.toString().isNotEmpty()
                && confirmPassword.text.toString().isNotEmpty()
                && confirmPassword.text.toString() == password.text.toString()
//                && checkFormat(password.text.toString(), DialogType.PASSWORD)
                && checkFormat(email.text.toString(), DialogType.EMAIL)
                && checkFormat(nameAndSurname.text.toString(), DialogType.USERNAME)
            ) {
                val userName = nameAndSurname.text.toString().split(" ")[0]
                val userSurname = nameAndSurname.text.toString().split(" ")[1]
                val userReg = UserRegistrationRequest(
                    userName,
                    userSurname,
                    email.text.toString(),
                    password.text.toString()
                )

                val callReg = webClient.reg(userReg)
                callReg.enqueue(object : Callback<UserToken> {
                    override fun onResponse(call: Call<UserToken>, response: Response<UserToken>) {
                        startFragment(response.body())
                    }

                    override fun onFailure(call: Call<UserToken>, t: Throwable) {
                        Log.i("test", "error $t")
                    }
                })
            } else if (nameAndSurname.text.toString().isNotEmpty()
                && email.text.toString().isNotEmpty()
                && password.text.toString().isNotEmpty()
                && confirmPassword.text.toString().isNotEmpty()
            ) {
                if (!checkFormat(email.text.toString(), DialogType.EMAIL)) {
                    val toast =
                        Toast.makeText(
                            applicationContext,
                            "Invalid email",
                            Toast.LENGTH_SHORT
                        )
                    toast.show()
                } else if (!checkFormat(nameAndSurname.text.toString(), DialogType.USERNAME)) {
                    val toast =
                        Toast.makeText(
                            applicationContext,
                            "Invalid username",
                            Toast.LENGTH_SHORT
                        )
                    toast.show()
                }
            }
        }
    }

    private fun startFragment(userResponse: UserToken?) {
        val auth = getSharedPreferences("AUTH", Context.MODE_PRIVATE)
        val userToken = getSharedPreferences("USER TOKEN", Context.MODE_PRIVATE)
        auth.edit().putBoolean("Success", true).apply()
        userToken.edit().putString("UserToken", userResponse?.objectToResponse?.token).apply()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun checkFormat(edit: String, typeOfDialog: DialogType): Boolean {
        return when (typeOfDialog) {
            DialogType.USERNAME -> edit.matches("[a-zA-Z][a-zA-Z_0-9\\-]+ [a-zA-Z][a-zA-Z_0-9\\-]+".toRegex())
            DialogType.EMAIL -> edit.matches("[a-zA-Z_0-9\\-]+.[a-zA-Z][a-zA-Z_0-9\\-]+@[a-z]{2,7}\\.[a-zA-Z_0-9\\-]+".toRegex()) && edit.length < 255
            DialogType.PASSWORD -> edit.matches("[a-zA-Z0-9!@#$%&]+".toRegex())
        }
    }
}