function removeProject(button) {

    const projectDiv = button.parentElement;

    projectDiv.remove();
    updateProjectIndexes();
    
}