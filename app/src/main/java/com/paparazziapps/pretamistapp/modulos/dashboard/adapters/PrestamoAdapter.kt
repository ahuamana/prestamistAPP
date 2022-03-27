package com.paparazziapps.pretamistapp.modulos.dashboard.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ContentPrestamoBinding
import com.paparazziapps.pretamistapp.helper.MainApplication.Companion.ctx
import com.paparazziapps.pretamistapp.helper.getDoubleWithTwoDecimals
import com.paparazziapps.pretamistapp.helper.replaceFirstCharInSequenceToUppercase
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Prestamo
import java.text.SimpleDateFormat
import java.util.*

class PrestamoAdapter() : RecyclerView.Adapter<PrestamoAdapter.ViewHolder>() {

    var prestamosList: List<Prestamo> = mutableListOf()
    var fechaActual:String
    //Time peru [Tiempo Actual]
    var fecha = SimpleDateFormat("dd/MM/yyyy").apply {
        timeZone = TimeZone.getTimeZone("GMT-5")
        format(Date()).toString().also {
            fechaActual = it
        }
    }

    fun setData(listPrestamos: List<Prestamo>) {
        prestamosList = listPrestamos
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrestamoAdapter.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.content_prestamo, parent, false)

        return ViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: PrestamoAdapter.ViewHolder, position: Int) {

        val currentItem = prestamosList[position]
        holder.bind(currentItem, fechaActual)
    }

    override fun getItemCount(): Int {
        return prestamosList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ContentPrestamoBinding.bind(itemView)

        fun bind(item: Prestamo, fechaActual: String) {
            var telefono = binding.telefono
            var nombreCompleto = binding.nombreCompleto
            var diasRetrasados = binding.diasRetrasados
            var diasRetrasadosCardview = binding.cardviewDiasRetrasados
            var cardViewEnviarMsj = binding.cardviewEnviarMsj
            var lblDiasRetrasados = binding.lblDiasRetrasados

            itemView.apply {


                //Nombres de las personas


                //Metodo para el calculo de dias retrasados
                calcularDiasRetrasados(itemView, diasRetrasados, item, diasRetrasadosCardview, fechaActual)

                //Enviar mensaje a whatsapp
                cardViewEnviarMsj.apply {
                    setOnClickListener {

                        var diasretraso = diasRetrasados.text.toString().trim()

                        var calcularMontoTotalAPagar = getDoubleWithTwoDecimals((diasretraso.toInt() * (item.capital?.toDouble()?.div(item.plazo_vto!!)!!)))
                        //Mensaje
                        var msj = "Hola *${item.nombres}, ${item.apellidos}*, te escribimos para recordarte que tienes *${diasretraso} ${lblDiasRetrasados.text}* " +
                                "con los pagos de tu préstamo con un monto total a pagar de: *S./$calcularMontoTotalAPagar*"
                        openWhatsapp(item.celular, msj)
                    }
                }

                //Separar los espacios telefono


                //Asignar datos iniciales
                telefono.setText(item.celular)


                nombreCompleto.setText("${replaceFirstCharInSequenceToUppercase(item.nombres.toString().trim())}, ${replaceFirstCharInSequenceToUppercase(item.apellidos.toString().trim())}")

            }

        }

        fun openWhatsapp(celular: String?, msj: String)
        {
            //NOTE : please use with country code first 2digits without plus signed
            try {

                var msg = "Its Working"
                ctx().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=51" + celular + "&text=" + msj)).apply {
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }catch (t: Throwable){
                //whatsapp app not install
                println("Error whatsapp: $t")
            }
        }

        private fun calcularDiasRetrasados(
            itemView: View,
            diasRetrasados: MaterialTextView,
            item: Prestamo,
            diasRetrasadosCardview: CardView,
            fechaActual: String
        ) {

                //Calcular fecha actual
                println("Adapter ----> Nombres:${item.nombres} --- Apellidos: ${item.apellidos} --- Fecha actual:${fechaActual} ----> Fecha registrada: ${item.fecha}")

                //Calcular fecha registrada-- GTM Peru [ Unixtime to Formato fecha ]
                /*item.unixtime.also {
                    SimpleDateFormat("dd/MM/yyyy").apply {
                        timeZone = TimeZone.getTimeZone("GMT")
                        format(it).toString().also {

                    }
                 */

                val milisecondsByDay = 86400000


               //Restar unixtime y obtener los dias restantes
               var fechaActualUnixtime  = SimpleDateFormat("dd/MM/yyyy").parse(fechaActual).time
               var dias = (fechaActualUnixtime).minus(item.unixtime?:0)?.div(milisecondsByDay)

                diasRetrasados.setText(dias.toString())

                println("dias restante $dias")
                if(dias.toInt() == 0 )
                {
                    binding.cardviewEnviarMsj.isVisible = false
                    diasRetrasadosCardview.backgroundTintList = ctx().resources.getColorStateList(R.color.primarycolordark_two)
                }else
                {
                    if(dias.toInt() == 1)
                    {
                        binding.lblDiasRetrasados.setText("día retrasado")
                    }

                    binding.cardviewEnviarMsj.isVisible = true
                    diasRetrasadosCardview.backgroundTintList = ctx().resources.getColorStateList(R.color.red)
                }



            }

        }

    }



