package com.example.lugares.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.lugares.model.Lugar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase


class LugarDao {

    private var codigoUsuario: String
    private var firestore: FirebaseFirestore
    private var lugaresApp = "lugaresApp"
    private var miColeccion = "misLugares"//la coleccion

    init {
        val usuario  = Firebase.auth.currentUser?.email
        codigoUsuario = "$usuario" //el email es único desde que estamos autenticando

        firestore = FirebaseFirestore.getInstance() //instancia de Firestore
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build() //para tomar la instancia e inicializarla

    }

    //El método me trae toda una coleccion respectiva a un usuario con el nombre que le asignamos
    fun getAlData(): MutableLiveData<List<Lugar>>{   //el tipo de dato se cambió para que se adapte a firebase

        val listaLugares = MutableLiveData<List<Lugar>> ()//lo que vamos a retornar de tipo Mutable
        firestore.collection(lugaresApp) //indicamos la coleccion de donde queremos que tome la info
            .document(codigoUsuario)
            .collection(miColeccion)
            .addSnapshotListener { snapshot, e ->  //un snapshot es como una copia en un momento definido
               if(e != null) {
                   return@addSnapshotListener
               }
               if(snapshot != null) {
                   val lista = ArrayList<Lugar>()//inicializamos una lista básica vacia de lugares
                   val lugares = snapshot.documents

                   lugares.forEach {
                       val lugar = it.toObject(Lugar::class.java) //si los objetos tienen la estructura de lugar los agregamos a la lista
                       if(lugar != null){
                           lista.add(lugar)
                       }
                   }

                   listaLugares.value = lista
               }
            }
        return listaLugares
    }

     fun addLugar(lugar: Lugar){
         var document: DocumentReference//creamos documento de referencia
         if(lugar.id.isEmpty()) {
             //es un lugar nuevo / documento nuevo necesitamos un id unico para lo que vamos a guardar
             document = firestore
                 .collection(lugaresApp)
                 .document(codigoUsuario)
                 .collection(miColeccion)
                 .document()
             lugar.id = document.id //Asignamos el id
         }else{

             document = firestore
                 .collection(lugaresApp)
                 .document(codigoUsuario)
                 .collection(miColeccion)
                 .document(lugar.id) //agregamos el lugar.id
         }

         val set = document.set(lugar)//la instancia para ya guardar ese lugar
         set.addOnSuccessListener {  //para saber cuando algo se ejecuta y cuando no se ejecuta
             Log.d ("AddLugar", "Lugar Agregado - " + lugar.id)//cuando esto se ejecute bien
     }
         set.addOnCanceledListener {
             Log.d ("AddLugar", "Lugar NO Agregado - " + lugar.id)//cuando esto no se ejecuta bien
         }
     }


     fun updateLugar(lugar: Lugar){
         addLugar(lugar) //cuando no este vacio se va a actualizar
     }


     fun deleteLugar(lugar: Lugar) {
         if(lugar.id.isNotEmpty()){ //tengo que siempre validar mi id
             firestore
                 .collection(lugaresApp)
                 .document(codigoUsuario)
                 .collection(miColeccion)
                 .document(lugar.id)
                 .delete()
                 .addOnSuccessListener { //si tuve exito
                     Log.d ("DeleteLugar", "Lugar Eliminado - " + lugar.id)//cuando esto se ejecute bien
                 }
                 .addOnCanceledListener {
                     Log.d ("DeleteLugar", "Lugar NO Eliminado - " + lugar.id)//cuando esto no se ejecuta bien
                 }
         }
     }
}