let speechRecognition = window.webkitSpeechRecognition
let isStaarted = false
recognition = new speechRecognition()

function recognitionStart() {
    const searchText = document.querySelector('#search-text')
    const voiceText = document.querySelector('.voice-text')
    const voiceIcon = document.querySelector('.voice-icon')
    const searchsubmit = document.querySelector('.icon')


    let content = ""

    recognition.addEventListener('result', (e) => {
        var result = e.results[0][0].transcript;
        words = result.split(" ")
        searchText.value = words[0]
        searchsubmit.click()

    })


    recognition.addEventListener('start', () => {
        console.log("Started")
        voiceText.innerHTML = "Voice Recognition is on..."
        voiceText.classList.toggle("working")
    })

    recognition.addEventListener('end', () => {
        console.log("Stopped")
        voiceText.innerHTML = "Press icon to start voice search."
        voiceText.classList.toggle("working")
        isStaarted = false
    })

    voiceIcon.addEventListener('click', (e) => {
        if (!isStaarted) {
            recognition.start()
            isStaarted = true
        }
        else {
            recognition.stop()
        }
    })
}