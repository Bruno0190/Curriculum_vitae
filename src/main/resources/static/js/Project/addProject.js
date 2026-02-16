function addProject() {
    const projectContainer = document.getElementById("projects_container");

    const currentCount = projectContainer.querySelectorAll('.div_project').length;
    const newProjectDiv = document.createElement('div');
    newProjectDiv.classList.add('div_project');

    newProjectDiv.innerHTML = `<input class="form-control" type="text" placeholder="Project Name" name="projects[${currentCount}].projectName">  
    <textarea class="form-control mt-2" placeholder="Project Description" name="projects[${currentCount}].description"></textarea> 
    <input class="form-control mt-2" type="text" placeholder="Project Link" name="projects[${currentCount}].url"> 
    <button type="button" class="btn btn-sm btn-danger mt-1" onclick="removeProject(this)">Rimuovi</button>`;

    projectContainer.appendChild(newProjectDiv);

    updateProjectIndexes();
}