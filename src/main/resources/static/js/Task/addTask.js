function addTask(button) {

    // 1. Trovo il Job corretto
    const jobDiv = button.closest('.div_job');

    // 2. Trovo il suo tasks_container
    const tasksContainer = jobDiv.querySelector('.tasks_container');

    // 3. Calcolo l'indice k
    const k = tasksContainer.querySelectorAll('.div_task').length;

    // 4. Creo il nuovo task
    const newTaskDiv = document.createElement('div');
    newTaskDiv.classList.add('div_task');

    newTaskDiv.innerHTML = `
        <label>Specifica della mansione</label>
        <input class="form-control" type="text"
               name="jobTask[${k}].task">

        <label class="mt-2">Descrizione della mansione</label>
        <input class="form-control" type="text"
               name="jobTask[${k}].task_description">

        <button type="button" class="btn btn-sm btn-danger mt-1"
                onclick="removeTask(this)">Rimuovi</button>
    `;

    // 5. Append del nuovo task
    tasksContainer.appendChild(newTaskDiv);

    // 6. Aggiorno tutti gli indici
    updateExperienceIndexes();
}