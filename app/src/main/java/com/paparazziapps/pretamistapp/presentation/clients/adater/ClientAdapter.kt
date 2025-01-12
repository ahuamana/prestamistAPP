package com.paparazziapps.pretamistapp.presentation.clients.adater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paparazziapps.pretamistapp.databinding.ClientItemBinding
import com.paparazziapps.pretamistapp.domain.clients.ClientDomain
import com.paparazziapps.pretamistapp.helper.replaceFirstCharInSequenceToUppercase

class ClientAdapter(
    private val listener : SetOnClickListenerClient
) : RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    private var clients: List<ClientDomain> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val binding = ClientItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        holder.bind(clients[position])
    }


    override fun getItemCount(): Int = clients.size

    fun setClients(clients: List<ClientDomain>) {
        this.clients = clients
        notifyDataSetChanged()
    }

    inner class ClientViewHolder(private val binding: ClientItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(client: ClientDomain) {

            val namesWithUpperCase = replaceFirstCharInSequenceToUppercase(client.name.trim())
            val lastnamesWithUpperCase = replaceFirstCharInSequenceToUppercase(client.lastName.trim())
            val fullName = "$namesWithUpperCase, $lastnamesWithUpperCase"

            binding.clientName.text = fullName
            binding.clientDetails.text = client.phone
            // Bind other client properties as needed

            binding.root.setOnClickListener {
                listener.onItemClicked(client)
            }
        }
    }
}