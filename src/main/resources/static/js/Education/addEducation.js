function addEducation() {

    const container = document.getElementById("educations_container");
    const i = container.querySelectorAll('.div_education').length;

    const newDiv = document.createElement('div');
    newDiv.classList.add('div_education');

    newDiv.innerHTML = `
        <label>Title</label>
        <input class="form-control" type="text" name="educations[${i}].title">

        <label class="mt-2">Start Month</label>
        <input class="form-control" type="number" name="educations[${i}].startMonth">

        <label class="mt-2">Start Year</label>
        <input class="form-control" type="number" name="educations[${i}].startYear">

        <label class="mt-2">End Month</label>
        <input class="form-control end-month" type="number" name="educations[${i}].endMonth">

        <label class="mt-2">End Year</label>
        <input class="form-control end-year" type="number" name="educations[${i}].endYear">

        <div class="form-check mt-2">
            <input class="form-check-input" type="checkbox"
                   name="educations[${i}].inProgress"
                   onchange="toggleEndDateEducationFields(this)">
            <label class="form-check-label">In corso</label>
        </div>

        <label class="mt-2">Descrizione titolo di studi</label>
        <textarea class="form-control" name="educations[${i}].description"></textarea>

        <label class="mt-2">Upload certificate</label>
        <input class="form-control" type="text" name="educations[${i}].imageCertificateUrl">

        <label class="mt-2">Enter link</label>
        <input class="form-control" type="text" name="educations[${i}].link">

        <button type="button" class="btn btn-sm btn-danger mt-3"
                onclick="removeEducation(this)">Rimuovi ISTRUZIONE</button>
    `;

    container.appendChild(newDiv);

    updateEducationIndexes();
}