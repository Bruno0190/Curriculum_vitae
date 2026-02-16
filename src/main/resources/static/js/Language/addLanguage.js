function addLanguage() {

    const container = document.querySelector(".language_container");
    const i = container.querySelectorAll('.div_language').length;

    const newDiv = document.createElement('div');
    newDiv.classList.add('div_language');

    newDiv.innerHTML = `
        <label>Lingua</label>
        <input class="form-control" type="text"
               name="languages[${i}].language_name">

        <label class="mt-2">Livello</label>
        <input class="form-control" type="text"
               name="languages[${i}].proficiency_level">

        <label class="mt-2">Scuola</label>
        <input class="form-control" type="text"
               name="languages[${i}].school">

        <button type="button" class="btn btn-sm btn-danger mt-2"
                onclick="removeLanguage(this)">Rimuovi LINGUA</button>
    `;

    container.appendChild(newDiv);

    updateLanguageIndexes();
}