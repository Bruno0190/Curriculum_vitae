function toggleEndDateExperienceFields(checkbox) {
    const experienceDiv = checkbox.closest('.div_experience');

    const endMonth = experienceDiv.querySelector('.end-month');
    const endYear = experienceDiv.querySelector('.end-year');

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