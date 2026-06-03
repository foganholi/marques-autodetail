package com.MatheusFoganholi.marquesautodetail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.MatheusFoganholi.marquesautodetail.R
import com.MatheusFoganholi.marquesautodetail.model.EmpresaAutomotiva
import java.util.Locale

class EmpresaAdapter(
    private var empresas: List<EmpresaAutomotiva>,
    private val onDetalhesClick: (EmpresaAutomotiva) -> Unit
) : RecyclerView.Adapter<EmpresaAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNomeEmpresa: TextView = itemView.findViewById(R.id.txtNomeEmpresa)
        val txtEnderecoEmpresa: TextView = itemView.findViewById(R.id.txtEnderecoEmpresa)
        val txtDistanciaEmpresa: TextView = itemView.findViewById(R.id.txtDistanciaEmpresa)
        val txtAvaliacaoEmpresa: TextView = itemView.findViewById(R.id.txtAvaliacaoEmpresa)
        val txtServicoResumo: TextView = itemView.findViewById(R.id.txtServicoResumo)
        val btnVerDetalhes: Button = itemView.findViewById(R.id.btnVerDetalhes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_empresa, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val empresa = empresas[position]
        holder.txtNomeEmpresa.text = empresa.nome
        holder.txtEnderecoEmpresa.text = "${empresa.bairro} • ${empresa.endereco}"
        holder.txtDistanciaEmpresa.text = String.format(Locale("pt", "BR"), "%.1f km de você", empresa.distanciaKm)
        holder.txtAvaliacaoEmpresa.text = "★ ${empresa.avaliacao} • ${empresa.tempoMedio}"
        holder.txtServicoResumo.text = empresa.servicos.take(3).joinToString(" • ") { it.nome }
        holder.btnVerDetalhes.setOnClickListener { onDetalhesClick(empresa) }
        holder.itemView.setOnClickListener { onDetalhesClick(empresa) }
    }

    override fun getItemCount(): Int = empresas.size

    fun atualizar(novasEmpresas: List<EmpresaAutomotiva>) {
        empresas = novasEmpresas
        notifyDataSetChanged()
    }
}
