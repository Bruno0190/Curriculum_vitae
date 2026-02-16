function addJob(button) {

    // 1. Trovo la Experience corretta
    const experienceDiv = button.closest('.div_experience');

    // 2. Trovo il suo jobs_container
    const jobsContainer = experienceDiv.querySelector('.jobs_container');

    // 3. Calcolo l'indice j
    const j = jobsContainer.querySelectorAll('.div_job').length;

    // 4. Creo il nuovo job
    const newJobDiv = document.createElement('div');
    newJobDiv.classList.add('div_job');

    newJobDiv.innerHTML = `
        <label>Role</label>
        <input class="form-control" type="text"
               name="jobs[${j}].jobTitle">

        <label class="mt-2">Start Month</label>
        <input class="form-control" type="number"
               name="jobs[${j}].startMonth">

        <label class="mt-2">Start Year</label>
        <input class="form-control" type="number"
               name="jobs[${j}].startYear">

        <label class="mt-2">End Month</label>
        <input class="form-control end-month" type="number"
               name="jobs[${j}].endMonth">

        <label class="mt-2">End Year</label>
        <input class="form-control end-year" type="number"
               name="jobs[${j}].endYear">

        <div class="form-check mt-2">
            <input class="form-check-input" type="checkbox"
                   name="jobs[${j}].inProgress">
            <label class="form-check-label">In Progress</label>
        </div>

        <section class="mt-3">
            <h4>MANSIONI</h4>

            <div class="tasks_container">

                <div class="div_task">
                    <label>Specifica della mansione</label>
                    <input class="form-control" type="text"
                           name="jobs[${j}].jobTask[0].task">

                    <label class="mt-2">Descrizione della mansione</label>
                    <input class="form-control" type="text"
                           name="jobs[${j}].jobTask[0].task_description">

                    <button type="button" class="btn btn-sm btn-danger mt-1"
                            onclick="removeTask(this)">Rimuovi</button>
                </div>

            </div>

            <button type="button" class="btn btn-sm btn-primary mt-2"
                    onclick="addTask(this)">Aggiungi MANSIONE</button>
        </section>

        <button type="button" class="btn btn-sm btn-danger mt-1"
                onclick="removeJob(this)">Rimuovi RUOLO</button>
    `;

    // 5. Append del nuovo job
    jobsContainer.appendChild(newJobDiv);

    // 6. Aggiorno tutti gli indici
    updateExperienceIndexes();
}