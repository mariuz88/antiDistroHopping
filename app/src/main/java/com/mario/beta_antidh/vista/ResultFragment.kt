//fragment finale result
package com.mario.beta_antidh.vista
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.mario.beta_antidh.R
import com.mario.beta_antidh.databinding.FragmentResultBinding
import com.mario.beta_antidh.databinding.ItemAlternativaBinding
import com.mario.beta_antidh.viewmodel.QuizViewModel
import org.json.JSONObject

// creazione fragment per vedere ris finali elab. da AI
class ResultFragment : Fragment() {
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizViewModel by activityViewModels() //uso del viewmodel condiviso con altri fragmnt
//creazione fragment 
    //ancora nn si puo interagire
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Interagibile
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // in viewmodel abbiamo dato caricamento 
          //fragment osserva caricamento fino a distruzione
          //passaggio lla lambda
        viewModel.caricamento.observe(viewLifecycleOwner) { inCaricamento ->         
            binding.pbCaricamento.isVisible = inCaricamento
            binding.tvRisultato.isVisible = !inCaricamento
            binding.ivWinnerLogo.isVisible = !inCaricamento
            binding.tvWinnerName.isVisible = !inCaricamento
            binding.tvLaTuaDistro.isVisible = !inCaricamento
            binding.tvAlternativeLabel.isVisible = !inCaricamento
            binding.llAlternative.isVisible = !inCaricamento
        } //visibilità in base allo stato caricamento 

        viewModel.risultato.observe(viewLifecycleOwner) { jsonString ->
            if (jsonString != null) {
                mostraRisultatoFormattato(jsonString)  // Se json non nulla chiama func per formattare
            }  //stessa cosa con risultato
        }

        //gestione errore rete/risposta vuota,  se !=null testo errore 
        viewModel.errore.observe(viewLifecycleOwner) { errore ->
            if (errore != null) {
                binding.tvErrore.text = errore
                binding.tvErrore.isVisible = true
                binding.tvRisultato.isVisible = false
            } else {
                binding.tvErrore.isVisible = false  //scompare errore
            }
        }

        //resetviewmodel se inziiamo un altro quiz ---> torna al HomeFragment tramite navcontroller
        binding.btnNuovoQuiz.setOnClickListener {
            viewModel.reset()
            findNavController().navigate(R.id.action_resultFragment_to_homeFragment)
        }
    }


     //Usiamo funzionni di libreria org.json (import org.json.JSONObject)
//formattiamo stringhe per risultato da vedere
    private fun mostraRisultatoFormattato(jsonString: String) {
        try {
            val json = JSONObject(jsonString)
            val vincitore = json.getString("vincitore")
            val motivo = json.getString("motivo")
            val alternative = json.getJSONArray("alternative")

            binding.tvWinnerName.text = vincitore
            binding.tvRisultato.text = motivo
            
                //caricamento logo 
            val logoRes = getLogoResource(vincitore)
            binding.ivWinnerLogo.setImageResource(logoRes)

            binding.llAlternative.removeAllViews()     //mostrare alternative (array 2 elementi)
            for (i in 0 until alternative.length()) {
                val alt = alternative.getJSONObject(i)
                val altName = alt.getString("nome")
                val altDesc = alt.getString("descrizione")

                // creazione oggetto kotlin da file xml per alternative
                val itemBinding = ItemAlternativaBinding.inflate(layoutInflater, binding.llAlternative, false)
                itemBinding.tvAltName.text = altName
                itemBinding.tvAltDesc.text = altDesc
                itemBinding.ivAltLogo.setImageResource(getLogoResource(altName))  // nome descrizione  e img
                
                binding.llAlternative.addView(itemBinding.root)  //farle vedere definitivamente
            }

        } catch (e: Exception) {
            binding.tvWinnerName.text = "Risultato"   
            binding.tvRisultato.text = "Errore ritenta!"
        }
    }

//abbiamo scaricato una cartella con loghi distro rounded carini, seguono formato nome_nome.png 
    private fun getLogoResource(nome: String): Int {
        val formatted = nome.lowercase().replace(" ", "_").replace("!", "")   // gestione problematica PopOS!  immagine non si chiama con !
        //cambia spazi con _ (nome distro)  
        val resId = resources.getIdentifier(formatted, "drawable", requireContext().packageName)   //nome, cartella draweble + require context cartella per trovare drawable della nostra app -> ce ne sono molti
        return if (resId != 0) resId else R.drawable.logo_homepage
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null 
    }
}
