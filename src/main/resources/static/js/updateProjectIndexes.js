function updateProjectIndexes() {

    const projectDivs = document.querySelectorAll('#projects_container .div_project');

    for (let i = 0; i < projectDivs.length; i++) {

        const div = projectDivs[i];

        const inputs = div.querySelectorAll('input, textarea');

        // Riscrivi i name in base al nuovo indice
        inputs[0].name = `projects[${i}].projectName`;
        inputs[1].name = `projects[${i}].description`;
        inputs[2].name = `projects[${i}].url`;
    }
}
