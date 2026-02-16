function updateJobIndexes(experienceDiv, i) {

    const jobDivs = experienceDiv.querySelectorAll('.div_job');

    for (let j = 0; j < jobDivs.length; j++) {

        const div = jobDivs[j];

        div.querySelector('[name$="jobTitle"]').name =
            `experiences[${i}].jobs[${j}].jobTitle`;

        div.querySelector('[name$="startMonth"]').name =
            `experiences[${i}].jobs[${j}].startMonth`;

        div.querySelector('[name$="startYear"]').name =
            `experiences[${i}].jobs[${j}].startYear`;

        div.querySelector('[name$="endMonth"]').name =
            `experiences[${i}].jobs[${j}].endMonth`;

        div.querySelector('[name$="endYear"]').name =
            `experiences[${i}].jobs[${j}].endYear`;

        div.querySelector('[name$="inProgress"]').name =
            `experiences[${i}].jobs[${j}].inProgress`;

        // Aggiorno i Task dentro questo Job
        updateTaskIndexes(div, i, j);
    }
}
