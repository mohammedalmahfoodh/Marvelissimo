package com.ecutbildning.marvelissimo.services

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import com.ecutbildning.marvelissimo.R
import com.ecutbildning.marvelissimo.activities.MainActivity
import com.ecutbildning.marvelissimo.dtos.User
import com.ecutbildning.marvelissimo.utilities.SnackbarManager
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FireBaseAuth {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private  var user: FirebaseUser? = null
    private var snackBarManager: SnackbarManager = SnackbarManager()
    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var userDataRef: DatabaseReference

    fun toggleOnline(status: Boolean) {
        if (user != null)
            userDataRef.child("online").setValue(status)
    }

    fun signIn(email: String, password: String, context: Context, view: View) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    user = auth.currentUser
                    userDataRef = database.child("users").child(user!!.uid)
                    //updateUI(user)
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                } else {
                    snackBarManager.createSnackbar(view, context.getString(R.string.signin_wrong_credentials), Color.RED)
                }

            }
    }


    fun createUser(email: String, password: String, firstName: String, lastName: String, context: Context, view: View) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    user = auth.currentUser
                    userDataRef = database.child("users").child(user!!.uid)
                    writeNewUser(firstName, lastName, user?.email!!, user?.uid!!)
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                } else {
                    snackBarManager.createSnackbar(view, context.getString(R.string.registration_failed), Color.RED)
                }
            }
    }
    private fun writeNewUser(firstName: String, lastName: String, email: String, uid: String) {
        val newUser = User(email, firstName, lastName)
        userDataRef.setValue(newUser)
        toggleOnline(true)
    }

    fun addFavorite(itemId: String) {
        userDataRef.child("favoriteCharacters/$itemId").setValue(true)
    }

    fun deleteFavorite(itemId: String) {
        userDataRef.child("favoriteCharacters/$itemId").removeValue()
    }
}