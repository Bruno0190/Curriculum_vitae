function removeExperience(button) {
    const experienceDiv = button.parentElement;
    experienceDiv.remove();
    updateExperienceIndexes();
}