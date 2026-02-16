function removeEducation(button) {
    const div = button.parentElement;
    div.remove();
    updateEducationIndexes();
}