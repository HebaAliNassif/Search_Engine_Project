


window.onload = () => {
    recognitionStart()
    let sug_list = []
    const searchBox = document.getElementsByClassName("search-text")[0]
    const suggest = document.querySelector(".suggestions")
    const submit = document.getElementsByClassName("icon")[0]
    const suggestList = document.querySelector('.suggest-list').innerHTML
    sug_list = suggestList.split(" ")

    searchBox.addEventListener("keyup", (e) => {
        e.preventDefault()
        let str = searchBox.value.toLowerCase()
        suggest.innerHTML = ''
        const suggestions = sug_list.filter((sug) => {
            return sug.toLowerCase().startsWith(str)
        })
        console.log(str.length)
        if (str.length != 0) {
            suggestions.forEach(element => {
                const div = document.createElement('div')
                div.innerHTML = element
                suggest.appendChild(div)
                div.addEventListener("click", () => {
                    console.log('clicked')
                    searchBox.value = div.innerHTML
                    suggest.style.display = "none"
                    submit.click()
                })
            });
        }
    })
}


// searchBox.addEventListener('keydown', () => {
//     console.log(searchBox.value)
// })