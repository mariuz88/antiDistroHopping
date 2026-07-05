package com.mario.beta_antidh.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mario.beta_antidh.BuildConfig
import com.mario.beta_antidh.modello.Domanda
import com.mario.beta_antidh.modello.GroqMessage
import com.mario.beta_antidh.modello.GroqRequest
import com.mario.beta_antidh.rete.RetrofitClient
import kotlinx.coroutines.launch


//creazione viewMODEL preservare i dati 
class QuizViewModel : ViewModel() {
    private val _indiceDomandaCorrente = MutableLiveData(0)//collegamento ui e viewmodel livedata
    val indiceDomandaCorrente: LiveData<Int> get() = _indiceDomandaCorrente   

    private val _risposteUtente = MutableLiveData<MutableList<String>>(mutableListOf())     //variabili mutabile per viewmodel (_var)
    val risposteUtente: LiveData<MutableList<String>> get() = _risposteUtente

    private val _risultato = MutableLiveData<String?>(null)
    val risultato: LiveData<String?> get() = _risultato

    
    private val _caricamento = MutableLiveData(false)
    val caricamento: LiveData<Boolean> get() = _caricamento

    private val _errore = MutableLiveData<String?>(null)
    val errore: LiveData<String?> get() = _errore

    //lista di dataclass (Domanda.kt)
    val domande = listOf(
        //1
        Domanda("Livello di esperienza con Linux",listOf("Principiante totale", "Intermedio (so muovermi un po')", "Avanzato / Esperto", "Altro")),
          //2..
        Domanda("Caso d'uso",listOf("Uso quotidiano / Studio / Lavoro d'ufficio", "Gaming / Creazione contenuti", "Programmazione / Server",  "Sicurezza / Pen. Testing", "Altro")),
    
        Domanda("Architettura processore",listOf("x86_64 (computer classici)", "32-bit per computer molto vecchi", "ARM (es. Raspberry Pi, Mac M1/M2)", "RISC-V", "Altro")),
    
        Domanda("Scheda video NVIDIA", listOf("Sì", "No"),haAltro = false),
        
        Domanda("Ecosistema e gestione pacchetti", listOf("Famiglia Debian/Ubuntu (.deb)", "Famiglia Arch (AUR, pacman)", "Famiglia Fedora/RedHat (.rpm)", "Non lo so, sono nuovo", "Indifferente" ,"Altro") ),
       
        Domanda("Aggiornamenti", listOf("Stable (affidabilità al primo posto)", "Rolling (sempre le ultime novità)", "Altro")),
        Domanda("Ideologia", listOf("Distro totalmente community driven e senza telemetria", "Basta che sia senza telemetria invasiva", "Non mi interessa, basta che funzioni bene", "Altro")),

        Domanda("Modello di Sicurezza e Controllo",listOf("Voglio il controllo totale sui servizi (Init system)", "Voglio sicurezza out-of-the-box (SELinux/AppArmor)", "Voglio un sistema immutabile (non modificabile se non tramite update)", "Non so", "Altro")),

        Domanda("Privacy & Networking", listOf("Uso standard", "VPN/Tor integrato", "Hardening del kernel (Grsecurity/Zen)", "Altro")),
        
        Domanda("C'è altro che dovremmo sapere?",listOf("No, sono a posto così", "Altro"),haAltro = true)
    )

    
// funzioni navigazioni avanti e dietro
    fun vaiAvanti() {
        val indice = _indiceDomandaCorrente.value ?: 0
        if (indice < domande.size - 1) {        // fino all'ultima
            _indiceDomandaCorrente.value = indice + 1
        }
    }

    fun vaiIndietro() {
        val indice = _indiceDomandaCorrente.value ?: 0
        if (indice > 0) {//fino alla prima
            _indiceDomandaCorrente.value = indice - 1
        }
    }

 //salva indice e salva rispsta 
    fun aggiungiRisposta(risposta: String) {
        val indice = _indiceDomandaCorrente.value ?: 0
        val risposte = _risposteUtente.value ?: mutableListOf()
        if (indice < risposte.size) { //risposta vechia
            risposte[indice] = risposta
        } else {
            risposte.add(risposta) //risposta nuova alla lista
        }
        _risposteUtente.value = risposte
    }


