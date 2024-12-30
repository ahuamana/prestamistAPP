package com.paparazziapps.pretamistapp.presentation.registro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paparazziapps.pretamistapp.databinding.ClientItemSelectableBinding
import com.paparazziapps.pretamistapp.domain.clients.ClientDomain
import com.paparazziapps.pretamistapp.domain.clients.ClientDomainSelect
import com.paparazziapps.pretamistapp.helper.replaceFirstCharInSequenceToUppercase

class ClientSelectAdapter(
    private val onClientSelectListener: OnClientSelectListener
) : RecyclerView.Adapter<ClientSelectAdapter.ClientSelectViewHolder>() {

    private var clients: List<ClientDomainSelect> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientSelectViewHolder {
        val binding = ClientItemSelectableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClientSelectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClientSelectViewHolder, position: Int) {
        holder.bind(clients[position])
    }

    override fun getItemCount(): Int = clients.size


    fun setClients(clients: List<ClientDomainSelect>) {
        this.clients = clients
        notifyDataSetChanged()
    }

    inner class ClientSelectViewHolder(private val binding: ClientItemSelectableBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(client: ClientDomainSelect) {

            val namesWithUpperCase = replaceFirstCharInSequenceToUppercase(client.name.trim())
            val lastnamesWithUpperCase = replaceFirstCharInSequenceToUppercase(client.lastName.trim())
            val fullName = "$namesWithUpperCase, $lastnamesWithUpperCase"

            binding.clientName.text = fullName
            binding.clientDetails.text = client.document
            binding.clientSelectCheckbox.isChecked = client.isSelected

            binding.clientSelectCheckbox.setOnClickListener {
                onClientSelectListener.onClientSelect(client)
            }
        }
    }
}

interface OnClientSelectListener {
    fun onClientSelect(client: ClientDomainSelect)
}