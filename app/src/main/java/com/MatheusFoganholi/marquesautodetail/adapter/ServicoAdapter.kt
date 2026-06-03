package com.MatheusFoganholi.marquesautodetail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.MatheusFoganholi.marquesautodetail.R
import com.MatheusFoganholi.marquesautodetail.model.Servico
import java.text.NumberFormat
import java.util.Locale

class ServicoAdapter(
    private var servicos: List<Servico>,
    private val onAgendarClick: (Servico) -> Unit
) : RecyclerView.Adapter<ServicoAdapter.ViewHolder>() {

    private val moeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNomeServico: TextView = itemView.findViewById(R.id.txtNomeServico)
        val txtDescricaoServico: TextView = itemView.findViewById(R.id.txtDescricaoServico)
        val txtPrecoServico: TextView = itemView.findViewById(R.id.txtPrecoServico)
        val txtDuracaoServico: TextView = itemView.findViewById(R.id.txtDuracaoServico)
        val btnAgendarServico: Button = itemView.findViewById(R.id.btnAgendarServico)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_servico, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val servico = servicos[position]
        holder.txtNomeServico.text = servico.nome
        holder.txtDescricaoServico.text = servico.descricao
        holder.txtPrecoServico.text = moeda.format(servico.preco)
        holder.txtDuracaoServico.text = "${servico.duracaoMinutos} min"
        holder.btnAgendarServico.setOnClickListener { onAgendarClick(servico) }
    }

    override fun getItemCount(): Int = servicos.size
}
