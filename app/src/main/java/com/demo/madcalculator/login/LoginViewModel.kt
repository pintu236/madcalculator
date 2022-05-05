package com.demo.madcalculator.login

import android.app.Activity
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.madcalculator.NetworkResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginViewModel : ViewModel() {

    private var mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();
    var loginLiveData = MutableLiveData<NetworkResponse<FirebaseUser?>>();

    fun isUserLoggedIn(): Boolean {
        return mFirebaseAuth.currentUser != null
    }

    fun loginUser(context: Activity, email: String, password: String) {
        loginLiveData.postValue(NetworkResponse.loading())
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(context) {
                if (it.isSuccessful) {
                    loginLiveData.postValue(NetworkResponse.success(mFirebaseAuth.currentUser))
                } else {
                    loginLiveData.postValue(NetworkResponse.error(it.exception?.message))
                }
            }
    }


}