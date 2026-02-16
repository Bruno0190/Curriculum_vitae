function updateTaskIndexes(jobDiv, i, j) {

    const taskDivs = jobDiv.querySelectorAll('.div_task');

    for (let k = 0; k < taskDivs.length; k++) {

        const div = taskDivs[k];

        div.querySelector('[name$="task"]').name =
            `experiences[${i}].jobs[${j}].jobTask[${k}].task`;

        div.querySelector('[name$="task_description"]').name =
            `experiences[${i}].jobs[${j}].jobTask[${k}].task_description`;
    }
}