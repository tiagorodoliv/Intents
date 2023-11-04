package br.edu.scl.ifsp.sdm.intents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.edu.scl.ifsp.sdm.intents.Extras.PARAMETER_EXTRA
import br.edu.scl.ifsp.sdm.intents.databinding.ActivityParameterBinding

class ParameterActivity : AppCompatActivity() {
    private val activityParameterBinding: ActivityParameterBinding by lazy {
        ActivityParameterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityParameterBinding.root)
        setSupportActionBar(activityParameterBinding.toolbarIn.toolbar)
        supportActionBar?.subtitle = localClassName

        //recebo o dado enviado pela main actitivity e seto ele no edt
        intent.getStringExtra(PARAMETER_EXTRA)?.let {
            activityParameterBinding.parameterEt.setText(it)
        }
        activityParameterBinding.apply {
            returnCloseBt.setOnClickListener {
                //criando uma intent vazia para devolver o resultado
                val resultIntent = Intent().apply {
                    putExtra(PARAMETER_EXTRA, parameterEt.text.toString())
                }
                setResult(RESULT_OK,resultIntent)
                finish()
            }
        }
    }
}