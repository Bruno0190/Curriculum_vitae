function addExperience() {

    const experienceContainer = document.getElementById("experiences_container");
    const i = experienceContainer.querySelectorAll('.div_experience').length;

    const newExperienceDiv = document.createElement('div');
    newExperienceDiv.classList.add('div_experience');

    newExperienceDiv.innerHTML = `
        <label>Company Name</label>
        <input class="form-control" type="text" name="experiences[${i}].companyName">

        <label class="mt-2">Company Description</label>
        <textarea class="form-control" name="experiences[${i}].company_description"></textarea>

        <label class="mt-2">Start Month</label>
        <input class="form-control" type="number" name="experiences[${i}].startMonth">

        <label class="mt-2">Start Year</label>
        <input class="form-control" type="number" name="experiences[${i}].startYear">

        <label class="mt-2">End Month</label>
        <input class="form-control end-month" type="number" name="experiences[${i}].endMonth">

        <label class="mt-2">End Year</label>
        <input class="form-control end-year" type="number" name="experiences[${i}].endYear">

        <div class="form-check mt-2">
            <input class="form-check-input" type="checkbox" name="experiences[${i}].inProgress">
            <label class="form-check-label">In Progress</label>
        </div>

        <section class="mt-3">
            <h4 style="font-size: 20px;color:cornflowerblue;">RUOLI</h4>

            <div class="jobs_container">

                <div class="div_job">

                    <label>Role</label>
                    <input class="form-control" type="text" name="experiences[${i}].jobs[0].jobTitle">

                    <label class="mt-2">Start Month</label>
                    <input class="form-control" type="number" name="experiences[${i}].jobs[0].startMonth">

                    <label class="mt-2">Start Year</label>
                    <input class="form-control" type="number" name="experiences[${i}].jobs[0].startYear">

                    <label class="mt-2">End Month</label>
                    <input class="form-control end-month" type="number" name="experiences[${i}].jobs[0].endMonth">

                    <label class="mt-2">End Year</label>
                    <input class="form-control end-year" type="number" name="experiences[${i}].jobs[0].endYear">

                    <div class="form-check mt-2">
                        <input class="form-check-input" type="checkbox" name="experiences[${i}].jobs[0].inProgress">
                        <label class="form-check-label">In Progress</label>
                    </div>

                    <section class="mt-3">
                        <h4>MANSIONI</h4>

                        <div class="tasks_container">

                            <div class="div_task">
                                <label>Specifica della mansione</label>
                                <input class="form-control" type="text" name="experiences[${i}].jobs[0].jobTask[0].task">

                                <label class="mt-2">Descrizione della mansione</label>
                                <input class="form-control" type="text" name="experiences[${i}].jobs[0].jobTask[0].task_description">

                                <button type="button" class="btn btn-sm btn-danger mt-1"
                                onclick="removeTask(this)">Rimuovi</button>
                            </div>

                        </div>

                        <button type="button" class="btn btn-sm btn-primary mt-2" onclick="addTask(this)">Aggiungi MANSIONE</button>
                    </section>

                    <button type="button" class="btn btn-sm btn-danger mt-1" onclick="removeJob(this)">Rimuovi RUOLO</button>

                </div>

            </div>

            <button type="button" class="btn btn-sm btn-primary mt-2" onclick="addJob(this)">Aggiungi RUOLO</button>
        </section>

        <button type="button" class="btn btn-sm btn-danger mt-3" onclick="removeExperience(this)">Rimuovi ESPERIENZA</button>
    `;

    experienceContainer.appendChild(newExperienceDiv);

    updateExperienceIndexes();
}