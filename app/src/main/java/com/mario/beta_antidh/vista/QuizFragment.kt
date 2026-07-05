
package com.mario.beta_antidh.vista
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.mario.beta_antidh.R
import com.mario.beta_antidh.databinding.FragmentQuizBinding
import com.mario.beta_antidh.databinding.ItemOpzioneBinding
import com.mario.beta_antidh.viewmodel.QuizViewModel




//fragment schermate quiz
class QuizFragment : Fragment() {
    private var _binding: FragmentQuizBinding? = null        //binding xml kotlin  vuoto 
     private val binding get() = _binding!!  //_ solo modifica viewmodel
    
    //viewmodel per scambiare dati fra fragment rispsote quiz
    private val viewModel: QuizViewModel by activityViewModels()

//ciclo vita fragment  collegamento inflate xml kotlin (creazione scherrmata)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    //prima schermata creata ma non interagibile, mo interazione 
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
// fragmanet osserva indice domanda viewmodel per ui 
        viewModel.indiceDomandaCorrente.observe(viewLifecycleOwner) { indice -> aggiornaUI(indice)}

                            //onClick bottone 
        binding.btnAvanti.setOnClickListener {
            salvaRisposta()
            if (viewModel.indiceDomandaCorrente.value == viewModel.domande.size - 1) {
                viewModel.inviaARisultato()  // fine quiz
                findNavController().navigate(R.id.action_quizFragment_to_resultFragment)  //usare navcontroller per passare fragment result
            } else {
                viewModel.vaiAvanti()
            }
        }
  //indietro
        binding.btnIndietro.setOnClickListener {
            salvaRisposta()
            viewModel.vaiIndietro()
        }
    }

     // in base all'indice genera opzioni scelta
 private fun aggiornaUI(indice: Int) {
        val domanda = viewModel.domande[indice]    //carica dataclass domanda indice
        binding.tvIndice.text = getString(R.string.domanda_indice, indice + 1, viewModel.domande.size)      // prende indice e indice totale per mettere nel testo(domanda rimaneneti)
        
         binding.tvDomanda.text = domanda.testo        //testo domanda 
        binding.llOpzioni.removeAllViews()  //llOpzioni (container bottoni) lo svuotiamo
        
        val risposte = viewModel.risposteUtente.value ?: mutableListOf()   //lista risposte
        val rispostaPrecedente = if (indice < risposte.size) risposte[indice] else ""   //se clicchiamo indietro ripristina stato rispsota

        domanda.opzioni.forEach { opzione ->
            val itemBinding = ItemOpzioneBinding.inflate(layoutInflater, binding.llOpzioni, false)  //for per predisposrre llopzioni per tenere le opzioni per rispsote
            itemBinding.tvOpzione.text = opzione    //mette testo di opzione
            
            val isSelected = rispostaPrecedente.startsWith(opzione)        //se torniamo indietro una deve essere selezionata. controllo se rispsota prec = opzione 
            selezionaOpzioneUI(itemBinding, isSelected)  //mette selezionato (blu) o no l'opzione
            
            //se torni indietro e prima avevamo altro , rende visibile altro (nascosto default)
            if (isSelected && opzione.equals("Altro", ignoreCase = true)) {
                binding.tilAltro.isVisible = true
            }

                // quando clicchi il bottone (siamo in  foreach), fa un altro for ch va fino al numero totlae di rispsote possibili a domanda
                //prende bottone, fa binding e lo rende grigio per poi accendere solo itemBindig (cliccato)
            itemBinding.root.setOnClickListener {
                for (i in 0 until binding.llOpzioni.childCount) {
                    val child = binding.llOpzioni.getChildAt(i)
                    val childBinding = ItemOpzioneBinding.bind(child)
                    selezionaOpzioneUI(childBinding, false)
                }
                selezionaOpzioneUI(itemBinding, true)
                binding.tilAltro.isVisible = opzione.equals("Altro", ignoreCase = true)        // se altro apre 
            }
            
            binding.llOpzioni.addView(itemBinding.root)   //crea view finale con selezione
        }

   
   // sempre nel for,  indice domanda == ultima 
        if (indice == viewModel.domande.size - 1) {
            binding.etAltro.hint = getString(R.string.hint_finale)       //mostra stringa hint (consiglio per opzioni nel campo altro)
        } else {
            binding.etAltro.hint = getString(R.string.hint_altro)    // campo testo in altro generico non alla fine che mettiam esempio
        }

            //siamo tornati indietro, gestione caso rispotsa altro
        if (indice < risposte.size) {
            val r = risposte[indice]  // salva risposta
            if (domanda.soloTesto) {   // domanda solo testo generica 
                binding.etAltro.setText(r)        //Ssalvi risposte e rendi visibile
                binding.tilAltro.isVisible = true
            } else {
                binding.etAltro.setText("")   //non è testo, mettiamo vuota  ++ falso per non rendere visibile
                binding.tilAltro.isVisible = false
            }
        } else {
            binding.etAltro.setText("")  //risposta vuota altro 
            binding.tilAltro.isVisible = false  // 7non visibile 
        }

        binding.btnIndietro.isVisible = indice > 0        // indietro visibile solo se non è la prima 
        binding.btnAvanti.text = if (indice == viewModel.domande.size - 1) getString(R.string.btn_analizza) else getString(R.string.btn_avanti)  // bottone ion basso a destra: Avanti fino alla 10a  poi analizza
    }


 //funzioni di Matrerial Design
    private fun selezionaOpzioneUI(item: ItemOpzioneBinding, selezionato: Boolean) {
        if (selezionato) {
            item.cardOpzione.setStrokeColor(ColorStateList.valueOf(resources.getColor(R.color.blue_primary, null))) //colore bordo
            item.cardOpzione.strokeWidth = (2 * resources.displayMetrics.density).toInt() // /spessore bordo
            item.ivRadio.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.blue_primary, null))  //cerchietto
            item.root.tag = "selected"  //tag selezionato per rispota
        } else {  
            item.cardOpzione.setStrokeColor(ColorStateList.valueOf(resources.getColor(R.color.gray_background, null)))
            item.cardOpzione.strokeWidth = 0  //non selezionato no bordo 
            item.ivRadio.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.gray_text, null))
            item.root.tag = null
        }
    }


    // crea string vuota, itera sulle opzioni, se un opzione ha il tag selected  crea oggetto con selected
      //poi prende la rispsota e la mette stringa nella variabile  
private fun salvaRisposta() {
        var opzioneScelta = ""
        for (i in 0 until binding.llOpzioni.childCount) {
            val child = binding.llOpzioni.getChildAt(i)
            if (child.tag == "selected") {
                val childBinding = ItemOpzioneBinding.bind(child)
                opzioneScelta = childBinding.tvOpzione.text.toString()
                break
            }
        }
        val testoAltro = binding.etAltro.text.toString().trim()   // se la rispsota è altro
       // risposta finale in base a se c'è altro, selezionato oppure niente 
      val rispostaFinale = when {
            opzioneScelta.isNotEmpty() && testoAltro.isNotEmpty() -> "$opzioneScelta ($testoAltro)"  //se avevam scelto altro + testo
            opzioneScelta.isNotEmpty() -> opzioneScelta    //scelta classica
            else -> ""
        }
        viewModel.aggiungiRisposta(rispostaFinale)   //salva risposta finale nel viewmodel
    }

//distruzione fragment
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  //evitare memLeak  lezione 12 textbook
    }
}
