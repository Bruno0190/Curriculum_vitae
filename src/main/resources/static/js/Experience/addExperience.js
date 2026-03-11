function addExperience() {

    const experienceContainer = document.getElementById("experiences_container");
    const i = experienceContainer.querySelectorAll('.div_experience').length;

    const newExperienceDiv = document.createElement('div');
    newExperienceDiv.classList.add('div_experience');

    newExperienceDiv.innerHTML = `
        <label>Company Name</label>
        <input class="form-control" style="border:2px solid #222;" type="text" name="experiences[${i}].companyName">

        <label class="mt-2">Company Description</label>
        <textarea class="form-control" style="border:2px solid #222;" name="experiences[${i}].company_description"></textarea>

        <label class="mt-2">Start Month</label>
            <input class="form-control" style="border:2px solid #222;" type="number" name="experiences[${i}].startMonth">

        <label class="mt-2">Start Year</label>
            <input class="form-control" style="border:2px solid #222;" type="number" name="experiences[${i}].startYear">

        <label class="mt-2">End Month</label>
        <input class="form-control end-month" style="border:2px solid #222;" type="number" name="experiences[${i}].endMonth">

        <label class="mt-2">End Year</label>
        <input class="form-control end-year" style="border:2px solid #222;" type="number" name="experiences[${i}].endYear">

        <div class="form-check mt-2">
            <input class="form-check-input" type="checkbox" name="experiences[${i}].inProgress">
            <label class="form-check-label">In Progress</label>
        </div>

        <section class="mt-3 ms-3 p-2" style="background-color:rgb(235, 235, 234); border-radius: 5px; box-shadow: 2px 6px 18px rgba(0,0,0,0.18);">
            <h4 style="font-size: 20px;color:rgb(15, 15, 237);">ROLE</h4>

            <div class="jobs_container">

                <div class="div_job">

                    <label>Role</label>
                    <input class="form-control" style="border:2px solid #222;" type="text" name="experiences[${i}].jobs[0].jobTitle" placeholder="e.g. Project Manager">

                    <label class="mt-2">Start Month</label>
                        <input class="form-control" style="border:2px solid #222;" type="number" name="experiences[${i}].jobs[0].startMonth">

                    <label class="mt-2">Start Year</label>
                        <input class="form-control" style="border:2px solid #222;" type="number" name="experiences[${i}].jobs[0].startYear">

                    <label class="mt-2">End Month</label>
                    <input class="form-control end-month" style="border:2px solid #222;" type="number" name="experiences[${i}].jobs[0].endMonth">

                    <label class="mt-2">End Year</label>
                    <input class="form-control end-year" style="border:2px solid #222;" type="number" name="experiences[${i}].jobs[0].endYear">

                    <div class="form-check mt-2">
                        <input class="form-check-input" type="checkbox" name="experiences[${i}].jobs[0].inProgress">
                        <label class="form-check-label">In Progress</label>
                    </div>

                    <section class="mt-3 ms-3 p-2" style="background-color:rgb(175, 176, 161); border-radius: 5px;box-shadow: 2px 6px 18px rgba(0,0,0,0.18);">
                        <h4 style="font-size: 20px;color:rgb(15, 15, 237);">TASKS</h4>

                        <div class="tasks_container">

                            <div class="div_task">
                                <label>Task Specification</label>
                                    <input class="form-control" style="border:2px solid #222;" type="text" name="experiences[${i}].jobs[0].jobTask[0].task" placeholder="e.g.Governance & Compliance">

                                <label class="mt-2">Task Description</label>
                                <input class="form-control" style="border:2px solid #222;" type="text" name="experiences[${i}].jobs[0].jobTask[0].task_description" placeholder="e.g. Coordinated requirements gathering, risk assessment, and stakeholder communication.">

                                <button type="button" class="btn btn-sm btn-danger mt-1"
                                onclick="removeTask(this)">Remove Task</button>
                            </div>

                        </div>

                        <button type="button" class="btn btn-sm btn-primary mt-2" onclick="addTask(this)">Add Task</button>
                    </section>

                    <button type="button" class="btn btn-sm btn-danger mt-1" onclick="removeJob(this)">Remove Role</button>

                </div>

            </div>

            <button type="button" class="btn btn-sm btn-primary mt-2" onclick="addJob(this)">Add Role</button>
        </section>

        <button type="button" class="btn btn-sm btn-danger mt-3" onclick="removeExperience(this)">Remove Experience</button>
    `;

    experienceContainer.appendChild(newExperienceDiv);

    updateExperienceIndexes();
}