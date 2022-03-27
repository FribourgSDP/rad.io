package com.github.fribourgsdp.radio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class CreateUserProfileActivity : AppCompatActivity() {
    private lateinit var nameInput : EditText
    private lateinit var saveButton : Button
    private lateinit var user : User
    private var db = FirestoreDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user_profile)



        nameInput = findViewById(R.id.nameInput)
        saveButton = findViewById(R.id.saveButton)


        prefillFields()
        saveButton.setOnClickListener(saveButtonBehaviour())
    }

    private fun saveButtonBehaviour() : View.OnClickListener{
        return View.OnClickListener {
            val textValue = nameInput.text
            user = User(textValue.toString())
            db.setUser("testCreateUserProfile",user)
            user.save(this)
            startActivity(Intent(this, UserProfileActivity::class.java))

        }



    }
    private fun prefillFields(){
        user = User.load(this)
        nameInput.setText(user.name)

    }
}