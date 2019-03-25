package com.pmdm.sauky.mensajefirebase

import java.util.Date

import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.database.ChildEventListener

import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.activity_main.*

import com.google.firebase.iid.FirebaseInstanceId
import org.w3c.dom.Text


class MainActivity : AppCompatActivity() {
    // para filtrar los logs
    val TAG = "Servicio"
    var aleatorio: Int = 0
    var saleatorio: String =""
    var numeroJugador: Int = 0
    var nintentos: Int = 3
    lateinit var snumeroJugador: String
    var aleatoriorecibido: Int = 0
    var numeroJugadorRecibido: Int = 0
    lateinit var snintentos: String
    lateinit var cajaNum: EditText
    lateinit var cajaMostrar: TextView
    lateinit var benviar: Button
    lateinit var baleatorio: Button
    lateinit var texto: Toast
    lateinit var cajaIntentos: TextView
    lateinit var tv4: TextView

    // referencia de la base de datos

    public var database: DatabaseReference? = null
    // Token del dispositivo
    public var FCMToken: String? = null
    // key unica creada automaticamente al añadir un child
    var misDatos : Datos = Datos()
    lateinit var key: String
    // para actualizar los datos necesito un hash map
    val miHashMapChild = HashMap<String, Any>()






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        cajaNum = findViewById(R.id.editText)
        baleatorio = findViewById(R.id.botonAleatorio)
        cajaMostrar = findViewById(R.id.textoBD)
        tv4 = findViewById(R.id.textView4)
        tv4.setText("Numero de Intentos: "+nintentos)


        aleatorio = java.util.Random().nextInt(10)
        saleatorio = aleatorio.toString()
        misDatos.aleatorio = aleatorio
        texto = Toast.makeText(applicationContext, "Numero generado: "+aleatorio, Toast.LENGTH_SHORT)
        Log.d(TAG, "Numero Aleatorio: " + misDatos.aleatorio)


        // referencia a la base de datos del proyecto en firebase
        database = FirebaseDatabase.getInstance().getReference("/dispositivos")

        // boton de la plantilla
        fab.setOnClickListener { view ->
            snumeroJugador = cajaNum.text.toString()
            numeroJugador = snumeroJugador.toInt()
            misDatos.numeroJugador = numeroJugador
            Snackbar.make(view, snumeroJugador, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            Log.d(TAG, "Numero Jugador: " + misDatos.numeroJugador)

            // Creamos el hashMap en el objeto
            misDatos.crearHashMapDatos()
            // actualizamos la base de datosw
            miHashMapChild.put(key.toString(),misDatos.miHashMapDatos)
            //Log.d(TAG, "Descripcion misDatos " + misDatos.miHashMapDatos)
            // actualizamos el child
            database!!.updateChildren(miHashMapChild)
            cajaNum.setText("")
        }

        // solo lo llamo cuando arranco la app
        // evito que cuando se pasa por el onCreate vuelva a ejecutarse
        if (savedInstanceState == null) {
            try {
                // Obtengo el token del dispositivo.
                FCMToken = FirebaseInstanceId.getInstance().token
                // creamos una entrada nueva en el child "dispositivos" con un key unico automatico
                key = database!!.push().key!!
                // guardamos el token, dispositivo, tiempo actual en la data class

                misDatos = Datos(FCMToken.toString(),android.os.Build.MANUFACTURER+" "+android.os.Build.ID, misDatos.numeroJugador, misDatos.aleatorio, Date())
                // creamos el hash map
                misDatos.crearHashMapDatos()
                // guardamos los datos en el hash map para la key creada anteriormente
                miHashMapChild.put(key.toString(), misDatos.miHashMapDatos)
                // actualizamos el child
                database!!.updateChildren(miHashMapChild)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG,"Error escribiendo datos ${e}")
            }
        }
        // inicializo el listener para los eventos de la basededatos
        initListener()
    }

    /**
     * Listener para los distintos eventos de la base de datos
     */
    private fun initListener() {
        val childEventListener = object : ChildEventListener {
            override fun onChildRemoved(p0: DataSnapshot) {
                Log.d(TAG, "Datos borrados: " + p0.key)
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                // creo un objeto para recojer los datos cambiados
                var misDatosCambiados = Datos("","", numeroJugador, aleatorio, Date())

                cajaMostrar.append("Numero Elegido: "+numeroJugador.toString()+"\n")
                cajaMostrar.append("Numero Aleatorio: "+aleatorio.toString()+"\n")

                // introduzco en el objeto los datos cambiados que vienen en el snapshot
                //misDatosCambiados = p0.getValue(Datos::class.java)!!
                // muestro datos desde el objeto

                Log.d(TAG, "Datos cambiados: " + misDatosCambiados.nombre + " " + misDatosCambiados.hora.time + " " + misDatosCambiados.numeroJugador + " " + misDatosCambiados.aleatorio  )
                if(misDatosCambiados.aleatorio == misDatosCambiados.numeroJugador){
                    //Enviar notificacion por el servidor
                    Log.d(TAG,"LOS NUMEROS COINCIDEN, TU GANAS")
                    texto = Toast.makeText(applicationContext, "ENHORABUENA, HAS GANADO", Toast.LENGTH_SHORT)
                    texto.show()
                    baleatorio.isClickable=true
                    cajaMostrar.setText("")

                }else{
                    nintentos --
                    if(nintentos == 0){
                        texto = Toast.makeText(applicationContext,"HAS PERDIDO", Toast.LENGTH_SHORT)
                        texto.show()
                        baleatorio.isClickable=true
                        cajaNum.isClickable=false
                        fab.isClickable=false
                    }
                    tv4.setText("Numero de Intentos: "+nintentos)

                }
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                Log.d(TAG, "Datos movidos")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                // onChildAdded() capturamos la key
                Log.d(TAG, "Datos añadidos: " + p0.key)

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "Error cancelacion")
            }
        }
        // attach el evenListener a la basededatos
        database!!.addChildEventListener(childEventListener)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

}

