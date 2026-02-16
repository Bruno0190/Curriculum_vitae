function removeJob(button) {
    const jobDiv = button.parentElement;
    jobDiv.remove();
    updateExperienceIndexes();
}