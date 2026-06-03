package com.MatheusFoganholi.marquesautodetail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.MatheusFoganholi.marquesautodetail.R
import com.MatheusFoganholi.marquesautodetail.model.Agendamento

class AgendamentoAdapter(
    private var lista: List<Agendamento>,
    private val modoEmpresa: Boolean = false,
    private val onConfirmar: (Agendamento) -> Unit = {},
    private val onRecusar: (Agendamento) -> Unit = {}
) : RecyclerView.Adapter<AgendamentoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtStatus: TextView = itemView.findViewById(R.id.txtStatusAgendamento)
        val txtNomeCliente: TextView = itemView.findViewById(R.id.txtNomeCliente)
        val txtEmpresaAgendamento: TextView = itemView.findViewById(R.id.txtEmpresaAgendamento)
        val txtServico: TextView = itemView.findViewById(R.id.txtServico)
        val txtDataHora: TextView = itemView.findViewById(R.id.txtDataHora)
        val containerAcoes: LinearLayout = itemView.findViewById(R.id.containerAcoesEmpresa)
        val btnConfirmar: Button = itemView.findViewById(R.id.btnConfirmarAgendamentoItem)
        val btnRecusar: Button = itemView.findViewById(R.id.btnRecusarAgendamentoItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_agendamento, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val agendamento = lista[position]
        holder.txtStatus.text = agendamento.status
        holder.txtNomeCliente.text = agendamento.nomeCliente
        holder.txtEmpresaAgendamento.text = agendamento.empresaNome?.let { "Empresa: $it" } ?: "Empresa não informada"
        holder.txtServico.text = "Serviço: ${agendamento.servico}"
        holder.txtDataHora.text = "Data: ${agendamento.data} • Hora: ${agendamento.hora}"
        holder.containerAcoes.visibility = if (modoEmpresa && agendamento.status == "PENDENTE") View.VISIBLE else View.GONE
        holder.btnConfirmar.setOnClickListener { onConfirmar(agendamento) }
        holder.btnRecusar.setOnClickListener { onRecusar(agendamento) }
    }

    override fun getItemCount(): Int = lista.size

    fun atualizarLista(novaLista: List<Agendamento>) {
        lista = novaLista
        notifyDataSetChanged()
    }
}
