function toggleEndDateJobFields(checkbox) {
    const jobDiv = checkbox.closest('.div_job');
    const endMonth = jobDiv.querySelector('.end-month');
    const endYear = jobDiv.querySelector('.end-year');
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
