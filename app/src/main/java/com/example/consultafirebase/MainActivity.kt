package com.example.consultafirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder


class MainActivity : AppCompatActivity() {

    private lateinit var etNomeProduto: TextInputEditText
    private lateinit var etValor: TextInputEditText
    private lateinit var btnCadastrarProduto: MaterialButton

    private lateinit var functions: FirebaseFunctions

    // usando o logcat (ferramenta do Android studio para verificar a saída)
    private val logEntry = "CADASTRO_PRODUTO";

    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // inicializar o uso dos functions COM A REGIAO ADEQUADA
        functions = Firebase.functions("southamerica-east1")

        // referenciando sem viewbinding
        etNomeProduto = findViewById(R.id.etNomeProduto)
        etValor = findViewById(R.id.etValor)
        btnCadastrarProduto = findViewById(R.id.btnCadastrarProduto)

        // ajustando o listener do btnCadastrar
        btnCadastrarProduto.setOnClickListener {

            val p = Product(etNomeProduto.text.toString(), etValor.text.toString().toDouble())
            cadastrarProduto(p)
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {

                        val e = task.exception
                        if (e is FirebaseFunctionsException) {
                            val code = e.code
                            val details = e.details
                        }

                        // dar um tratamento adequado...

                    }else{

                        /**
                         * Lembre-se que na Function criamos um padrão de retorno.
                         * Um JSON composto de status, message e payload.
                         * Podemos obter esse Json genérico e convertê-lo para um
                         * objeto da classe nossa FunctionsGenericResponse
                         * e a partir dali, tratar o não a conversão do payload. Veja:
                         */

                        // convertendo.
                        val genericResp = gson.fromJson(task.result, FunctionsGenericResponse::class.java)


                        // abra a aba Logcat e selecione "INFO" e filtre por
                        Log.i(logEntry, genericResp.status.toString())
                        Log.i(logEntry, genericResp.message.toString())

                        // claro, o payload deve ser convertido para outra coisa depois.
                        Log.i(logEntry, genericResp.payload.toString())

                        /*
                            Converter o "payload" para um objeto mais específico para
                            tratar o docId. Veja a classe "InsertResult"
                            Lembrando que para cada situação o payload é um campo "polimorfico"
                            por isso na classe de resposta genérica é um Any.
                        */
                        val insertInfo = gson.fromJson(genericResp.payload.toString(), GenericInsertResponse::class.java)

                        Snackbar.make(btnCadastrarProduto, "Produto cadastrado: " + insertInfo.docId, Snackbar.LENGTH_LONG).show();
                    }
                })
        }
    }

    private fun cadastrarProduto(p: Product): Task<String> {
        val data = hashMapOf(
            "name" to p.name,
            "price" to p.price
        )
        return functions
            .getHttpsCallable("addNewProduct")
            .call(data)
            .continueWith { task ->
                // se faz necessario transformar a string de retorno como uma string Json valida.
                val res = gson.toJson(task.result?.data)
                res
            }
    }
}
