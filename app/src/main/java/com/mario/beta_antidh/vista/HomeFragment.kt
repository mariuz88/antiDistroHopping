package com.mario.beta_antidh.vista


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mario.beta_antidh.R
import com.mario.beta_antidh.databinding.FragmentHomeBinding

// classe fragment home
class HomeFragment : Fragment() {

//setup ViewBinding 
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // collegamento ddel layout xml a questo file kotlin
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root 
    }

              //controllo delle azioni dell'utente
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //utente clicca inizia allora va a quiz
        binding.btnInizia.setOnClickListener {
            //navcontroller per cambiare schermata (destinazione definita in nav_graph.xml
            findNavController().navigate(R.id.action_homeFragment_to_quizFragment)
        }
    }
    //pulizia memoria nel momentio in cui l'utente esce dalla home - finevita fragment
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  //evita lo spreco di RAM in backgrouund
    }
}
