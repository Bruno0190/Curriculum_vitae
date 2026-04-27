function updateEducationIndexes() {

    const educationDivs = document.querySelectorAll('#educations_container .div_education');

    for (let i = 0; i < educationDivs.length; i++) {

        const div = educationDivs[i];

        const title = div.querySelector('input[name*=".title"]');
        const startMonth = div.querySelector('input[name*=".startMonth"]');
        const startYear = div.querySelector('input[name*=".startYear"]');
        const endMonth = div.querySelector('input.end-month');
        const endYear = div.querySelector('input.end-year');
        const inProgress = div.querySelector('input.in-progress-checkbox, input[type="checkbox"][name*=".inProgress"]');
        const description = div.querySelector('textarea[name*=".description"]');
        const certificateUrl = div.querySelector('input.certificate_url_input');
        const link = div.querySelector('input[name*=".link"]');
        const certificateFile = div.querySelector('input.certificate_image_input[type="file"]');

        if (title) title.name = `educations[${i}].title`;
        if (startMonth) startMonth.name = `educations[${i}].startMonth`;
        if (startYear) startYear.name = `educations[${i}].startYear`;
        if (endMonth) endMonth.name = `educations[${i}].endMonth`;
        if (endYear) endYear.name = `educations[${i}].endYear`;
        if (inProgress) inProgress.name = `educations[${i}].inProgress`;
        if (description) description.name = `educations[${i}].description`;
        if (certificateUrl) certificateUrl.name = `educations[${i}].image_certificate_Url`;
        if (link) link.name = `educations[${i}].link`;

        // File inputs must keep this flat name because backend reads MultipartFile[] certificates_files.
        if (certificateFile) certificateFile.name = 'certificates_files';
    }
}