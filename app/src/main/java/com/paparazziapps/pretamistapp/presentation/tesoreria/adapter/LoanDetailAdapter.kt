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

            val position = binding.circleCount
            val deuda = binding.btnDeuda
            val nombres = binding.tvNombres
            val plazo_vto = binding.tvDiasRestantes

            //Calcular total deuda
            val totalDebt = ((item.quotas?:0) - (item.quotasPaid?:0)).times(item.amountPerQuota?:0.0)


            val quotas = item.quotas?:0
            val typeLoanInDays = item.typeLoanDays?:1
            val quotasPerDays = quotas * typeLoanInDays
            val daysWhenEndsInDays = getDiasRestantesFromStart(item.loanStartDateFormatted?:"",quotasPerDays)
            val daysSett= if(daysWhenEndsInDays > 0) daysWhenEndsInDays else 0


            itemView.apply {
                position.text = adapterPosition.toString()
                nombres.text = replaceFirstCharInSequenceToUppercase(item.names.toString())
                plazo_vto.text = "Se vence en ${if(daysSett==1) "$daysSett día" else "$daysSett días"}"

                if(totalDebt > 0) {
                    deuda.text = "${getDoubleWithTwoDecimalsReturnDouble(totalDebt)} Deuda"
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