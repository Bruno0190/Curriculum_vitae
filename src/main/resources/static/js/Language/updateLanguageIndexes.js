function updateLanguageIndexes() {

    const languageDivs = document.querySelectorAll('.language_container .div_language');

    for (let i = 0; i < languageDivs.length; i++) {

        const div = languageDivs[i];
        const inputs = div.querySelectorAll('input');

        inputs[0].name = `languages[${i}].language_name`;
        inputs[1].name = `languages[${i}].proficiency_level`;
        inputs[2].name = `languages[${i}].school`;
    }
}