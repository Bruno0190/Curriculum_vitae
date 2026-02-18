let profileInput = document.getElementById(`profile_image_area`);

let certificateInputs = document.querySelectorAll('.certificate_image_input');

function dragAndDropFile(event, inputElement) {

    event.preventDefault();

    let files = event.dataTransfer.files;

    if (files.length > 0) {
        
        let file = files[0]
        let uploaded = new DataTransfer();
        uploaded.items.add(file);
        inputElement.files = uploaded.files;

        if (inputElement === profileInput) {

            let urlInput = document.getElementById('profileImageUrl');
            uploadFile(file, "profiles", urlInput);
            return;
        } else if (inputElement.classList.contains('certificate_image_input')) {

            let urlInput = inputElement.parentElement.querySelector('.certificate_url_input');
            uploadFile(file, "certificates", urlInput);
            return;
        }

    }

}

profileInput.addEventListener("dragover", event => event.preventDefault());
profileInput.addEventListener("drop", event => dragAndDropFile(event, profileInput));

certificateInputs.forEach(input => {
    input.addEventListener("dragover", event => event.preventDefault());
    input.addEventListener("drop", event => dragAndDropFile(event, input));
});
    
async function uploadFile(file, folder, urlInput) {
    const formData = new FormData();
    formData.append("file", file);

    try {
        const response = await fetch(`/upload/${folder}`, {
            method: "POST",
            body: formData
        });
    
        if (response.ok) {
            const url = await response.text();
            urlInput.value = url;
        }
    } catch (error) {
        console.error("Error uploading file:", error);
    }
} 

