function dragAndDropFile(event, inputId) {

    event.preventDefault();

    const file = event.dataTransfer.files[0];

    const input = document.getElementById(`profile_image_area`);
    input.files = event.dataTransfer.files;

    const files = event.dataTransfer.files;

    const filesCertificateInput = document.querySelector('certificate_image_area');


}



