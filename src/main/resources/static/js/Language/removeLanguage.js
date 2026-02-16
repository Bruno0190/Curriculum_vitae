function removeLanguage(button) {
    const div = button.parentElement;
    div.remove();
    updateLanguageIndexes();
}