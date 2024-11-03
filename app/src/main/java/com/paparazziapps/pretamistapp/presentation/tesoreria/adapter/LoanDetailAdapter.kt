package com.paparazziapps.pretamistapp.presentation.tesoreria.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ItemDetallePrestamoBinding
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.domain.LoanDomain

class LoanDetailAdapter : RecyclerView.Adapter<LoanDetailAdapter.ViewHolder>() {



    var prestamosDetalle: MutableList<LoanDomain> = mutableListOf()

    fun setData(listLoanRespons: MutableList<LoanDomain>) {
        prestamosDetalle = listLoanRespons
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanDetailAdapter.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_detalle_prestamo, parent, false)

        return LoanDetailAdapter.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LoanDetailAdapter.ViewHolder, position: Int) {

        val currentItem = prestamosDetalle[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return prestamosDetalle.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ItemDetallePrestamoBinding.bind(itemView)

        fun bind(item: LoanDomain){

            var position = binding.circleCount
            var deuda = binding.btnDeuda
            var nombres = binding.tvNombres
            var plazo_vto = binding.tvDiasRestantes

            //
            var plazo = getDiasRestantesFromStart(item.fecha_start_loan?:"",item.quotas?:0)

            //Calcular deuda de dias no pagados
            var deudaTotal = getDiasRestantesFromDateToNowMinusDiasPagados(item.fecha_start_loan?:"",item.quotasPaid?:0).toInt().times(item.amountPerQuota?:0.0)

            itemView.apply {
                position.text = adapterPosition.toString()
                nombres.text = replaceFirstCharInSequenceToUppercase(item.names.toString())
                plazo_vto.text = "Se vence en ${if(plazo==1) "$plazo día" else "$plazo días"}"

                if(deudaTotal > 0) {
                    deuda.text = "${getDoubleWithTwoDecimalsReturnDouble(deudaTotal)} Deuda"
                }else {
                    deuda.apply {
                        text = "Sin Deudas"
                        standardSimpleButtonOutline(itemView.context)
                    }

                }
            }
        }
    }

}