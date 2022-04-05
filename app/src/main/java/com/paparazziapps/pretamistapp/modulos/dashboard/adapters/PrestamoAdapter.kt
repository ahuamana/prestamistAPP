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
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.helper.MainApplication.Companion.ctx
import com.paparazziapps.pretamistapp.modulos.dashboard.interfaces.setOnClickedPrestamo
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Prestamo
import java.text.SimpleDateFormat
import java.util.*

class PrestamoAdapter(var setOnClickedPrestamo: setOnClickedPrestamo) : RecyclerView.Adapter<PrestamoAdapter.ViewHolder>() {

    var prestamosList: MutableList<Prestamo> = mutableListOf()
    var fechaActual:String
    //Time peru [Tiempo Actual]
    var fecha = SimpleDateFormat("dd/MM/yyyy").apply {
        timeZone = TimeZone.getTimeZone("GMT-5")
        format(Date()).toString().also {
            fechaActual = it
        }
    }

    fun setData(listPrestamos: MutableList<Prestamo>) {
        prestamosList = listPrestamos
        notifyDataSetChanged()
    }

    fun updateItem(position: Int, prestamo: Prestamo)
    {
        prestamosList.set(position,prestamo)
        notifyItemChanged(position)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrestamoAdapter.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.content_prestamo, parent, false)

        return ViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: PrestamoAdapter.ViewHolder, position: Int) {

        val currentItem = prestamosList[position]
        holder.bind(currentItem, fechaActual, setOnClickedPrestamo)
    }

    override fun getItemCount(): Int {
        return prestamosList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ContentPrestamoBinding.bind(itemView)

        fun bind(item: Prestamo, fechaActual: String, setOnClickedPrestamo: setOnClickedPrestamo) {
            var telefono = binding.telefono
            var nombreCompleto = binding.nombreCompleto
            var numero_dias_retrasados = binding.numeroDiasRetrasados
            var diasRetrasadosCardview = binding.cardviewDiasRetrasados
            var cardViewEnviarMsj = binding.cardviewEnviarMsj
            var lblDiasRetrasados = binding.lblDiasRetrasados
            var montoTotalAPagar:Double? = null
            var diasRetraso:Int = 10


            itemView.apply {

                //Metodo para el calculo de dias retrasados
                calcularDiasRetrasados(itemView, numero_dias_retrasados, item, diasRetrasadosCardview, fechaActual)



                //Enviar mensaje a whatsapp
                cardViewEnviarMsj.apply {
                    setOnClickListener {

                        //calcular el monto total a pagar
                        diasRetraso = numero_dias_retrasados.text.toString().toInt()
                        montoTotalAPagar = getDoubleWithTwoDecimalsReturnDouble((diasRetraso * (item.capital?.toDouble()?.div(item.plazo_vto!!)!!)))
                        //Mensaje
                        var msj = "Hola *${nombreCompleto.text}*, te escribimos para recordarte que tienes *${diasRetraso} ${lblDiasRetrasados.text}* " +
                                "con los pagos de tu préstamo con un monto total a pagar de: *S./$montoTotalAPagar*"
                        openWhatsapp(item.celular, msj)
                    }
                }

                //Separar los espacios telefono


                //Asignar datos iniciales
                telefono.setText(item.celular)


                nombreCompleto.setText("${replaceFirstCharInSequenceToUppercase(item.nombres.toString().trim())}, ${replaceFirstCharInSequenceToUppercase(item.apellidos.toString().trim())}")

                //Actualizar Pago al hacer click al itemview
                setOnClickListener {
                    //calcular el monto total a pagar
                    diasRetraso = numero_dias_retrasados.text.toString().toInt()
                    montoTotalAPagar = getDoubleWithTwoDecimalsReturnDouble(diasRetraso * (item.capital?.toDouble()?.div(item.plazo_vto!!)!!))

                    if(numero_dias_retrasados.text.toString().toInt() == 0)
                    {
                        println("numero de dias retrasados es cero: ${numero_dias_retrasados.text}")
                        setOnClickedPrestamo.actualizarPagoPrestamo(item, false, 0.0, adapterPosition, numero_dias_retrasados.text.toString())
                    }else
                    {
                        println("monto total a pagar: ${montoTotalAPagar}")
                        setOnClickedPrestamo.actualizarPagoPrestamo(item, true, montoTotalAPagar?:0.0, adapterPosition,numero_dias_retrasados.text.toString())
                    }

                }

            }


            setDiasRestantesPorPagar(item)

        }

        private fun setDiasRestantesPorPagar(item: Prestamo) {

            val lblDiasPorPagar = binding.lblDiasPorPagar
            val numeroDiasPorPagar = binding.numeroDiasPorPagar

            var diasEnQueTermina = getDiasRestantesFromStart(item.fecha?:"",item.plazo_vto?:0)

            numeroDiasPorPagar.apply {
                setText(diasEnQueTermina.toString())
            }

            lblDiasPorPagar.apply {
                text = when {
                    diasEnQueTermina == 1 -> "día por pagar"
                    else -> "días por pagar "
                }
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

            println("Adapter ----> Nombres:${item.nombres} --- Apellidos: ${item.apellidos} --- Fecha actual:${fechaActual} ----> Fecha registrada: ${item.fecha}")

            if(item.fechaUltimoPago != null)
            {
                //Obtener dias restantes si ya esta pagando diariamente ---> de los pagos actualizados
                getDiasRestantesFromDateToNowMinusDiasPagados(item.fecha?:"",item.diasPagados?:0).apply {

                    if(this.toInt() <= 0 )
                    {
                        diasRetrasados.text = "0"
                        binding.cardviewEnviarMsj.isVisible = false
                        diasRetrasadosCardview.backgroundTintList = ctx().resources.getColorStateList(R.color.primarycolordark_two)
                    }else
                    {
                        diasRetrasados.text = this
                        if(this.toInt() == 1)
                        {
                            binding.lblDiasRetrasados.text = "día retrasado"
                        }

                        binding.cardviewEnviarMsj.isVisible = true
                        diasRetrasadosCardview.backgroundTintList = ctx().resources.getColorStateList(R.color.red)
                    }
                }

            }else
            {
                //Calcular la fecha con el ultimo dia
                //Restar unixtime y obtener los dias restantes
                getDiasRestantesFromDateToNow(item.fecha?:"").apply {
                    diasRetrasados.setText(this)
                    if(this.toInt() == 0)
                    {
                        binding.cardviewEnviarMsj.isVisible = false
                        diasRetrasadosCardview.backgroundTintList = ctx().resources.getColorStateList(R.color.primarycolordark_two)
                    }else
                    {
                        if(this.toInt() == 1)
                        {
                            binding.lblDiasRetrasados.setText("día retrasado")
                        }

                        binding.cardviewEnviarMsj.isVisible = true
                        diasRetrasadosCardview.backgroundTintList = ctx().resources.getColorStateList(R.color.red)
                    }
                }

            }





            }

        }

    }



