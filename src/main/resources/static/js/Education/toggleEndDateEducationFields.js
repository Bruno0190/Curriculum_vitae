function toggleEndDateEducationFields(checkbox) {
    const educationDiv = checkbox.closest('.div_education');

    const endMonth = educationDiv.querySelector('.end-month');
    const endYear = educationDiv.querySelector('.end-year');
    if (checkbox.checked) {
        endMonth.value = "";
        endYear.value = "";
        endMonth.disabled = true;
        endYear.disabled = true;
    } else {
        endMonth.disabled = false;
        endYear.disabled = false;
    }
}