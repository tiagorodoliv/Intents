package br.edu.scl.ifsp.sdm.intents

import android.Manifest.permission.CALL_PHONE
import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.content.Intent.ACTION_CHOOSER
import android.content.Intent.ACTION_DIAL
import android.content.Intent.ACTION_PICK
import android.content.Intent.ACTION_VIEW
import android.content.Intent.EXTRA_INTENT
import android.content.Intent.EXTRA_TITLE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import br.edu.scl.ifsp.sdm.intents.Extras.PARAMETER_EXTRA
import br.edu.scl.ifsp.sdm.intents.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var parameterArl: ActivityResultLauncher<Intent>
    private lateinit var callPhonePermissionArl: ActivityResultLauncher<String>
    private  lateinit var pickImageArl:ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)
        setSupportActionBar(activityMainBinding.toolbarIn.toolbar)
        supportActionBar?.subtitle = localClassName

        //TRatando os dados de retornor ja utilizando o activity launcher
        parameterArl =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.getStringExtra(PARAMETER_EXTRA)?.also {
                        activityMainBinding.parameterTv.text = it
                    }
                }
            }

        callPhonePermissionArl =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
                if (permissionGranted) {
                    //chamar
                    callPhone(call = true)
                } else {
                    //caso nao deu permissao
                    Toast.makeText(
                        this,
                        getString(R.string.permision_required_to_call), Toast.LENGTH_SHORT
                    ).show()
                }
            }

        pickImageArl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            with(result){
                if (resultCode == RESULT_OK){
                    data?.data?.also {
                        activityMainBinding.parameterTv.text = it.toString()
                        startActivity(Intent(ACTION_VIEW).apply { data = it })
                    }
                }
            }

        }

        activityMainBinding.apply {
            //Enviando para parameter activity os dados do edt
            parameterBt.setOnClickListener {
                val parameterIntent =
                    Intent(this@MainActivity, ParameterActivity::class.java).apply {
                        putExtra(PARAMETER_EXTRA, parameterTv.text)
                    }
                parameterArl.launch(parameterIntent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            //Abrir em modo implicito menu
            R.id.openActivityMi -> {
                val parameterIntent = Intent("OPEN_PARAMETER_ACTIVITY_ACTION").apply {
                    putExtra(PARAMETER_EXTRA, activityMainBinding.parameterTv.text)
                }
                parameterArl.launch(parameterIntent)
                true
            }
            //Abrir em um navegador URL
            R.id.viewMi -> {
                val url = Uri.parse(activityMainBinding.parameterTv.text.toString())
                val browserIntent = Intent(ACTION_VIEW, url)
                startActivity(browserIntent())
                true
            }

            R.id.callMi -> {
                //verifico se versao que esta sendo executada e maior ou igual a versao M(marshemellow)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkCallingOrSelfPermission(CALL_PHONE) == PERMISSION_GRANTED) {
                        //chamar
                        callPhone(call = true)
                    } else {
                        //solicitar permissao
                        callPhonePermissionArl.launch(CALL_PHONE)
                    }
                } else {
                    //chamar permissao ja foi dada na instalacao
                    callPhone(call =  true)
                }
                true
            }

            R.id.dialMi -> {
                callPhone(call = false)
                true
            }

            R.id.pickMi -> {
                val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                pickImageArl.launch((Intent(ACTION_PICK).apply { setDataAndType(Uri.parse(imageDir),"image/*") }))
                true
            }

            R.id.chooserMi -> {
                startActivity(
                    Intent(ACTION_CHOOSER).apply {
                        putExtra(EXTRA_TITLE, getString(R.string.choose_your_favotire_browser))
                        putExtra(EXTRA_INTENT, browserIntent())
                    }
                )
                true
            }

            else -> {
                false
            }
        }
    }

    private fun callPhone(call: Boolean) {
        startActivity(
            Intent(if(call)ACTION_CALL else ACTION_DIAL).apply {
                "tel:${activityMainBinding.parameterTv.text}".also {
                    data = Uri.parse(it)
                }
            }
        )

    }

    private fun browserIntent():Intent{
        val url = Uri.parse(activityMainBinding.parameterTv.text.toString())
        return Intent(ACTION_VIEW, url)
    }
}