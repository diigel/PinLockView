package com.beautycoder.applicationlockscreenexample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.beautycoder.pflockscreen.PFFLockScreenConfiguration
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment
import com.beautycoder.pflockscreen.viewmodels.PFPinCodeViewModel

class MainActivity : AppCompatActivity() {

    private val mCodeCreateListener = object : PFLockScreenFragment.OnPFLockScreenCodeCreateListener {
        override fun onCodeCreated(encodedCode: String) {
            Toast.makeText(this@MainActivity, "Code created", Toast.LENGTH_SHORT).show()
            PreferencesSettings.saveToPref(this@MainActivity, encodedCode)
        }

        override fun onNewCodeValidationFailed() {
            Toast.makeText(this@MainActivity, "Code validation error", Toast.LENGTH_SHORT).show()
        }
    }

    private val mLoginListener = object : PFLockScreenFragment.OnPFLockScreenLoginListener {

        override fun onCodeInputSuccessful() {
            Toast.makeText(this@MainActivity, "Code successfull", Toast.LENGTH_SHORT).show()
            showMainFragment()
        }

        override fun onFingerprintSuccessful() {
            Toast.makeText(this@MainActivity, "Fingerprint successfull", Toast.LENGTH_SHORT).show()
            showMainFragment()
        }

        override fun onPinLoginFailed() {
            Toast.makeText(this@MainActivity, "Pin failed", Toast.LENGTH_SHORT).show()
        }

        override fun onFingerprintLoginFailed() {
            Toast.makeText(this@MainActivity, "Fingerprint failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showLockScreenFragment()
        //PFSecurityManager.getInstance().setPinCodeHelper(new TestPFPinCodeHelperImpl());
    }

    private fun showLockScreenFragment() {
        PFPinCodeViewModel().isPinCodeEncryptionKeyExist.observe(
                this,
                Observer { result ->
                    if (result == null) {
                        return@Observer
                    }
                    if (result.error != null) {
                        Toast.makeText(this@MainActivity, "Can not get pin code info", Toast.LENGTH_SHORT).show()
                        return@Observer
                    }
                    showLockScreenFragment(result.result)
                }
        )
    }

    private fun showLockScreenFragment(isPinExist: Boolean) {
        val builder = PFFLockScreenConfiguration.Builder(this)
                .setTitle(if (isPinExist) "Unlock with your pin code or fingerprint" else "Create Code")
                .setCodeLength(6)
                .setLeftButton("Can't remeber")
                .setNewCodeValidation(true)
                .setNewCodeValidationTitle("Please input code again")
                .setUseFingerprint(true)
        val fragment = PFLockScreenFragment()

        fragment.setOnLeftButtonClickListener { Toast.makeText(this@MainActivity, "Left button pressed", Toast.LENGTH_LONG).show() }

        builder.setMode(if (isPinExist)
            PFFLockScreenConfiguration.MODE_AUTH
        else
            PFFLockScreenConfiguration.MODE_CREATE)
        if (isPinExist) {
            fragment.setEncodedPinCode(PreferencesSettings.getCode(this))
            fragment.setLoginListener(mLoginListener)
        }

        fragment.setConfiguration(builder.build())
        fragment.setCodeCreateListener(mCodeCreateListener)
        supportFragmentManager.beginTransaction()
                .replace(R.id.container_view, fragment).commit()

    }


    private fun showMainFragment() {
        val fragment = MainFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.container_view, fragment).commit()
    }


}
