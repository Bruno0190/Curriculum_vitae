function removeTask(button) {
    const taskDiv = button.parentElement;
    taskDiv.remove();
    updateExperienceIndexes();
}