       //funzione per mandare e ricevere la rispsota da Groq
    fun inviaARisultato() {
        _caricamento.value = true
        _errore.value = null  // due var. gestione corutines 

        //coroutine scope per gestire ciclo di vita della coroutine nel viewmodel 
        viewModelScope.launch {
            try {
                val prompt = costruisciPrompt()  //funzione sotto
                val request = GroqRequest(
                    messages = listOf(
                        GroqMessage(role = "system", content = getSystemPrompt()),  //istruzioni
                        GroqMessage(role = "user", content = prompt)//scelte utente
                    )
                )
                // Chiamata retrofit
                val response = RetrofitClient.service.chatCompletions("Bearer ${BuildConfig.GROQ_API_KEY}", request)  // prende retrofitClient, accende a service e poi alla nostra coroutine chat completions
                //chat compleation richiede 1) Header Bearer standard con API KEY da buildCOnfig 2) la richiesta

                // accede alla rispsota dell'IA  tutte possibili richieste
                val content = response.choices.firstOrNull()?.message?.content  //solo la prima se non è NULL  (prendiamo solo la stringa ossia .content dalla dataclass message)
                if (content != null) {
                    _risultato.value = content        // se not null salva risultato 
                } else {
                    _errore.value = "Errore: risposta vuota dall'IA"
                }
            } catch (e: Exception) { //in caso di errore di rete
                _errore.value = "Errore di rete: ${e.message}"  
            } finally {    
                _caricamento.value = false     //fine di interazione rete
            }
        }
    }


//  *** Il prompt che abbiamo usato qui è stato perfezionato da IA. Abbiamo usato dati di distrowhatch e abbiamo chiesto una domanda in formato JSON ordinata
    // prompt da inviare
    private fun getSystemPrompt(): String {
        return """
            Sei un esperto di distribuzioni Linux universale (per principianti ed esperti). 
            Devi scegliere le distribuzioni ESCLUSIVAMENTE da questa lista JSON:
            [
              {"nome": "CachyOS", "base": "Arch", "architetture": "x86_64, x86-64-v3", "nvidia_nativo": "Si (ISO dedicata)", "categoria": "Desktop, Performance, x86-64-v3 optimized"},
              {"nome": "Linux Mint", "base": "Ubuntu/Debian", "architetture": "i686, x86_64", "nvidia_nativo": "Si (Driver Manager eccellente)", "categoria": "Beginners, Desktop"},
              {"nome": "MX Linux", "base": "Debian/antiX", "architetture": "armhf, i686, x86_64", "nvidia_nativo": "Si (Versione AHS)", "categoria": "Desktop, Old Computers"},
              {"nome": "Pop!_OS", "base": "Ubuntu", "architetture": "aarch64, x86_64", "nvidia_nativo": "Si (ISO dedicata NVIDIA)", "categoria": "Desktop, Gaming"},
              {"nome": "Debian", "base": "Indipendente", "architetture": "Tutte", "nvidia_nativo": "No (Richiede configurazione manuale repo non-free)", "categoria": "Server, Desktop stabile"},
              {"nome": "Zorin OS", "base": "Ubuntu", "architetture": "x86_64", "nvidia_nativo": "Si (Opzione in fase di boot)", "categoria": "Beginners, Windows-like"},
              {"nome": "EndeavourOS", "base": "Arch", "architetture": "x86_64, ARM", "nvidia_nativo": "Si (Opzione in fase di boot)", "categoria": "Desktop, Intermediate"},
              {"nome": "Fedora", "base": "Indipendente", "architetture": "x86_64, aarch64", "nvidia_nativo": "No (Richiede RPM Fusion/Click in app store)", "categoria": "Desktop, Developers, SELinux by default"},
              {"nome": "Manjaro", "base": "Arch", "architetture": "x86_64, aarch64", "nvidia_nativo": "Si (Hardware detection al boot)", "categoria": "Desktop, Gaming"},
              {"nome": "Ubuntu", "base": "Debian", "architetture": "x86_64, ARM", "nvidia_nativo": "Si (Opzione in fase di installazione)", "categoria": "Desktop, Beginners, Server, AppArmor by default"},
              {"nome": "Bazzite", "base": "Fedora Kinoite", "architetture": "x86_64", "nvidia_nativo": "Si (Immagine dedicata NVIDIA)", "categoria": "Gaming, Immutable, Steam Deck alternative"},
              {"nome": "Arch Linux", "base": "Indipendente", "architetture": "x86_64", "nvidia_nativo": "No (Installazione manuale driver)", "categoria": "Advanced, DIY"},
              {"nome": "openSUSE Tumbleweed", "base": "Indipendente", "architetture": "x86_64, aarch64", "nvidia_nativo": "No (Richiede aggiunta repository)", "categoria": "Desktop, Developers, Rolling"},
              {"nome": "Nobara", "base": "Fedora", "architetture": "x86_64", "nvidia_nativo": "Si (ISO dedicata NVIDIA)", "categoria": "Gaming, Content Creation"},
              {"nome": "antiX", "base": "Debian", "architetture": "i686, x86_64", "nvidia_nativo": "Limitato", "categoria": "Old Computers, Lightweight, No Systemd (SysVinit/runit)"},
              {"nome": "elementary OS", "base": "Ubuntu", "architetture": "x86_64", "nvidia_nativo": "Si (Tramite AppCenter)", "categoria": "Desktop, macOS-like"},
              {"nome": "NixOS", "base": "Indipendente", "architetture": "x86_64, aarch64", "nvidia_nativo": "Si (Tramite file configuration.nix)", "categoria": "Advanced, Reproducible, Declarative"},
              {"nome": "Kali Linux", "base": "Debian", "architetture": "x86_64, ARM", "nvidia_nativo": "Si", "categoria": "Cybersecurity, Pentesting"},
              {"nome": "Void Linux", "base": "Indipendente", "architetture": "x86_64, i686, ARM", "nvidia_nativo": "No (Manuale)", "categoria": "Advanced, Lightweight, Runit init"},
              {"nome": "Alpine Linux", "base": "Indipendente", "architetture": "Tutte", "nvidia_nativo": "No (Manuale)", "categoria": "Containers, Server, Minimal, musl/busybox"},
              {"nome": "AlmaLinux", "base": "RHEL", "architetture": "x86_64, aarch64", "nvidia_nativo": "No (Tramite EPEL/ELRepo)", "categoria": "Server, Enterprise"},
              {"nome": "Tails", "base": "Debian", "architetture": "x86_64", "nvidia_nativo": "No (Focus su driver open source/Nouveau per privacy)", "categoria": "Privacy, Live Medium, Tor by default"},
              {"nome": "Kubuntu", "base": "Ubuntu", "architetture": "x86_64", "nvidia_nativo": "Si", "categoria": "Desktop, KDE"},
              {"nome": "Parrot OS", "base": "Debian", "architetture": "x86_64, ARM", "nvidia_nativo": "Si", "categoria": "Cybersecurity, Privacy"},
              {"nome": "Gentoo", "base": "Indipendente", "architetture": "Tutte", "nvidia_nativo": "No (Da compilare)", "categoria": "Advanced, DIY, Source-based, OpenRC support"},
              {"nome": "Raspberry Pi OS", "base": "Debian", "architetture": "armhf, aarch64", "nvidia_nativo": "N/A", "categoria": "Raspberry Pi, SBC"}
            ]

            REGOLE DI SELEZIONE:
            1. Per i PRINCIPIANTI: privilegia Mint, Ubuntu, Zorin, Pop!_OS.
            2. Per gli ESPERTI: considera Arch, NixOS, Gentoo, Void.
            3. Considera sempre l'hardware (NVIDIA, architettura) e i requisiti tecnici (Init system, hardening, kernel RT, x86-64-v3).

            Rispondi SEMPRE in formato JSON:
            {
              "vincitore": "Nome Distro dalla lista",
              "motivo": "Spiegazione in italiano dettagliata ed amichevole",
              "alternative": [
                {"nome": "Alternativa 1", "descrizione": "Perché sceglierla"},
                {"nome": "Alternativa 2", "descrizione": "Perché sceglierla"}
              ]
            }
        """.trimIndent()  //  trimm rimuove spazio
    }

    // costruisce prompt utente: prende rispsote, fa zip chiaveValore, torna 
    private fun costruisciPrompt(): String {
        val risposte = _risposteUtente.value ?: emptyList()
        val testoRisposte = domande.zip(risposte).joinToString("\n") { (d, r) -> "- ${d.testo}: $r" }
        return "Ecco le risposte dell'utente al quiz di 10 domande:\n$testoRisposte\nScegli la distro ideale."
    }

//reset viewModel quiz 
    fun reset() {
        _indiceDomandaCorrente.value = 0
        _risposteUtente.value = mutableListOf()
        _risultato.value = null
        _errore.value = null
    }
}
