function updateEducationIndexes() {

    const educationDivs = document.querySelectorAll('#educations_container .div_education');

    for (let i = 0; i < educationDivs.length; i++) {

        const div = educationDivs[i];
        const inputs = div.querySelectorAll('input, textarea');

        inputs[0].name = `educations[${i}].title`;
        inputs[1].name = `educations[${i}].startMonth`;
        inputs[2].name = `educations[${i}].startYear`;
        inputs[3].name = `educations[${i}].endMonth`;
        inputs[4].name = `educations[${i}].endYear`;
        inputs[5].name = `educations[${i}].inProgress`;
        inputs[6].name = `educations[${i}].description`;
        inputs[7].name = `educations[${i}].imageCertificateUrl`;
        inputs[8].name = `educations[${i}].link`;
    }
}