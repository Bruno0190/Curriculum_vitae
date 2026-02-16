function updateExperienceIndexes() {

    const experienceDivs = document.querySelectorAll('#experiences_container .div_experience');

    for (let i = 0; i < experienceDivs.length; i++) {

        const div = experienceDivs[i];

        div.querySelector('[name$="companyName"]').name =
            `experiences[${i}].companyName`;

        div.querySelector('[name$="company_description"]').name =
            `experiences[${i}].company_description`;

        div.querySelector('[name$="startMonth"]').name =
            `experiences[${i}].startMonth`;

        div.querySelector('[name$="startYear"]').name =
            `experiences[${i}].startYear`;

        div.querySelector('[name$="endMonth"]').name =
            `experiences[${i}].endMonth`;

        div.querySelector('[name$="endYear"]').name =
            `experiences[${i}].endYear`;

        div.querySelector('[name$="inProgress"]').name =
            `experiences[${i}].inProgress`;

        // Aggiorno i Job dentro questa Experience
        updateJobIndexes(div, i);
    }
}